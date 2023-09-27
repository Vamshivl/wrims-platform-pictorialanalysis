package com.vassarlabs.pictorialanalysis.dsp.api;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.pictorialanalysis.pojo.api.IRawImage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IRawImageDSP {
    public void insertRawImage(List<IRawImage> rawImages) throws DSPException;

    public List<IRawImage> getListOfRawImagesDSP(Long startTs, Long endTs, String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException;

    public List<IRawImage> getListOfRawImagesDSP(String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException;

    LinkedHashMap<String, String> getImagePaths(List<String> imageUUIDs) throws DSPException;

    Map<String , IRawImage> getImagesByImageUUIDs(List<String> imageUUIDs) throws DSPException;

    List<IRawImage> getRawImagesByLocUUID(String locUUID, int year, String season, String source) throws DSPException;
    public void deleteRawImageByImageUUIDsList(List<String> imageUUIDsList) throws DSPException;

    public void insertTempRawImage(List<IRawImage> rawImages) throws DSPException;

    public List<IRawImage> getTempRawImagesByPointUUID(String locUUID, int year, String season, String source) throws DSPException;

    Map<String , Integer > getLocationWiseRawImagesCount(String userUUID, String locType,String cropYear, String season, String source) throws DSPException;
}
