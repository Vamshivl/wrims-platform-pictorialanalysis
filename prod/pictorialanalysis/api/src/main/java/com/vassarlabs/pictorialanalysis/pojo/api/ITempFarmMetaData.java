package com.vassarlabs.pictorialanalysis.pojo.api;

public interface ITempFarmMetaData {

    public String getLocUUID();

    public void setLocUUID(String locUUID);

    public String getLocType();

    public void setLocType(String locType);

    public String getLatitude();

    public void setLatitude(String latitude);

    public String getLongitude();

    public void setLongitude(String longitude);

    public String getCropInfoJson();

    public void setCropInfoJson(String cropInfoJson);

    public String getOwnerInfoJson();

    public void setOwnerInfoJson(String ownerInfoJson);

    public Boolean getDeleted();

    public void setDeleted(Boolean deleted);

    public Long getInsertTs();

    public void setInsertTs(Long insertTs);

    public Long getUpdateTs();

    public void setUpdateTs(Long updateTs);

    public String getSeasonName();

    public void setSeasonName(String seasonName);

    public Integer getCropYear();

    public void setCropYear(Integer cropYear);

    public String getPointUUID();

    public void setPointUUID(String pointUUID);
}
