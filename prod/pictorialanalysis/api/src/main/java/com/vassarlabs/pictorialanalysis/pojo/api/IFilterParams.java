package com.vassarlabs.pictorialanalysis.pojo.api;

public interface IFilterParams {

    public Long getStartDateTs();

    public void setStartDateTs(Long startDateTs);

    public Long getEndDateTs();

    public void setEndDateTs(Long endDateTs);

    public String getAppUUID();

    public void setAppUUID(String appUUID);

    public String getLocUUID();

    public void setLocUUID(String locUUID);

    public String getLocType();

    public void setLocType(String locType);

    public String getSource();

    public void setSource(String source);

    public String getLabelType();

    public void setLabelType(String labelType);
}
