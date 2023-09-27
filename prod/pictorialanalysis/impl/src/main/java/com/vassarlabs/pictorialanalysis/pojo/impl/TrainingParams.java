package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vassarlabs.pictorialanalysis.pojo.api.ITrainingParams;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingParams implements ITrainingParams {
    private Long startDateTs;
    private Long endDateTs;
    private String appUUID;
    private String locUUID;
    private String locType;
    private String userUUID;
    private String modelUUID;
    private String modelName;
    private String algoName;
    private String existingTaskUUID;
    private String componentType;
    private Integer trainOrPredictFlag;
    private String source ;
    private String labelType;
    private String actionType;
    public TrainingParams() {
    }

    public TrainingParams(Long startDateTs, Long endDateTs, String appUUID, String locUUID, String locType, String userUUID, String modelUUID, String modelName, String algoName, String existingTaskUUID, String componentType, Integer trainOrPredictFlag, String source, String labelType) {
        this.startDateTs = startDateTs;
        this.endDateTs = endDateTs;
        this.appUUID = appUUID;
        this.locUUID = locUUID;
        this.locType = locType;
        this.userUUID = userUUID;
        this.modelUUID = modelUUID;
        this.modelName = modelName;
        this.algoName = algoName;
        this.existingTaskUUID = existingTaskUUID;
        this.componentType = componentType;
        this.trainOrPredictFlag = trainOrPredictFlag;
        this.source = source;
        this.labelType = labelType;
    }

    public TrainingParams(Long startDateTs, Long endDateTs, String appUUID, String locUUID, String locType, String userUUID, String modelUUID, String modelName, String algoName, String existingTaskUUID, String componentType, Integer trainOrPredictFlag, String source, String labelType, String actionType) {
        this.startDateTs = startDateTs;
        this.endDateTs = endDateTs;
        this.appUUID = appUUID;
        this.locUUID = locUUID;
        this.locType = locType;
        this.userUUID = userUUID;
        this.modelUUID = modelUUID;
        this.modelName = modelName;
        this.algoName = algoName;
        this.existingTaskUUID = existingTaskUUID;
        this.componentType = componentType;
        this.trainOrPredictFlag = trainOrPredictFlag;
        this.source = source;
        this.labelType = labelType;
        this.actionType = actionType;
    }

    @Override
    public String getComponentType() {
        return componentType;
    }
    @Override
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    @Override
    public String getSource() {
        return source;
    }
    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public Integer getTrainOrPredictFlag() {
        return trainOrPredictFlag;
    }

    @Override
    public void setTrainOrPredictFlag(Integer trainOrPredictFlag) {
        this.trainOrPredictFlag = trainOrPredictFlag;
    }


    @Override
    public String getUserUUID() {
        return userUUID;
    }
    @Override
    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }
    @Override
    public String getModelUUID() {
        return modelUUID;
    }
    @Override
    public void setModelUUID(String modelUUID) {
        this.modelUUID = modelUUID;
    }
    @Override
    public String getModelName() {
        return modelName;
    }
    @Override
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    @Override
    public String getAlgoName() {
        return algoName;
    }
    @Override
    public void setAlgoName(String algoName) {
        this.algoName = algoName;
    }
    @Override
    public String getExistingTaskUUID() {
        return existingTaskUUID;
    }
    @Override
    public void setExistingTaskUUID(String existingTaskUUID) {
        this.existingTaskUUID = existingTaskUUID;
    }
    @Override
    public Long getStartDateTs() {
        return startDateTs;
    }
    @Override
    public void setStartDateTs(Long startDate) {
        this.startDateTs = startDate;
    }
    @Override
    public Long getEndDateTs() {
        return endDateTs;
    }
    @Override
    public void setEndDateTs(Long endDateTs) {
        this.endDateTs = endDateTs;
    }
    @Override
    public String getAppUUID() {
        return appUUID;
    }
    @Override
    public void setAppUUID(String appUUID) {
        this.appUUID = appUUID;
    }
    @Override
    public String getLocUUID() {
        return locUUID;
    }
    @Override
    public void setLocUUID(String locUUID) {
        this.locUUID = locUUID;
    }
    @Override
    public String getLocType() {
        return locType;
    }
    @Override
    public void setLocType(String locType) {
        this.locType = locType;
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
    public String getActionType() {
        return actionType;
    }

    @Override
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "TrainingParams{" +
                "startDateTs=" + startDateTs +
                ", endDateTs=" + endDateTs +
                ", appUUID='" + appUUID + '\'' +
                ", locUUID='" + locUUID + '\'' +
                ", locType='" + locType + '\'' +
                ", userUUID='" + userUUID + '\'' +
                ", modelUUID='" + modelUUID + '\'' +
                ", modelName='" + modelName + '\'' +
                ", algoName='" + algoName + '\'' +
                ", existingTaskUUID='" + existingTaskUUID + '\'' +
                ", componentType='" + componentType + '\'' +
                ", trainOrPredictFlag=" + trainOrPredictFlag +
                ", source='" + source + '\'' +
                ", labelType='" + labelType + '\'' +
                ", actionType='" + actionType + '\'' +
                '}';
    }
}
