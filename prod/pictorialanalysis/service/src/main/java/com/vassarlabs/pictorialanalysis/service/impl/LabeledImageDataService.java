package com.vassarlabs.pictorialanalysis.service.impl;

import com.vassarlabs.boundarydetection.service.api.IFarmMetaDataService;
import com.vassarlabs.location.utils.LocationConstants;
import com.vassarlabs.pictorialanalysis.dsp.api.ILabeledImageDSP;
import com.vassarlabs.pictorialanalysis.service.api.ILabeledImageDataService;
import com.vassarlabs.pictorialanalysis.service.api.IRawImageDataService;
import com.vassarlabs.config.err.PropertyNotFoundException;
import com.vassarlabs.location.service.api.ILocationHierarchyCacheService;
import com.vassarlabs.config.service.api.IProductConfigCacheService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.logging.api.IVLLogService;
import com.vassarlabs.common.logging.api.IVLLogger;
import com.vassarlabs.common.utils.err.InvalidDataFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import com.vassarlabs.pictorialanalysis.pojo.api.ILabeledImage;

import java.util.*;

@Component
public class LabeledImageDataService implements ILabeledImageDataService {

    protected IVLLogger logger;

    @Autowired
    private IVLLogService logFactory;


    @PostConstruct
    public void initLogger() {
        this.logger = logFactory.getLogger(getClass());
    }

    @Autowired
    private ILabeledImageDSP labeledImageDSP;

    @Autowired
    private IRawImageDataService rawImageDataService;

    @Autowired
    private IProductConfigCacheService productConfigCacheService;

    @Autowired
    private ILocationHierarchyCacheService locHCacheService;

    @Autowired
    private IFarmMetaDataService farmMetaDataService;

    @Override
    public void insertListOfLabeledImages(List<ILabeledImage> newLabeledImages)
            throws DSPException, InvalidDataFoundException {

        System.out.println("In LabeledImageDataService :: insertLabeledImages");

        if (newLabeledImages == null || newLabeledImages.isEmpty()) {
            throw new InvalidDataFoundException("Invalid labeledImages are sent: " + newLabeledImages);
        }

        labeledImageDSP.insertLabeledImagesDSP(newLabeledImages);
    }


    @Override
    public List<ILabeledImage> getListOfLabeledImages(Long startTs, Long endTs, String applicationUUID)
            throws DSPException, InvalidDataFoundException {

        System.out.println("In LabeledImageDataService :: getListOfLabeledImages");

        List<ILabeledImage> labeledImageList = new ArrayList<>();

        if (startTs == null || startTs < 0) {
            throw new InvalidDataFoundException("Invalid Start Date sent: " + startTs);
        }

        if (endTs == null || endTs < 0) {
            throw new InvalidDataFoundException("Invalid End Date sent: " + endTs);
        }

        if (applicationUUID == null || applicationUUID.isEmpty()) {
            throw new InvalidDataFoundException("Invalid Application UUID sent: " + applicationUUID);
        }

        return labeledImageDSP.getListOfLabeledImagesDSP(startTs, endTs, applicationUUID);
    }


    @Override
    public Map<String, List<ILabeledImage>> getMapOfLabeledImages(List<String> imageUUIDs, String labelType)
            throws DSPException, InvalidDataFoundException {

        Map<String, List<ILabeledImage>> labeledImagesMap = new HashMap<>();

        if (imageUUIDs == null || imageUUIDs.isEmpty()) {
            System.out.println("No raw images present for params given");
            return labeledImagesMap;
        }

        if (labelType == null || labelType.isEmpty()) {
            throw new InvalidDataFoundException("Invalid label Type sent: " + null);
        }

        List<ILabeledImage> labeledImageListFromDSP = labeledImageDSP.getListOfLabeledImagesByUUIDsAndLabelTypeForTraining(imageUUIDs, labelType);
        System.out.println("labeledImageListFromDSP: " + labeledImageListFromDSP + "\nsize: " + labeledImageListFromDSP.size());

        for (ILabeledImage currLabeledImage : labeledImageListFromDSP) {
            String imageUUID = currLabeledImage.getRawImageUUID();

            labeledImagesMap.computeIfAbsent(imageUUID, k -> new ArrayList<>());

            labeledImagesMap.get(imageUUID).add(currLabeledImage);
        }

        return labeledImagesMap;
    }

