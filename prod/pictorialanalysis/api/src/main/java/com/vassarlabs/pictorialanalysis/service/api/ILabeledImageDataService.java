package com.vassarlabs.pictorialanalysis.service.api;

import com.vassarlabs.common.utils.err.InvalidDataFoundException;
import com.vassarlabs.pictorialanalysis.pojo.api.ILabeledImage;
import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.config.err.PropertyNotFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ILabeledImageDataService {

    public void insertListOfLabeledImages(List<ILabeledImage> newLabeledImages)
            throws DSPException, InvalidDataFoundException;

    public List<ILabeledImage> getListOfLabeledImages(Long startTs, Long endTs, String applicationUUID)
        throws DSPException, InvalidDataFoundException;


    public Map<String, List<ILabeledImage>> getMapOfLabeledImages(List<String> imageUUIDs, String labelType)
            throws DSPException, InvalidDataFoundException;

    public Map<String, List<ILabeledImage>> getMapOfLabeledImages(Long startTs, Long endTs, List<String> imageUUIDs, String labelType)
            throws DSPException, InvalidDataFoundException;

    public LinkedHashMap<String,LinkedHashMap<String, String>> getBboxLabelsForImages(Long startDate, Long endDate, String locUUID, String locType, String appUUID, String source,String labelType)
            throws DSPException, InvalidDataFoundException, PropertyNotFoundException;

    LinkedHashMap<String, Integer> getDistinctImageLabelCounts(LinkedHashMap<String, LinkedHashMap<String, String>> labeledImages);

    LinkedHashMap<String,LinkedHashMap<String,String>> getLabeledImagesWithPath(LinkedHashMap<String, LinkedHashMap<String, String>> bboxLabels) throws DSPException;

    public List<ILabeledImage> getLabeledImagesByTaskUUID(String taskUUID) throws DSPException;

    public boolean deleteLabeledImagesForARawImage(List<String> rawImageUUIDs) throws InvalidDataFoundException, DSPException;
}
