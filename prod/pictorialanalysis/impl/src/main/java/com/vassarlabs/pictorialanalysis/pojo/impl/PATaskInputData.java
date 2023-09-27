package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vassarlabs.pictorialanalysis.pojo.api.IPATaskInputData;


public class PATaskInputData implements IPATaskInputData<FilterParams> {

    @JsonProperty("filterParams")
    @SerializedName("filterParams")
    private FilterParams filterParams;

    @JsonProperty("annotationsFilePath")
    @SerializedName("annotationsFilePath")
    private String annotationsFilePath;

    @JsonProperty("retrainModelUUID")
    @SerializedName("retrainModelUUID")
    private String retrainOnModelUUID;
    @JsonProperty("taskName")
    @SerializedName("taskName")
    private String taskName;


    @JsonProperty("modelUUID")
    @SerializedName("modelUUID")
    private String modelUUID;

    private static Gson GSON;

    static {
        GSON = new Gson();
    }

    public PATaskInputData() {
    }

    public PATaskInputData(FilterParams filterParams, String annotationsFilePath) {
        this.filterParams = filterParams;
        this.annotationsFilePath = annotationsFilePath;
    }
//    public PATaskInputData(IFilterParams filterParams, String annotationsFilePath) {
//        this.filterParams = (FilterParams) filterParams;
//        this.annotationsFilePath = annotationsFilePath;
//    }

    public PATaskInputData(FilterParams filterParams, String annotationsFilePath, String retrainOnModelUUID) {
        this.filterParams = filterParams;
        this.annotationsFilePath = annotationsFilePath;
        this.retrainOnModelUUID = retrainOnModelUUID;
    }

    public PATaskInputData(FilterParams filterParams, String annotationsFilePath, String retrainOnModelUUID, String taskName) {
        this.filterParams = filterParams;
        this.annotationsFilePath = annotationsFilePath;
        this.retrainOnModelUUID = retrainOnModelUUID;
        this.taskName = taskName;
    }

    public PATaskInputData(FilterParams filterParams, String annotationsFilePath, String retrainOnModelUUID, String taskName, String modelUUID) {
        this.filterParams = filterParams;
        this.annotationsFilePath = annotationsFilePath;
        this.retrainOnModelUUID = retrainOnModelUUID;
        this.taskName = taskName;
        this.modelUUID = modelUUID;
    }

    public static String convertTaskInputDataToJsonData(IPATaskInputData taskInputData) {
        return GSON.toJson(taskInputData);
    }

    public static IPATaskInputData convertJsonToTaskInputData(String taskInputData){
        return GSON.fromJson(taskInputData, PATaskInputData.class);
    }




    public FilterParams getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(FilterParams filterParams) {
        this.filterParams = filterParams;
    }


    public String getAnnotationsFilePath() {
        return annotationsFilePath;
    }

    public void setAnnotationsFilePath(String annotationsFilePath) {
        this.annotationsFilePath = annotationsFilePath;
    }

    public String getRetrainOnModel() {
        return retrainOnModelUUID;
    }

    public void setRetrainOnModel(String retrainOnModel) {
        this.retrainOnModelUUID = retrainOnModel;
    }
    @Override
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    @Override
    public String getTaskName(){
        return taskName;
    }
    @Override
    public void setModelUUID(String modelUUID) {
        this.modelUUID = modelUUID;
    }
    @Override
    public String getModelUUID(){
        return modelUUID;
    }

    @Override
    public String toString() {
        return "PATaskInputData{" +
                "filterParams=" + filterParams +
                ", annotationsFilePath='" + annotationsFilePath + '\'' +
                ", retrainOnModelUUID='" + retrainOnModelUUID + '\'' +
                ", taskName='" + taskName + '\'' +
                ", modelUUID='" + modelUUID + '\'' +
                '}';
    }
}
