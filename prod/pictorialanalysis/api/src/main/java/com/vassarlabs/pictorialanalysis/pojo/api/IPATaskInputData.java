package com.vassarlabs.pictorialanalysis.pojo.api;

public interface IPATaskInputData<T> {

    public T getFilterParams();

    public void setFilterParams(T filterParams);

    public String getAnnotationsFilePath();

    public void setAnnotationsFilePath(String annotationsFilePath);

    public String getRetrainOnModel();

    public void setRetrainOnModel(String retrainOnModel);

    void setTaskName(String taskName);

    String getTaskName();

    void setModelUUID(String modelUUID);

    String getModelUUID();
}
