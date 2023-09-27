package com.vassarlabs.pictorialanalysis.service.impl;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.utils.DateUtils;
import com.vassarlabs.common.utils.err.InvalidDataFoundException;
import com.vassarlabs.pictorialanalysis.service.api.ILabelPredictionService;
import com.vassarlabs.pictorialanalysis.utils.PAConstantProperties;
import com.vassarlabs.pictorialanalysis.utils.PAUtils;
import com.vassarlabs.taskmngr.pojo.api.ITaskInfo;
import com.vassarlabs.taskmngr.pojo.impl.TaskInfo;

import com.vassarlabs.taskmngr.service.api.ITaskManagementService;
import com.vassarlabs.taskmngr.utils.TaskManagementConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class LabelPredictionService implements ILabelPredictionService {

    @Autowired
    protected ITaskManagementService taskManagementService;

    @Autowired
    protected PAConstantProperties paConstantProperties;


    @Override
    public boolean predictLabel(Map<String,String> imgUUIDVsImagePathMap, String modelPath, String userUUID, String taskInputData ,String predictType){

        System.out.println("In LabelPredictionService :: predictLabel");
        if(modelPath==null || modelPath.isEmpty() || userUUID==null || userUUID.isEmpty() || taskInputData==null || taskInputData.isEmpty() || imgUUIDVsImagePathMap.isEmpty() || imgUUIDVsImagePathMap == null){
            System.out.println("Invalid Parameters found");
            return false;
        }

        Long refTs = System.currentTimeMillis();
        Integer eventGenDay = DateUtils.getEventGenDay(refTs);

        ITaskInfo taskInfo = new TaskInfo();
        String taskUUID = UUID.randomUUID().toString();
        System.out.println("taskUUID :: " + taskUUID);
        taskInfo.setUserUUID(userUUID);
        taskInfo.setTaskUUID(taskUUID);
        taskInfo.setComponentType(predictType);
        taskInfo.setEventGenDay(eventGenDay);
        taskInfo.setEventGenMonth(DateUtils.getEventGenMonthFromDate(eventGenDay));
        taskInfo.setEventGenTs(refTs);
        taskInfo.setTaskStartTs(refTs);
        taskInfo.setTaskInputDetails(taskInputData);

        try {
            taskManagementService.addNewTaskInfo(taskInfo);
            executeModel(imgUUIDVsImagePathMap,modelPath,taskUUID);
            System.out.println("Completed Prediction Process - Check Task Status to know whether prediction is successful or not");

//            ITaskInfo taskInfoFromPyScript = taskManagementService.readTaskInfoByTaskUUID(taskUUID);
//            if (taskInfoFromPyScript.getEventStatus() == TaskManagementConstants.FAILED_STATUS) {
//
//                System.out.println(" Prediction not successful  : python Script returned Error, check for remarks in task_info table ");
//                return false;
//            }
//
//            taskInfo.setEventStatus(TaskManagementConstants.SUCCESS_STATUS);
//            taskInfo.setEventState(TaskManagementConstants.COMPLETED_STATE);
//            taskManagementService.updateTaskInfo(taskUUID, taskInfo);

        } catch (IOException | InterruptedException | DSPException | InvalidDataFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Prediction Process Completed successfully, returning");
        return true;
    }
    public void executeModel(Map<String,String> imgUUIDVsImagePathMap,String modelPath,String taskUUID) throws IOException, InterruptedException, DSPException, InvalidDataFoundException {


        String UUIDVsImagePathListAsString = getUUIDVsImagePathListAsString(imgUUIDVsImagePathMap);


        List<String> pythonArgs = new ArrayList<>();
        pythonArgs.add(PAUtils.PA_COMPONENT_NAME);
        pythonArgs.add(PAUtils.PA_PREDICTION_ACTION_TYPE);
        pythonArgs.add(modelPath);
        pythonArgs.add(UUIDVsImagePathListAsString);
        pythonArgs.add(taskUUID);


        ProcessBuilder pb = new ProcessBuilder(
                //TODO:: move conda params to yaml.
                "/home/vassarml/miniconda3/condabin/conda", "run" , "-n", "glob"
                ,paConstantProperties.getPYTHON_SCRIPT_CMD(),
                paConstantProperties.getPYTHON_PREDICTION_SCRIPT());
        pb.command().addAll(pythonArgs);
        pb.directory(new File(paConstantProperties.getPYTHON_SCRIPTS_DIR()));
        pb.redirectErrorStream(true);

        System.out.println("Executing Prediction PythonScript with command : " + pb.command());
        Process process = pb.start();

        long pythonProcessID = process.pid();
        System.out.println("Updating processID in taskInfo table :: " + pythonProcessID);
        taskManagementService.updateProcessID(taskUUID, pythonProcessID);

        System.out.println("************ Redirecting logs here :: Prediction PythonScript **********");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine())!=null){
            System.out.println(line);
        }
        reader.close();

        int exitCode = process.waitFor();
        System.out.println("Python script execution completed with exit code: " + exitCode);
        System.out.println("Completed Executing Prediction_Python_Script ");

    }

    private String getUUIDVsImagePathListAsString(Map<String,String> imgUUIDVsImagePathMap){

        StringBuilder builder = new StringBuilder();

        for(String imgUUID : imgUUIDVsImagePathMap.keySet()){
            builder.append(imgUUID).append("@@").append(imgUUIDVsImagePathMap.get(imgUUID)).append("%%");
        }
        builder.delete(builder.length()-2,builder.length());
        return builder.toString();
    }
}