    @Override
    public Map<String, List<ILabeledImage>> getMapOfLabeledImages(Long startTs, Long endTs, List<String> imageUUIDs, String labelType)
            throws DSPException, InvalidDataFoundException {

        Map<String, List<ILabeledImage>> labeledImagesMap = new HashMap<>();

        if (startTs == null || startTs < 0) {
            throw new InvalidDataFoundException("Invalid Start Date sent: " + startTs);
        }

        if (endTs == null || endTs < 0) {
            throw new InvalidDataFoundException("Invalid End Date sent: " + endTs);
        }

        if (imageUUIDs == null || imageUUIDs.isEmpty()) {
            System.out.println("No raw images present for params given");
            return labeledImagesMap;
        }

        if (labelType == null || labelType.isEmpty()) {
            throw new InvalidDataFoundException("Invalid label Type sent: " + null);
        }

        List<ILabeledImage> labeledImageListFromDSP = labeledImageDSP.getListOfLabeledImages(startTs, endTs, imageUUIDs, labelType);
        System.out.println("labeledImageListFromDSP: " + labeledImageListFromDSP + "\nsize: " + labeledImageListFromDSP.size());

        for (ILabeledImage currLabeledImage : labeledImageListFromDSP) {
            String imageUUID = currLabeledImage.getRawImageUUID();

            labeledImagesMap.computeIfAbsent(imageUUID, k -> new ArrayList<>());

            labeledImagesMap.get(imageUUID).add(currLabeledImage);
        }

        return labeledImagesMap;
    }


    @Override
    public LinkedHashMap<String, LinkedHashMap<String, String>> getBboxLabelsForImages(Long startTs, Long endTs, String locUUID, String locType, String appUUID, String source, String labelType)
            throws DSPException, InvalidDataFoundException, PropertyNotFoundException {

        if (startTs == null || endTs == null || locUUID == null || locType == null || appUUID == null ||
                locUUID.isBlank() || locType.isBlank() || appUUID.isBlank()) {

            throw new InvalidDataFoundException("InvalidDataFound for getListOfLabeledImages");
        }
        LinkedHashMap<String, LinkedHashMap<String, String>> resultMap = new LinkedHashMap<>();

        // Fetching all children details till farm level
        List<String> locUUIDsList = getChildLocationsList(locUUID, locType);
        System.out.println("LocUUIDsList size: " + locUUIDsList.size());
        Set<String> rawImageUUIDs = rawImageDataService.getMapOfRawImages(appUUID, locUUIDsList, source , null).keySet();

        List<ILabeledImage> resultImages = labeledImageDSP.getListOfLabeledImages(startTs, endTs,new ArrayList<>(rawImageUUIDs), labelType);

        for (ILabeledImage image : resultImages) {
            if (!resultMap.containsKey(image.getRawImageUUID())) {

                LinkedHashMap<String, String> BboxLabels = new LinkedHashMap<>();
                BboxLabels.put(image.getImageBBOX(), image.getImageLabel());
                resultMap.put(image.getRawImageUUID(), BboxLabels);

            } else {
                System.out.println(resultMap.get(image.getRawImageUUID()));
                resultMap.get(image.getRawImageUUID()).put(image.getImageBBOX(), image.getImageLabel());

            }
        }
        return resultMap;
    }

    @Override
    public LinkedHashMap<String, Integer> getDistinctImageLabelCounts(LinkedHashMap<String, LinkedHashMap<String, String>> labeledImages) {

        LinkedHashMap<String, Integer> classMap = new LinkedHashMap<>();
        for (String imgUUID : labeledImages.keySet()) {
            for (String label : labeledImages.get(imgUUID).values()) {
                if (classMap.containsKey(label)) {
                    classMap.put(label, classMap.get(label) + 1);
                } else {
                    classMap.put(label, 1);
                }
            }
        }
        return classMap;
    }

