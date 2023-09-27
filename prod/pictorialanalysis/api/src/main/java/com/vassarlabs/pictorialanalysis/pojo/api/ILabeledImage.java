package com.vassarlabs.pictorialanalysis.pojo.api;

import java.io.Serializable;

public interface ILabeledImage {

    public Long getLabeledImageID();

    public void setLabeledImageID(Long labeledImageID);

    public String getRawImageUUID();

    public void setRawImageUUID(String rawImageUUID);

    public String getImageBBOX();

    public void setImageBBOX(String imageBBOX);

    public String getImageLabel();

    public void setImageLabel(String imageLabel);

    public String getLabelType();

    public void setLabelType(String labelType);

    public String getTaskUUID();

    public void setTaskUUID(String taskUUID);

    public boolean isDeleted();

    public void setDeleted(boolean deleted);

    public Long getInsertTS();

    public void setInsertTS(Long insertTS);

    public Long getUpdateTS();

    public void setUpdateTS(Long updateTS);
}