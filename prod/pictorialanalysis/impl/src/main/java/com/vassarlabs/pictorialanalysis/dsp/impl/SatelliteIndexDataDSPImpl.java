package com.vassarlabs.pictorialanalysis.dsp.impl;

import com.vassarlabs.common.dsp.context.DataStoreContext;
import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.dsp.rdbms.api.IRDBMSDataStore;
import com.vassarlabs.pictorialanalysis.dsp.api.ISatelliteIndexDataDSP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SatelliteIndexDataDSPImpl implements ISatelliteIndexDataDSP {

    @Autowired
    @Qualifier("business_data")
    protected IRDBMSDataStore dataStore;

    @Override
    public String getRecentDateForAoiUUIDAndEntityName(String aoiUUID, String entityName) throws DSPException {
        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Map<String,String>> dateVsSatelliteData = new HashMap<>();
        String date = null;

        try {

            dataStoreOwnerKey = DataStoreContext.initReusableDataStoreContext(dataStore);

            String sql = "SELECT date FROM cga_satellite_index_data WHERE aoi_uuid = ? AND entity_name = ? ORDER BY date DESC LIMIT 1;";

            ps = dataStore.createPreparedStatement(sql);
            ps.setString(1, aoiUUID);
            ps.setString(2, entityName);
            System.out.println("In getRecentDateForAoiUUID:: Prepared Statement:" + ps);

            rs = ps.executeQuery();

            if(rs.next()) {
                date = rs.getString("date");
            }

        }catch (SQLException se) {
            throw new DSPException("SatelliteIndexDataDSPImpl :: getRecentDateForAoiUUIDAndEntityName(), "
                    + "unable to get date with aoiUUID: " + aoiUUID + " and entityName: " + entityName, se);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // Do Nothing
                }
                rs = null;
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    // Do Nothing
                }
                ps = null;
            }
            DataStoreContext.clearDataStoreContext(dataStoreOwnerKey);
        }
        return date;
    }

    @Override
    public LinkedHashMap<String, Map<String, String>> getSatelliteIndexDataForStartAndEndDate(String aoiUUID, String entityName, Date startDate, Date endDate) throws DSPException {
        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LinkedHashMap<String, Map<String,String>> dateVsSatelliteData = new LinkedHashMap<>();

        try {

            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);

            String sql = "SELECT entity_value,date,entity_filepath FROM cga_satellite_index_data WHERE aoi_uuid = ? and entity_name = ? and date between ? and ? order by date;";

            ps = dataStore.createPreparedStatement(sql);
            ps.setString(1, aoiUUID);
            ps.setString(2, entityName);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            System.out.println("In getSatelliteIndexData:: Prepared Statement:" + ps);

            rs = ps.executeQuery();

            while(rs.next()) {

                String date = rs.getString("date");
                String entityValue = rs.getString("entity_value");
                String entityFilepath = rs.getString("entity_filepath");
                Map<String,String> entityValueVsEntityFilepath = new HashMap<>();
                entityValueVsEntityFilepath.put("entityValue",entityValue);
                entityValueVsEntityFilepath.put("entityFilepath",entityFilepath);
                dateVsSatelliteData.put(date,entityValueVsEntityFilepath);
            }


        }catch (SQLException se) {
            throw new DSPException("SatelliteIndexDataDSPImpl :: getSatelliteIndexData(), "
                    + "unable to get SatelliteIndexData with aoiUUID: " + aoiUUID + " and entityName: " + entityName, se);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // Do Nothing
                }
                rs = null;
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    // Do Nothing
                }
                ps = null;
            }
            DataStoreContext.clearDataStoreContext(dataStoreOwnerKey);
        }
        return dateVsSatelliteData;
    }
}
