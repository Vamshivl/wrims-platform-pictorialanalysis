package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vassarlabs.pictorialanalysis.pojo.api.ITempFarmMetaData;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class TempFarmMetaData implements ITempFarmMetaData {

    private String locUUID;

    private String locType;

    private String latitude;

    private String longitude;

    private String cropInfoJson;

    private String ownerInfoJson;

    @JsonIgnore
    private Boolean isDeleted;

    @JsonIgnore
    private Long insertTs;

    @JsonIgnore
    private Long updateTs;

    private String seasonName;

    private Integer cropYear;

    private String pointUUID;

    public TempFarmMetaData() {
    }

    public TempFarmMetaData(String locUUID, String locType, String latitude, String longitude, String cropInfoJson, String ownerInfoJson, Boolean isDeleted, Long insertTs, Long updateTs, String seasonName, Integer cropYear, String pointUUID) {
        this.locUUID = locUUID;
        this.locType = locType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cropInfoJson = cropInfoJson;
        this.ownerInfoJson = ownerInfoJson;
        this.isDeleted = isDeleted;
        this.insertTs = insertTs;
        this.updateTs = updateTs;
        this.seasonName = seasonName;
        this.cropYear = cropYear;
        this.pointUUID = pointUUID;
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
    public String getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String getCropInfoJson() {
        return cropInfoJson;
    }

    @Override
    public void setCropInfoJson(String cropInfoJson) {
        this.cropInfoJson = cropInfoJson;
    }

    @Override
    public String getOwnerInfoJson() {
        return ownerInfoJson;
    }

    @Override
    public void setOwnerInfoJson(String ownerInfoJson) {
        this.ownerInfoJson = ownerInfoJson;
    }

    @Override
    public Boolean getDeleted() {
        return isDeleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public Long getInsertTs() {
        return insertTs;
    }

    @Override
    public void setInsertTs(Long insertTs) {
        this.insertTs = insertTs;
    }

    @Override
    public Long getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdateTs(Long updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public String getSeasonName() {
        return seasonName;
    }

    @Override
    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    @Override
    public Integer getCropYear() {
        return cropYear;
    }

    @Override
    public void setCropYear(Integer cropYear) {
        this.cropYear = cropYear;
    }

    @Override
    public String getPointUUID() {
        return pointUUID;
    }

    @Override
    public void setPointUUID(String pointUUID) {
        this.pointUUID = pointUUID;
    }

    @Override
    public String toString() {
        return "TempFarmMetaData{" +
                "locUUID='" + locUUID + '\'' +
                ", locType='" + locType + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", cropInfoJson='" + cropInfoJson + '\'' +
                ", ownerInfoJson='" + ownerInfoJson + '\'' +
                ", isDeleted=" + isDeleted +
                ", insertTs=" + insertTs +
                ", updateTs=" + updateTs +
                ", seasonName='" + seasonName + '\'' +
                ", cropYear=" + cropYear +
                ", pointUUID='" + pointUUID + '\'' +
                '}';
    }
}