    @Override
    public LinkedHashMap<String,LinkedHashMap<String,String>> getLabeledImagesWithPath(LinkedHashMap<String, LinkedHashMap<String, String>> bboxLabels) throws DSPException {
        List<String> imageUUIDs = new ArrayList<>(bboxLabels.keySet());
        LinkedHashMap<String,String> imageVsPath = rawImageDataService.getImageUUIDVsImagePath(imageUUIDs);
        for(Map.Entry<String,LinkedHashMap<String,String>> bbox : bboxLabels.entrySet()){
            Map<String,String> bboxWithPath = bbox.getValue();
            bboxWithPath.put("ImagePath",imageVsPath.get(bbox.getKey()));
        }
        return bboxLabels;
    }

    @Override
    public List<ILabeledImage> getLabeledImagesByTaskUUID(String taskUUID) throws DSPException {
        System.out.println("In LabeledImageDataService :: getLabeledImagesByTaskUUID :: taskUUID :: "+taskUUID);
        if(taskUUID==null || taskUUID.isEmpty()){
            return null;
        }
        return labeledImageDSP.getLabeledImagesByTaskUUID(taskUUID);
    }

    public List<String> getChildLocationsList(String locUUID, String locType) throws PropertyNotFoundException, DSPException {

        List<String> imageLocUUIDList = new ArrayList<>();
        imageLocUUIDList.add(locUUID);

        String masterList = productConfigCacheService
                .getConfigValue("d06afc58-298a-4f40-942a-ea4c2321d194", "PICTORIAL_ANALYSIS_PROJECT", "ADMIN_HIERARCHY");
        if (masterList == null) {
            System.out.println("In PictorialAnalysisDataService::predictForAllImagesRequest productConfigValues not found");
            return imageLocUUIDList;
        }
        List<String> hierarchy = new LinkedList<>(Arrays.asList((masterList == null) ? "".split("") : masterList.split(",")));
        System.out.println("hierarchy :: " + hierarchy);

        Long refTs = System.currentTimeMillis();

        /*If params passed locUUID,locType is a village then return its respective farmUUIDs and that villageUUID also*/
        if (locType.equalsIgnoreCase(hierarchy.get(0))) {
            List<String> farmUUIDs = farmMetaDataService.getFarmMetaDataByInsertTsAndLoc(imageLocUUIDList,refTs);
            imageLocUUIDList.addAll(farmUUIDs);
            return imageLocUUIDList;
        }

        int orderOfLocInHierarchy = hierarchy.indexOf(locType);
        for (int index = orderOfLocInHierarchy - 1; index >= 0; index--) {
            System.out.println("locType :: " + locType + "  hierarchy.get(index)) :: " +  hierarchy.get(index));
            List<String> locations = locHCacheService.getAllChildLocForParentType(locType, hierarchy.get(index)).get(locUUID);
            if (locations != null && !locations.isEmpty()) {
                imageLocUUIDList.addAll(locations);

                /*If hierarchy.get(index)==VILLAGE then fetch farmUUIDs of those villages also and add them to result list*/
                List<String> farmUUIDs = new ArrayList<>();
                if(hierarchy.get(index).equalsIgnoreCase(LocationConstants.VILLAGE)){
                    farmUUIDs = farmMetaDataService.getFarmMetaDataByInsertTsAndLoc(locations,refTs);
                }
                imageLocUUIDList.addAll(farmUUIDs);
            }
        }
        return imageLocUUIDList;
    }

    @Override
    public boolean deleteLabeledImagesForARawImage(List<String> rawImageUUIDs) throws InvalidDataFoundException, DSPException {
        if (rawImageUUIDs==null || rawImageUUIDs.isEmpty()) {
            throw new InvalidDataFoundException("Invalid rawImage UUID is sent: " + rawImageUUIDs);
        }

        return labeledImageDSP.deleteLabeledImagesForARawImageDSP(rawImageUUIDs);
    }
}
