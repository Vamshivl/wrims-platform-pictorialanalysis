package com.vassarlabs.pictorialanalysis.service.impl;

import com.google.gson.Gson;
import com.vassarlabs.boundarydetection.pojo.api.IModelData;
import com.vassarlabs.boundarydetection.pojo.impl.ModelInfo;
import com.vassarlabs.boundarydetection.service.api.IModelDataService;
import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.utils.DateUtils;
import com.vassarlabs.common.utils.StringUtils;
import com.vassarlabs.common.utils.err.InvalidDataFoundException;
import com.vassarlabs.config.err.PropertyNotFoundException;
import com.vassarlabs.pictorialanalysis.dsp.api.ILabeledImageDSP;
import com.vassarlabs.pictorialanalysis.pojo.api.IFilterParams;
import com.vassarlabs.pictorialanalysis.pojo.api.IImageBbox;
import com.vassarlabs.pictorialanalysis.pojo.api.IPATaskInputData;
import com.vassarlabs.pictorialanalysis.pojo.api.IRawImage;
import com.vassarlabs.pictorialanalysis.pojo.api.ITrainingParams;
import com.vassarlabs.pictorialanalysis.pojo.impl.FilterParams;
import com.vassarlabs.pictorialanalysis.pojo.impl.ImageBbox;
import com.vassarlabs.pictorialanalysis.pojo.impl.PATaskInputData;
import com.vassarlabs.pictorialanalysis.service.api.ILabeledImageDataService;
import com.vassarlabs.pictorialanalysis.service.api.IModelTrainingService;
import com.vassarlabs.pictorialanalysis.service.api.IRawImageDataService;
import com.vassarlabs.pictorialanalysis.utils.PAConstantProperties;
import com.vassarlabs.pictorialanalysis.utils.PAUtils;
import com.vassarlabs.taskmngr.pojo.api.ITaskInfo;
import com.vassarlabs.taskmngr.pojo.impl.TaskInfo;
import com.vassarlabs.taskmngr.service.api.ITaskManagementService;
import com.vassarlabs.taskmngr.utils.TaskManagementConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ModelTrainingService implements IModelTrainingService {

    @Autowired
    protected ITaskManagementService taskManagementService;

    @Autowired
    protected IModelDataService modelDataService;

    @Autowired
    protected ILabeledImageDataService labeledImageDataService;

    @Autowired
    protected ILabeledImageDSP labeledImageDataDSP;

    @Autowired
    protected PAConstantProperties PAConstants;

    @Autowired
    protected IRawImageDataService rawImageDataService;

    private static Map<String, Integer> getImageDimensions(String imagePath) {

        try {

            File imageFile = new File(imagePath);
            BufferedImage image = ImageIO.read(imageFile);
            System.out.println(image);
            int width = image.getWidth();
            int height = image.getHeight();

            return Map.of("width", width,
                    "height", height);
        } catch (IOException e) {
            System.out.println("Unable to read image Dimensions : " + imagePath);
            e.printStackTrace();
            return null;
        }
    }

    private LinkedHashMap<String, String> getDistinctClassCodesForLabeledImages(LinkedHashMap<String, LinkedHashMap<String, String>> labeledImages) {
        LinkedHashMap<String, String> classMap = new LinkedHashMap<>();
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : labeledImages.entrySet()) {
            for (Map.Entry<String, String> en : entry.getValue().entrySet()) {
                classMap.put(en.getValue(), en.getKey());
            }
        }
        return classMap;
    }

    private void updateTaskStatus(ITaskInfo taskInfo, boolean taskStatus) {
        System.out.println("TrainingServiceImpl :: updateTaskStatus method");
        if (taskStatus) {
            taskInfo.setTaskEndTs(System.currentTimeMillis());
            taskInfo.setEventState(TaskManagementConstants.COMPLETED_STATE);
            taskInfo.setEventStatus(TaskManagementConstants.SUCCESS_STATUS);
        } else {
            taskInfo.setTaskEndTs(System.currentTimeMillis());
            taskInfo.setEventState(TaskManagementConstants.DISCARDED_STATE);
            taskInfo.setEventStatus(TaskManagementConstants.FAILED_STATUS);
        }

        try {
            taskManagementService.updateTaskInfo(taskInfo.getTaskUUID(), taskInfo);
        } catch (DSPException | InvalidDataFoundException e) {
            System.out.println("Unable to update task status :: taskUUID :: " + taskInfo.getTaskUUID());
            e.printStackTrace();
        }
    }

    private String getBboxImageLabelAnnotation(Map<String, String> BboxLabels) {

        String annotation = "";
        for (String BboxJson : BboxLabels.keySet()) {

            IImageBbox Bbox = new Gson().fromJson(BboxJson, ImageBbox.class);
            List<Integer> BboxDims = List.of((int) (double) Bbox.getBbox_X(), (int) (double) Bbox.getBbox_Y(), (int) (double) Bbox.getBbox_W(), (int) (double) Bbox.getBbox_H());
            annotation += (BboxDims.toString().replaceAll("[\\[\\]\\s]", "") + "," + BboxLabels.get(BboxJson)) + StringUtils.SPACE_STRING;

        }
        return annotation;

    }

    private boolean generateTrainingData(IFilterParams filterParams, String annotationsFilePath) throws FileNotFoundException, DSPException, PropertyNotFoundException {

        try {

            LinkedHashMap<String, LinkedHashMap<String, String>> imageBboxLabels = labeledImageDataService.getBboxLabelsForImages(filterParams.getStartDateTs(), filterParams.getEndDateTs(), filterParams.getLocUUID(), filterParams.getLocType(), filterParams.getAppUUID(), filterParams.getSource(), filterParams.getLabelType());
            Map<String, IRawImage> rawImagesData = rawImageDataService.getListOfImagesForUUIDs(new ArrayList<>(imageBboxLabels.keySet()));

            if(imageBboxLabels.isEmpty()){
                System.out.println("WARNING :: labled Images List is empty");
            }
            if(rawImagesData.isEmpty()){
                System.out.println("WARNING :: raw Images List is empty");
            }
            File annotationsFile = new File(annotationsFilePath);

            File parentDir = annotationsFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!annotationsFile.createNewFile()) {
                System.out.println("Annotations file with name < " + annotationsFile.getName() + " > Already Exists");
                return false;
            }
            System.out.println("Creating AnnotationsFile at > " + annotationsFilePath);

            BufferedWriter annotationsFileWriter = new BufferedWriter(
                    new FileWriter(annotationsFile));

            StringBuilder annotation_content = new StringBuilder();

            for (String imageUUID : imageBboxLabels.keySet()) {
                IRawImage image = rawImagesData.get(imageUUID);
                String imagePath = image.getImagePath();

                String imageDimStr = image.getImageDetails().getDimension();

                Map<String, String> imageBboxes = imageBboxLabels.get(imageUUID);
                String imageBboxeLabelsString = getBboxImageLabelAnnotation(imageBboxes);

                String Line = imagePath + StringUtils.SPACE_STRING + imageDimStr + StringUtils.SPACE_STRING + imageBboxeLabelsString + "\n";

                annotation_content.append(Line);

            }
            try {

                annotationsFileWriter.write(String.valueOf(annotation_content));
                System.out.println("FIle written");
            } catch (IOException e) {
                e.printStackTrace();
//                throw new RuntimeException(e);
            } finally {
                annotationsFileWriter.close();
            }
            return true;

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Annotations FIle not Found");
        } catch (DSPException | InvalidDataFoundException | IOException e) {
            e.printStackTrace();
            return false;
//            throw new RuntimeException(e);

        }
    }
    private String readFileContents(String filePath ) throws IOException {

        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder();
        if(!file.exists()){
            System.out.println("Trying to read fileContents : but file doesn't exits.\nfile : "+file.getAbsolutePath());
            return fileContent.toString();
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null){
            fileContent.append(line);
        }

        return fileContent.toString();
    }

    @Override
    public boolean trainPAModel(ITrainingParams trainingParams) throws InvalidDataFoundException {
        System.out.println("Train Params ::" + trainingParams);
        if (trainingParams.getUserUUID() == null || trainingParams.getAlgoName() == null || trainingParams.getAppUUID() == null || trainingParams.getLocUUID() == null
                || trainingParams.getLocType() == null || trainingParams.getStartDateTs() == null
                || trainingParams.getEndDateTs() == null) {

            throw new InvalidDataFoundException("Invalid Data for found, Can't proceed for Training \nfilter params , userUUID , algoName can't be null ");

        }
        FilterParams filterParams = getFilterParamsFromTrainingParams(trainingParams);
        String userUUID = trainingParams.getUserUUID();
        String modelName = trainingParams.getModelName();
        String reTrainOnModelUUID = trainingParams.getModelUUID();
        String algoName = trainingParams.getAlgoName();
        String existingTaskUUID = trainingParams.getExistingTaskUUID();
        String actionType = trainingParams.getActionType();

        String outputWeightFile = null;
        Long refTs = System.currentTimeMillis();
        int eventGenDay = DateUtils.getEventGenDay(refTs);

        ITaskInfo taskInfo = new TaskInfo();
        String taskUUID = "";
        setTaskInfo(taskInfo, taskUUID, userUUID, eventGenDay, refTs);

        try {

            String annotationsFilePath;

            // new task
            if (existingTaskUUID == null || existingTaskUUID.isEmpty()) {

                taskUUID = UUID.randomUUID().toString();
                System.out.println("taskUUID :: " + taskUUID);
                taskInfo.setTaskUUID(taskUUID);
                taskInfo = taskManagementService.addNewTaskInfo(taskInfo);

                annotationsFilePath = PAConstants.getANNOTATIONS_FILES_DIR() + modelName + StringUtils.UNDERSCORE_STRING + taskUUID + ".txt";

                boolean status = generateTrainingData(filterParams, annotationsFilePath);
                if (!status) {
                    return false;
                }
            // restarting existing task
            } else {
                taskUUID = existingTaskUUID;
                System.out.println("taskUUID :: " + taskUUID);
                taskInfo = taskManagementService.readTaskInfoByTaskUUID(taskUUID);
                taskInfo.setInsertTs(refTs);
                taskInfo.setEventState(TaskManagementConstants.RUNNING_STATE);
                taskInfo.setEventStatus(TaskManagementConstants.NO_STATUS);
                taskInfo = taskManagementService.updateTaskInfo(taskUUID, taskInfo);

                IPATaskInputData existingTaskInputData = new Gson().fromJson(taskInfo.getTaskInputDetails(), PATaskInputData.class);

                annotationsFilePath = existingTaskInputData.getAnnotationsFilePath();
                reTrainOnModelUUID = existingTaskInputData.getRetrainOnModel();

            }

            String modelUUID = UUID.randomUUID().toString();
            IPATaskInputData taskInputData = null;

            //training new model
            if (actionType.equals(PAUtils.PA_TRAINING_ACTION_TYPE)) {

                System.out.println("Training Preprocessing");

                String taskName = "Task_" + "PA_Train_" + DateUtils.getTimeStringFromTimeInMillis(System.currentTimeMillis());
                taskInputData = new PATaskInputData((FilterParams) filterParams, annotationsFilePath);
                taskInputData.setTaskName(taskName);
                System.out.println("taskInputData :: "+taskInputData);
                String outputDir = PAConstants.getTRAINING_OUTPUT_WEIGHT_FILE_DIR();
                outputWeightFile = outputDir + modelName + StringUtils.UNDERSCORE_STRING + algoName + StringUtils.UNDERSCORE_STRING + modelUUID + ".h5";

            //re-training existing model
            } else if(actionType.equals(PAUtils.PA_RETRAINING_ACTION_TYPE)) {

                if(reTrainOnModelUUID==null || reTrainOnModelUUID.isEmpty()){
                    System.out.println("ModelUUID is not present");
                    throw new InvalidDataFoundException("ModelUUID is not present");
                }
                System.out.println("Retraining Preprocessing");

                String taskName = "Task_" + "PA_Retrain_" + DateUtils.getTimeStringFromTimeInMillis(System.currentTimeMillis());
                taskInputData = new PATaskInputData((FilterParams) filterParams, annotationsFilePath , reTrainOnModelUUID);
                taskInputData.setTaskName(taskName);

                IModelData modelData = modelDataService.getModelDetailsForModelUUID(reTrainOnModelUUID);

                String trainedWeightFile = modelData.getModelFilePath();
                if (!(new File(trainedWeightFile).exists())) {
                    System.out.println("The weight file of the corresponding modelUUID <" + reTrainOnModelUUID + "> doesn't exists ");
                    throw new InvalidDataFoundException("The weight file of the corresponding modelUUID <" + reTrainOnModelUUID + "> doesn't exists " );
                }

                outputWeightFile = trainedWeightFile.replace(reTrainOnModelUUID , modelUUID);
                System.out.println("Trained Weight file : " + trainedWeightFile);
                System.out.println("Retraining Weight file : " + outputWeightFile);
                try{
                    Files.copy(Path.of(trainedWeightFile), Path.of(outputWeightFile), StandardCopyOption.REPLACE_EXISTING);
                }
                catch(IOException e){
                    System.out.println("Unable to create retrain Output weightFile");
                    e.printStackTrace();
                    return false;
                }
            }

            /** ***************** Calling Python Script **/

            //updating taskInputDetails
            taskInfo.setTaskInputDetails(PATaskInputData.convertTaskInputDataToJsonData(taskInputData));
            System.out.println("taskInfo ::"+taskInfo);
            taskManagementService.updateTaskInfo(taskUUID , taskInfo);

            File annotationsFile = new File(annotationsFilePath);

            LinkedHashMap<String, LinkedHashMap<String, String>> imageBboxLabels = labeledImageDataService.getBboxLabelsForImages(filterParams.getStartDateTs(), filterParams.getEndDateTs(), filterParams.getLocUUID(), filterParams.getLocType(), filterParams.getAppUUID(), filterParams.getSource(), filterParams.getLabelType());

//            System.out.println("imageBboxLabels :" + imageBboxLabels);
            List<String> classesList = new ArrayList<>(getDistinctClassCodesForLabeledImages(imageBboxLabels).keySet());
            classesList.add("bg");
            System.out.println("CLassList:" + classesList.toString().replaceAll("\\s", ""));

            try{
                executeTrainingScript(annotationsFile.getPath(), taskUUID, outputWeightFile, classesList);
            }catch (InterruptedException e){
                e.printStackTrace();
                throw new InterruptedException("Training Process got Interrupted");

            }

            ITaskInfo taskInfoFromPyScript = taskManagementService.readTaskInfoByTaskUUID(taskUUID);
            System.out.println("Python script task update status " + taskInfoFromPyScript);
            if (taskInfoFromPyScript.getEventStatus() != TaskManagementConstants.SUCCESS_STATUS) {
                System.out.println("Python script returned error :: Unable to train the model :: taskUUID :: " + taskUUID);
                return false;
            }

            //updating taskOutputDetails
            taskInfo.setTaskOutput(Map.of("modelUUID", modelUUID));
            taskManagementService.updateTaskInfo(taskUUID , taskInfo);

            String jsonFilePath = PAConstants.getTRAINING_OUTPUT_WEIGHT_FILE_DIR() + (new File(outputWeightFile).getName()).split("\\.")[0] + ".json";

            String confusionMatrixJson = readFileContents(jsonFilePath);
            ModelInfo modelInfo = new ModelInfo(new ArrayList<>(classesList), confusionMatrixJson);
            modelDataService.insertModelDataAndModelInfo(modelUUID,modelName, algoName, PAUtils.PA_COMPONENT_UUID, userUUID, outputWeightFile, reTrainOnModelUUID, modelInfo.toJson());
            updateTaskStatus(taskInfo, true);
            System.out.println("Training is done successfully :: taskUUID :: " + taskUUID);


        } catch (DSPException | InvalidDataFoundException | IOException | InterruptedException | PropertyNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void executeTrainingScript(String annotationsFilePath, String taskUUID, String outputWeightFile, List<String> classesList) throws IOException, DSPException, InvalidDataFoundException, InterruptedException {

        /**  python app.py 'PICTORIAL ANALYSIS' TRAINING C:/Users/VassarGIS/Downloads/faster-rcnn-keras/weights/new1.h5 C:/Users/VassarGIS/Downloads/faster-rcnn-keras/labels_annotations_desktop.txt "['wheat', 'mustard', 'maize','ground_nut', 'green_gram', 'black_gram', 'sugarcane', 'chilli', 'potato', 'turmeric', 'Paddy','harvested','ploughed', 'bg']" **/

        if (classesList == null || classesList.isEmpty() ||
                annotationsFilePath == null || !(new File(annotationsFilePath).exists())) {
            System.out.println("ERROR :: Invalid commandLine arguments for calling Training Script");
            throw new InvalidDataFoundException("Invalid commandLine arguments for calling Training Script");
        }
        File weightFile = new File(outputWeightFile);
        if(!weightFile.exists()){
            if(!weightFile.createNewFile()){
                System.out.println("ERROR :: Unable to create an empty weight File with "+ outputWeightFile);
            }
        }else {
            System.out.println("Weight FIle already exists ; overwriting it < "+ outputWeightFile);
        }

        ProcessBuilder pb = new ProcessBuilder(
                //TODO:: move conda params to yaml.
                "/home/vassarml/miniconda3/condabin/conda", "run", "-n", "glob"
                ,PAConstants.getPYTHON_SCRIPT_CMD(),
                PAConstants.getPYTHON_APP_SCRIPT(),
                PAUtils.PA_COMPONENT_NAME,
                PAUtils.PA_TRAINING_ACTION_TYPE,
                outputWeightFile,
                annotationsFilePath,
                classesList.toString().replaceAll("\\s", ""),
                taskUUID

        ).directory(new File(PAConstants.getPYTHON_SCRIPTS_DIR()));
        pb.redirectErrorStream(true);

        System.out.println("pb" + pb);
        System.out.println("************ PA_Training_Python_Script command : " + String.join(" ", pb.command()));

        Process trainingProcess = pb.start();
        long pythonProcessID = trainingProcess.pid();

        System.out.println("processID"+pythonProcessID);
        taskManagementService.updateProcessID(taskUUID, pythonProcessID);

        System.out.println("************ Executing PA_Training_Python_Script with command : " + String.join(" ", pb.command()));

        System.out.println("************ redirecting logs here :: Training_Python_Script **********");

        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(trainingProcess.getInputStream()));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();

        trainingProcess.waitFor();

        System.out.println("Completed Executing Training ");


    }

    private FilterParams getFilterParamsFromTrainingParams(ITrainingParams trainingParams) {
        FilterParams filterParams = new FilterParams();
        filterParams.setAppUUID(trainingParams.getAppUUID());
        filterParams.setEndDateTs(trainingParams.getEndDateTs());
        filterParams.setStartDateTs(trainingParams.getStartDateTs());
        filterParams.setSource(trainingParams.getSource());
        filterParams.setLocType(trainingParams.getLocType());
        filterParams.setLocUUID(trainingParams.getLocUUID());
        filterParams.setLabelType(trainingParams.getLabelType());
        return filterParams;
    }


    private void setTaskInfo(ITaskInfo taskInfo, String taskUUID, String userUUID, int eventGenDay, Long refTs) {
        taskInfo.setUserUUID(userUUID);
        taskInfo.setTaskUUID(taskUUID);
        taskInfo.setComponentType(TaskManagementConstants.COMPONENT_PA_TRAINING);
        taskInfo.setEventGenDay(eventGenDay);
        taskInfo.setEventGenMonth(DateUtils.getEventGenMonthFromDate(eventGenDay));
        taskInfo.setEventGenTs(refTs);
        taskInfo.setTaskStartTs(refTs);
    }
}
