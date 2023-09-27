package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vassarlabs.pictorialanalysis.pojo.api.ILabeledImage;

//id integer
//        raw_image_uuid character varying NOT NULL,
//        image_bbox jsonb,
//        image_label character varying,
//        label_type character varying,
//        task_uuid character varying,
//        deleted boolean DEFAULT false,
//        insert_ts bigint NOT NULL
//        update_ts bigint  NOT NULL,

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class LabeledImage implements ILabeledImage{

    @JsonIgnore
    private Long labeledImageID;

    @JsonProperty("raw_image_uuid")
//    @SerializedName("raw_image_uuid")
    private String rawImageUUID;

    @JsonProperty("image_bbox")
//    @SerializedName("image_bbox")
    private String imageBBOX;

    @JsonProperty("image_label")
//    @SerializedName("image_label")
    private String imageLabel;

    @JsonProperty("label_type")
//    @SerializedName("label_type")
    private String labelType;

    @JsonProperty("task_uuid")
//    @SerializedName("task_uuid")
    private String taskUUID;

    @JsonIgnore
    private boolean deleted;

    @JsonIgnore
    private Long insertTS;

    @JsonIgnore
    private Long updateTS;

    @Override
    public Long getLabeledImageID() {
        return labeledImageID;
    }

    @Override
    public void setLabeledImageID(Long labeledImageID) {
        this.labeledImageID = labeledImageID;
    }

    @Override
    public String getRawImageUUID() {
        return rawImageUUID;
    }

    @Override
    public void setRawImageUUID(String rawImageUUID) {
        this.rawImageUUID = rawImageUUID;
    }

    @Override
    public String getImageBBOX() {
        return imageBBOX;
    }

    @Override
    public void setImageBBOX(String imageBBOX) {
        this.imageBBOX = imageBBOX;
    }

    @Override
    public String getImageLabel() {
        return imageLabel;
    }

    @Override
    public void setImageLabel(String imageLabel) {
        this.imageLabel = imageLabel;
    }

    @Override
    public String getLabelType() {
        return labelType;
    }

    @Override
    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    @Override
    public String getTaskUUID() {
        return taskUUID;
    }

    @Override
    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public Long getInsertTS() {
        return insertTS;
    }

    @Override
    public void setInsertTS(Long insertTS) {
        this.insertTS = insertTS;
    }

    @Override
    public Long getUpdateTS() {
        return updateTS;
    }

    @Override
    public void setUpdateTS(Long updateTS) {
        this.updateTS = updateTS;
    }

    @Override
    public String toString() {
        return "LabeledImage{" +
                "labeledImageID=" + labeledImageID +
                ", rawImageUUID='" + rawImageUUID + '\'' +
                ", imageBBOX='" + imageBBOX + '\'' +
                ", imageLabel='" + imageLabel + '\'' +
                ", labelType='" + labelType + '\'' +
                ", taskUUID='" + taskUUID + '\'' +
                ", deleted=" + deleted +
                ", insertTS=" + insertTS +
                ", updateTS=" + updateTS +
                '}';
    }
}