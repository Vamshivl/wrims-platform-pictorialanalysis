package com.vassarlabs.pictorialanalysis.dsp.api;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.pictorialanalysis.pojo.api.ILabeledImage;

import java.util.List;

public interface ILabeledImageDSP {

    public void insertLabeledImagesDSP (List<ILabeledImage> newLabeledImages)
            throws DSPException;

    public List<ILabeledImage> getListOfLabeledImagesDSP (Long startTs, Long endTs, String applicationUUID)
        throws DSPException;

    public List<ILabeledImage> getListOfLabeledImagesByUUIDsAndLabelTypeForTraining (List<String> imageUUIDs, String labelType)
            throws DSPException;

    public List<ILabeledImage> getListOfLabeledImages (Long startTs, Long endTs,List<String> imageUUIDs,String labelType)
            throws DSPException;

    public List<ILabeledImage> getLabeledImagesByTaskUUID(String taskUUID) throws DSPException;

    public boolean deleteLabeledImagesForARawImageDSP (List<String> rawImageUUIDs) throws DSPException;
}
