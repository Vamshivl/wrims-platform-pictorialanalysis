package com.vassarlabs.pictorialanalysis.service.api;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.utils.err.InvalidDataFoundException;
import com.vassarlabs.pictorialanalysis.pojo.api.IRawImage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IRawImageDataService {

    public void addRawImage(List<IRawImage> rawImageList) throws DSPException, InvalidDataFoundException;

    public Map<String, IRawImage> getMapOfRawImages(Long startTs, Long endTs, String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException, InvalidDataFoundException;

    public Map<String, IRawImage> getMapOfRawImages(String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException, InvalidDataFoundException;

    public List<String> getDistinctLabelsFromRawImages (Map<String, IRawImage> mapOfRawImagesData);

    public LinkedHashMap<String, String> getImageUUIDVsImagePath(List<String> imageUUIDs) throws DSPException;

    public Map<String,IRawImage> getListOfImagesForUUIDs(List<String> imageUUIDs) throws DSPException;

    public List<IRawImage> getRawImagesListByFarmUUID(String farmUUID, int year, String season,String source) throws DSPException;

    public void deleteRawImagesByImageUUIDsList(List<String> imageUUIDsList) throws DSPException;

    public void addTempRawImage(List<IRawImage> rawImageList)
            throws DSPException, InvalidDataFoundException;

    public List<IRawImage> getTempRawImagesListByPointUUID(String pointUUID, int year, String season, String source) throws DSPException;

    Map<String, Integer> getLocationWiseRawImagesCount(String userUUID, String locType, String cropYear, String season, String source) throws DSPException;
}
