package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vassarlabs.pictorialanalysis.pojo.api.IFilterParams;
public class FilterParams implements IFilterParams {
    @JsonProperty("startDateTs")
    private Long startDateTs;

    @JsonProperty("endDateTs")
    private Long endDateTs;

    @JsonProperty("appUUID")
    private String appUUID;

    @JsonProperty("locUUID")
    private String locUUID;

    @JsonProperty("locType")
    private String locType;

    @JsonProperty("source")
    private String source;

    @JsonProperty("labelType")
    private String labelType;

    public FilterParams() {
    }

    public FilterParams(Long startDateTs, Long endDateTs, String appUUID, String locUUID, String locType, String source) {
        this.startDateTs = startDateTs;
        this.endDateTs = endDateTs;
        this.appUUID = appUUID;
        this.locUUID = locUUID;
        this.locType = locType;
        this.source = source;
    }

    public FilterParams(Long startDateTs, Long endDateTs, String appUUID, String locUUID, String locType, String source, String labelType) {
        this.startDateTs = startDateTs;
        this.endDateTs = endDateTs;
        this.appUUID = appUUID;
        this.locUUID = locUUID;
        this.locType = locType;
        this.source = source;
        this.labelType = labelType;
    }

    @Override
    public Long getStartDateTs() {
        return startDateTs;
    }
    @Override
    public void setStartDateTs(Long startDateTs) {
        this.startDateTs = startDateTs;
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
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
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
    public String toString() {
        return "FilterParams{" +
                "startDateTs=" + startDateTs +
                ", endDateTs=" + endDateTs +
                ", appUUID='" + appUUID + '\'' +
                ", locUUID='" + locUUID + '\'' +
                ", locType='" + locType + '\'' +
                ", source='" + source + '\'' +
                ", labelType='" + labelType + '\'' +
                '}';
    }
}
