package com.vassarlabs.pictorialanalysis.dsp.api;

import com.vassarlabs.common.dsp.err.DSPException;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public interface ISatelliteIndexDataDSP {

    public String getRecentDateForAoiUUIDAndEntityName(String aoiUUID, String entityName) throws DSPException;

    public LinkedHashMap<String, Map<String,String>> getSatelliteIndexDataForStartAndEndDate(String aoiUUID, String entityName, Date startDate, Date endDate) throws DSPException;
}
