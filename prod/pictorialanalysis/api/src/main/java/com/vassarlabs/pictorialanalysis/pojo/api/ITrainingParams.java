package com.vassarlabs.pictorialanalysis.pojo.api;

public interface ITrainingParams {

    public Long getStartDateTs();

    public void setStartDateTs(Long startDateTs);

    public Long getEndDateTs();

    public void setEndDateTs(Long endDateTs);

    String getAppUUID();

    void setAppUUID(String appUUID);

    String getLocUUID();

    void setLocUUID(String locUUID);

    String getLocType();

    void setLocType(String locType);

    String getComponentType();

    void setComponentType(String componentType);

    String getSource();

    void setSource(String source);

    String getUserUUID();

    void setUserUUID(String userUUID);

    String getModelUUID();

    void setModelUUID(String modelUUID);

    String getModelName();

    void setModelName(String modelName);

    String getAlgoName();

    void setAlgoName(String algoName);

    String getExistingTaskUUID();

    void setExistingTaskUUID(String existingTaskUUID);

    Integer getTrainOrPredictFlag();

    void setTrainOrPredictFlag(Integer trainOrPredictFlag);

    String getLabelType();

    void setLabelType(String labelType);

    public String getActionType();

    public void setActionType(String actionType);
}
