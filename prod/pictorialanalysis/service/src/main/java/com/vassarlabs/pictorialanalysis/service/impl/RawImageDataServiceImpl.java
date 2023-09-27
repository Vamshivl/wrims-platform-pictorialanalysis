package com.vassarlabs.pictorialanalysis.service.impl;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.utils.FileUtils;
import com.vassarlabs.common.utils.err.InvalidDataFoundException;
import com.vassarlabs.pictorialanalysis.dsp.api.IRawImageDSP;
import com.vassarlabs.pictorialanalysis.pojo.api.IRawImage;
import com.vassarlabs.pictorialanalysis.service.api.IRawImageDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class RawImageDataServiceImpl implements IRawImageDataService {

    @Autowired
    protected IRawImageDSP rawImageDSP;

    @Override
    public void addRawImage(List<IRawImage> rawImageList)
            throws DSPException, InvalidDataFoundException {
        System.out.println("Inside RawImageDataServiceImpl :: addRawImage");

        if(rawImageList == null || rawImageList.isEmpty()){
            throw new InvalidDataFoundException("Invalid rawImage sent :: " + rawImageList);
        }
        rawImageDSP.insertRawImage(rawImageList);
        System.out.println("Inserted Successfully");
    }

    @Override
    public Map<String, IRawImage> getMapOfRawImages(Long startTs, Long endTs, String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException, InvalidDataFoundException {

        Map<String, IRawImage> rawImageMap = new HashMap<>();

        if (startTs == null || startTs < 0) {
            throw new InvalidDataFoundException("Invalid Start Date sent: " + startTs);
        }

        if (endTs == null || endTs < 0) {
            throw new InvalidDataFoundException("Invalid End Date sent: " + endTs);
        }

        if (applicationUUID == null || applicationUUID.isEmpty()) {
            throw new InvalidDataFoundException("Invalid Application UUID sent: " + applicationUUID);
        }

        if (locationUUIDs.isEmpty()) {
            throw new InvalidDataFoundException("Invalid location UUIDs sent: " + locationUUIDs);
        }

        List<IRawImage> rawImageListFromDSP = rawImageDSP.getListOfRawImagesDSP(startTs, endTs, applicationUUID, locationUUIDs, imgSrc, imgLabel);
        System.out.println("rawImageListFromDSP: " + rawImageListFromDSP + "\nsize: " + rawImageListFromDSP.size());

        for (IRawImage currRawImage: rawImageListFromDSP) {
            String rawImageUUID = currRawImage.getImageUUID();

            rawImageMap.putIfAbsent(rawImageUUID, currRawImage);
        }

        return rawImageMap;
    }

    @Override
    public Map<String, IRawImage> getMapOfRawImages(String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException, InvalidDataFoundException {

        Map<String, IRawImage> rawImageMap = new HashMap<>();


        if (applicationUUID == null || applicationUUID.isEmpty()) {
            throw new InvalidDataFoundException("Invalid Application UUID sent: " + applicationUUID);
        }

        if (locationUUIDs.isEmpty()) {
            throw new InvalidDataFoundException("Invalid location UUIDs sent: " + locationUUIDs);
        }

        List<IRawImage> rawImageListFromDSP = rawImageDSP.getListOfRawImagesDSP(applicationUUID, locationUUIDs, imgSrc, imgLabel);
        System.out.println("rawImageListFromDSP: " + rawImageListFromDSP + "\nsize: " + rawImageListFromDSP.size());

        for (IRawImage currRawImage: rawImageListFromDSP) {
            String rawImageUUID = currRawImage.getImageUUID();

            rawImageMap.putIfAbsent(rawImageUUID, currRawImage);
        }
        return rawImageMap;
    }

    @Override
    public List<String> getDistinctLabelsFromRawImages (Map<String, IRawImage> mapOfRawImagesData) {
        Set<String> resultSetOfLabels = new HashSet<>();

        for (Map.Entry<String,IRawImage> entry : mapOfRawImagesData.entrySet()) {
            IRawImage currRawImage = entry.getValue();

            String label = currRawImage.getImageLabel();
            resultSetOfLabels.add(label);
        }

        return new ArrayList<>(resultSetOfLabels);
    }

    @Override
    public LinkedHashMap<String, String> getImageUUIDVsImagePath(List<String> imageUUIDs) throws DSPException {
        return rawImageDSP.getImagePaths(imageUUIDs);
    }

    @Override
    public Map<String, IRawImage> getListOfImagesForUUIDs(List<String> imageUUIDs) throws DSPException {
        return rawImageDSP.getImagesByImageUUIDs(imageUUIDs);
    }
    @Override
    public List<IRawImage> getRawImagesListByFarmUUID(String farmUUID, int year, String season, String source) throws DSPException {
        return rawImageDSP.getRawImagesByLocUUID(farmUUID, year, season, source);
    }

    @Override
    public void deleteRawImagesByImageUUIDsList(List<String> imageUUIDsList) throws DSPException {
        System.out.println("In RawImageDataServiceImpl :: deleteRawImagesByImageUUIDsList");
        if (imageUUIDsList == null || imageUUIDsList.isEmpty()) {
            System.out.println("imageUUIDsList is null or Empty");
        }
        Map<String, String> rawImageUUIDVsImagePathMap = rawImageDSP.getImagePaths(imageUUIDsList);
        if (rawImageUUIDVsImagePathMap != null && !rawImageUUIDVsImagePathMap.isEmpty()) {
            for (String imageUUID : rawImageUUIDVsImagePathMap.keySet()) {
                String imagePath = rawImageUUIDVsImagePathMap.get(imageUUID);
                if (imagePath == null || imagePath.isBlank()) {
                    continue;
                }
                File file = new File(imagePath);
                boolean isDeleted = FileUtils.deleteFile(file);
                if (!isDeleted) {
                    System.out.println("VAXXX::unable to delete image for image uuid = " + imageUUID + " of image path = " + imagePath);
                }
            }
            rawImageDSP.deleteRawImageByImageUUIDsList(new ArrayList<>(rawImageUUIDVsImagePathMap.keySet()));
        }
    }

    @Override
    public void addTempRawImage(List<IRawImage> rawImageList)
            throws DSPException, InvalidDataFoundException {
        System.out.println("Inside RawImageDataServiceImpl :: addTempRawImage");

        if(rawImageList == null || rawImageList.isEmpty()){
            throw new InvalidDataFoundException("Invalid rawImage sent :: " + rawImageList);
        }
        rawImageDSP.insertTempRawImage(rawImageList);
        System.out.println("Inserted Successfully");
    }



    @Override
    public List<IRawImage> getTempRawImagesListByPointUUID(String pointUUID, int year, String season, String source) throws DSPException {
        return rawImageDSP.getTempRawImagesByPointUUID(pointUUID, year, season, source);
    }

    @Override
    public Map<String, Integer> getLocationWiseRawImagesCount(String userUUID, String locType, String cropYear, String season, String source) throws DSPException {
        return rawImageDSP.getLocationWiseRawImagesCount(userUUID, locType, cropYear, season, source);
    }


}
