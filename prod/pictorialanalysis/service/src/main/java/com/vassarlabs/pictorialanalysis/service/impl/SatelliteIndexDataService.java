package com.vassarlabs.pictorialanalysis.service.impl;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.pictorialanalysis.dsp.api.ISatelliteIndexDataDSP;
import com.vassarlabs.pictorialanalysis.service.api.ISatelliteIndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SatelliteIndexDataService implements ISatelliteIndexDataService {

    @Autowired
    private ISatelliteIndexDataDSP satelliteIndexDataDSP;

    @Override
    public String getRecentDateForAoiUUIDAndEntityName(String aoiUUID, String entityName) throws DSPException {
        System.out.println("In SatelliteIndexDataService :: getRecentDateForAoiUUIDAndEntityName");
        return  satelliteIndexDataDSP.getRecentDateForAoiUUIDAndEntityName(aoiUUID,entityName);
    }

    @Override
    public LinkedHashMap<String, Map<String, String>> getSatelliteIndexDataForStartAndEndDate(String aoiUUID, String entityName, Long startTs, Long endTs) throws DSPException, ParseException {
        System.out.println("In SatelliteIndexDataService :: getSatelliteIndexDataForStartAndEndDate");
        Date startDate = new Date(startTs);
        Date endDate = new Date(endTs);
        return satelliteIndexDataDSP.getSatelliteIndexDataForStartAndEndDate(aoiUUID, entityName, startDate, endDate);
    }
}
