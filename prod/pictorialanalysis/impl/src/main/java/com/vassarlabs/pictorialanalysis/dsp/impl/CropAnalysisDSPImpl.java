package com.vassarlabs.pictorialanalysis.dsp.impl;

import com.vassarlabs.common.dsp.context.DataStoreContext;
import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.dsp.err.ObjectCreationException;
import com.vassarlabs.common.dsp.err.FetchingObjectException;
import com.vassarlabs.common.dsp.rdbms.api.IRDBMSDataStore;
import com.vassarlabs.pictorialanalysis.dsp.api.ICropAnalysisDSP;
import com.vassarlabs.pictorialanalysis.pojo.api.ITempFarmMetaData;
import com.vassarlabs.pictorialanalysis.pojo.impl.TempFarmMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class CropAnalysisDSPImpl implements ICropAnalysisDSP {

    @Autowired
    @Qualifier("business_data")
    protected IRDBMSDataStore dataStore;

    @Override
    public void insertCropAndOwnerInfoDSP(ITempFarmMetaData tempFarmMetaData) throws DSPException {

        System.out.println("CropAnalysisDSPImpl :: insertCropAndOwnerInfoDSP");
        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        boolean result = false;
        long startTs = System.currentTimeMillis();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);

            String sqlQuery = "INSERT INTO temp_farm_meta_data (point_geom, loc_uuid, loc_type, crop_info_json, " +
                    "owner_info_json, crop_year, season_name, point_uuid) VALUES (ST_SetSRID(ST_POINT(?, ?),4326), ?, ?, " +
                    "?::jsonb, ?::jsonb, ?, ?, ?) ";
            ps = dataStore.createPreparedStatement(sqlQuery);

            ps.setDouble(1, Double.parseDouble(tempFarmMetaData.getLatitude()));
            ps.setDouble(2, Double.parseDouble(tempFarmMetaData.getLongitude()));
            ps.setString(3, tempFarmMetaData.getLocUUID());
            ps.setString(4, tempFarmMetaData.getLocType());
            ps.setString(5, tempFarmMetaData.getCropInfoJson());
            ps.setString(6, tempFarmMetaData.getOwnerInfoJson());
            ps.setInt(7, tempFarmMetaData.getCropYear());
            ps.setString(8, tempFarmMetaData.getSeasonName());
            ps.setString(9, tempFarmMetaData.getPointUUID());

            System.out.println("SQL Query: " + ps);

            transactionId = dataStore.beginTransaction();
            ps.executeUpdate();
            dataStore.commitTransaction(transactionId);
            transactionResult = true;
            System.out.println("RM: Inserted tempFarmMetaData 1 row in time " + (System.currentTimeMillis() - startTs) + "ms ");

        } catch (SQLException se) {
            System.out.println("RM:: CropAnalysisDSPImpl - insertCropAndOwnerInfoDSP, " + " Error while inserting");
            se.printStackTrace();
            throw new ObjectCreationException("Error inserting crop and owner info: " + tempFarmMetaData, se);
        } finally {
            if (!transactionResult) {
                dataStore.rollbackTransaction(transactionId);
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
    }

    @Override
    public void updateCropAndOwnerInfoDSP(ITempFarmMetaData tempFarmMetaData) throws DSPException {
        System.out.println("CropAnalysisDSPImpl :: updateCropAndOwnerInfoDSP");

        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        boolean result = false;
        long startTs = System.currentTimeMillis();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);

            String sqlQuery = "UPDATE temp_farm_meta_data SET crop_info_json=?::jsonb , owner_info_json=?::jsonb , update_ts=? " +
                    " WHERE point_uuid=? and crop_year=? and season_name=?";
            ps = dataStore.createPreparedStatement(sqlQuery);


            ps.setString(1, tempFarmMetaData.getCropInfoJson());
            ps.setString(2, tempFarmMetaData.getOwnerInfoJson());
            ps.setLong(3,System.currentTimeMillis());
            ps.setString(4, tempFarmMetaData.getPointUUID());
            ps.setInt(5, tempFarmMetaData.getCropYear());
            ps.setString(6, tempFarmMetaData.getSeasonName());

            System.out.println("SQL Query: " + ps);

            transactionId = dataStore.beginTransaction();
            Integer affectedRows = ps.executeUpdate();
            System.out.println(affectedRows + " rows are updated");
            dataStore.commitTransaction(transactionId);
            transactionResult = true;
            System.out.println("RM: updated tempFarmMetaData 1 row in time " + (System.currentTimeMillis() - startTs) + " ms");
        } catch (SQLException se) {
            System.out.println("RM:: CropAnalysisDSPImpl - updateCropAndOwnerInfoDSP, " + " Error while updating");
            se.printStackTrace();
            throw new ObjectCreationException("Error updating crop and owner info: " + tempFarmMetaData, se);
        } finally {
            if (!transactionResult) {
                dataStore.rollbackTransaction(transactionId);
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
    }

    @Override
    public ITempFarmMetaData getTempFarmMetaData(String pointUUID, Integer cropYear, String seasonName) throws DSPException {
        System.out.println("In CropAnalysisDSPImpl :: getTempFarmMetaData() :: pointUUID :: " + pointUUID + " cropYear :: " + cropYear + " seasonName :: " + seasonName);

        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ITempFarmMetaData tempFarmMetaData = null;
        try {

            dataStoreOwnerKey = DataStoreContext.initReusableDataStoreContext(dataStore);

            String sql = "select * from temp_farm_meta_data where point_uuid=? and crop_year=? and " +
                    "season_name=? and (deleted=false OR deleted IS NULL)";
            ps = dataStore.createPreparedStatement(sql);

            ps.setString(1,pointUUID);
            ps.setInt(2, cropYear);
            ps.setString(3,seasonName);

            System.out.println("PS::"+ps);
            rs = ps.executeQuery();

            if(rs.next()) {
                tempFarmMetaData = new TempFarmMetaData();
                decodeTempFarmMetaData(rs,tempFarmMetaData);
            }

        } catch (SQLException se) {
            System.out.println("Error retrieving temp_farm_meta_data record for given params");
            throw new FetchingObjectException(
                    "Error retrieving temp_farm_meta_data record for given params " + ": Exception : " + se);
        } catch (DSPException e) {
            System.out.println("Error retrieving temp_farm_meta_data record for given params ");
            throw new DSPException("Error retrieving temp_farm_meta_data record for given params " + ": Exception : " + e);
        } finally {
            if(rs != null){
                try {
                    rs.close();
                }catch (SQLException e){} // Do nothing
                rs = null;
            }
            if(ps != null){
                try{
                    ps.close();
                }catch (SQLException e){} //Do nothing
                ps = null;
            }

            DataStoreContext.clearDataStoreContext(dataStoreOwnerKey);
            System.out.println("DataStore Context is cleared");
        }
        return tempFarmMetaData;
    }

    private void decodeTempFarmMetaData(ResultSet rs, ITempFarmMetaData tempFarmMetaData) throws SQLException {
        tempFarmMetaData.setPointUUID(rs.getString("point_uuid"));
        tempFarmMetaData.setLocUUID(rs.getString("loc_uuid"));
        tempFarmMetaData.setLocType(rs.getString("loc_type"));
        tempFarmMetaData.setCropYear(Integer.valueOf(rs.getString("crop_year")));
        tempFarmMetaData.setSeasonName(rs.getString("season_name"));
        tempFarmMetaData.setCropInfoJson(rs.getString("crop_info_json"));
        tempFarmMetaData.setOwnerInfoJson(rs.getString("owner_info_json"));
        tempFarmMetaData.setInsertTs(Long.valueOf(rs.getString("insert_ts")));
        tempFarmMetaData.setUpdateTs(Long.valueOf(rs.getString("update_ts")));
        tempFarmMetaData.setDeleted(Boolean.valueOf(rs.getString("deleted")));
    }
}
