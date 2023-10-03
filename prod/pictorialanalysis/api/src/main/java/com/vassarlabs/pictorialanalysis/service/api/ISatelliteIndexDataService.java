package com.vassarlabs.pictorialanalysis.service.api;

import com.vassarlabs.common.dsp.err.DSPException;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public interface ISatelliteIndexDataService {

    public String getRecentDateForAoiUUIDAndEntityName(String aoiUUID, String entityName) throws DSPException;

    public LinkedHashMap<String, Map<String, String>> getSatelliteIndexDataForStartAndEndDate(String aoiUUID, String entityName, Long startTs, Long endTs) throws DSPException, ParseException;

    public void deleteCropIndexData(String aoiUUID) throws DSPException;
}
