package com.vassarlabs.pictorialanalysis.dsp.impl;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.dsp.context.DataStoreContext;
import com.vassarlabs.common.dsp.err.FetchingObjectException;
import com.vassarlabs.common.dsp.err.ObjectCreationException;
import com.vassarlabs.common.dsp.rdbms.api.IRDBMSDataStore;
import com.vassarlabs.pictorialanalysis.dsp.api.IRawImageDSP;
import com.vassarlabs.pictorialanalysis.pojo.api.IImageDetails;
import com.vassarlabs.pictorialanalysis.pojo.api.IRawImage;
import com.vassarlabs.pictorialanalysis.pojo.impl.RawImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RawImageDSPImpl implements IRawImageDSP {

    @Autowired
    @Qualifier("business_data")
    protected IRDBMSDataStore dataStore;

    @Override
    public void insertRawImage(List<IRawImage> rawImages) throws DSPException {

        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;
        Long startTs = null;
        Long currTs = System.currentTimeMillis();
        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);
            transactionId = dataStore.beginTransaction();

            String sql = "INSERT INTO pa_raw_images(uuid,name,capture_ts,image_path,image_location_uuid,image_location_type,application_uuid,image_src,user_id,image_label,image_detail_json,deleted,insert_ts,update_ts,bearing_point,season_name,crop_year)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" + "::jsonb" + ", ?, ?, ?,ST_SetSRID(ST_Point(?,?),4326),?,?)";

            ps = dataStore.createPreparedStatement(sql);

            for (IRawImage rawImage : rawImages) {


                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2,rawImage.getImageName());
                if(rawImage.getCaptureTs()!=null){
                    ps.setLong(3,rawImage.getCaptureTs());
                }else {
                    ps.setLong(3,currTs);
                }
                ps.setString(4,rawImage.getImagePath());
                ps.setString(5,rawImage.getImageLocationUUID());
                ps.setString(6,rawImage.getImageLocationType());
                ps.setString(7,rawImage.getApplicationUUID());
                ps.setString(8,rawImage.getImageSource());
                ps.setString(9,rawImage.getUserUUID());
                ps.setString(10,rawImage.getImageLabel());
                ps.setObject(11,RawImage.getJsonForImageDetails(rawImage.getImageDetails()));
                ps.setBoolean(12,false);
                ps.setLong(13,currTs);
                ps.setLong(14,currTs);

                if(rawImage.getBearingPoint()!=null) {
                    ps.setDouble(15, Double.parseDouble(rawImage.getBearingPoint().split(",")[0].trim()));
                    ps.setDouble(16, Double.parseDouble(rawImage.getBearingPoint().split(",")[1].trim()));
                }else {
                    ps.setNull(15, Types.DOUBLE);
                    ps.setNull(16, Types.DOUBLE);
                }
                ps.setObject(17,rawImage.getSeasonName());
                ps.setObject(18,rawImage.getCropYear());
                System.out.println("ps : " + ps);
                ps.addBatch();
            }
            startTs = System.currentTimeMillis();
            ps.executeBatch();
            dataStore.commitTransaction(transactionId);
            result = true;

        } catch (SQLException se) {
            System.out.println("RM: RawImageDSPImpl - insertRawImage, " + " Error while inserting rawImages Data records =" + rawImages + " : Exception : " + se.getMessage());
            se.printStackTrace();
            throw new ObjectCreationException("Error inserting rawImages Data records =" + rawImages, se);
        } finally {
            if (!result) {
                dataStore.rollbackTransaction(transactionId);
            }
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
            System.out.println("Info: RawImageDSPImpl - insertRawImage, Inserted " + rawImages.size() + " rows in time " + (System.currentTimeMillis()-startTs) + "ms");
        }
    }

    @Override
    public List<IRawImage> getListOfRawImagesDSP(Long startTs, Long endTs, String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException {

        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<IRawImage> listOfRawImages = new ArrayList<>();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);

            StringBuilder sqlQuery = new StringBuilder();

            sqlQuery.append("SELECT * FROM pa_raw_images WHERE application_uuid = ? AND image_location_uuid IN (");

            for (int i = 0; i < locationUUIDs.size(); i++) {
                if (i > 0 && i < locationUUIDs.size()) {
                    sqlQuery.append(",");
                }
                sqlQuery.append("'").append(locationUUIDs.get(i)).append("'");
            }
            sqlQuery.append(") AND (capture_ts BETWEEN ? AND ?) AND (deleted= ? OR deleted IS NULL)");

            if (imgSrc != null && !imgSrc.isEmpty()) {
                sqlQuery.append(" AND (image_src = ?)");
            }

            if (imgLabel != null && !imgLabel.isEmpty()) {
                sqlQuery.append(" AND (image_label = ?)");
            }

            ps = dataStore.createPreparedStatement(sqlQuery.toString());

            ps.setString(1, applicationUUID);
            ps.setLong(2, startTs);
            ps.setLong(3, endTs);
            ps.setBoolean(4, false);

            if (imgSrc != null && !imgSrc.isEmpty() && imgLabel!=null && !imgLabel.isEmpty()) {
                ps.setString(5, imgSrc);
                ps.setString(6, imgLabel);
            }else if (imgSrc != null && !imgSrc.isEmpty()) {
                ps.setString(5, imgSrc);
            }else if (imgLabel != null && !imgLabel.isEmpty()) {
                ps.setString(5, imgLabel);
            }

            System.out.println("getListOfRawImagesDSP :: Prepared Statement: " + ps);

            rs = ps.executeQuery();

            while (rs.next()) {
                IRawImage rawImage = new RawImage();
                listOfRawImages.add(decodeRawImage(rs, rawImage));
            }
        }catch (SQLException e) {
            throw new DSPException("RawImageDSP :: getListOfRawImagesDSP(), "
                    + "unable to get a Raw Images with user start time =" + startTs  + ", end time =" + endTs + " and applicationUUID: " + applicationUUID + " and locations: " + locationUUIDs, e);
        }finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {}
                rs = null;
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {}
                ps = null;
            }

            DataStoreContext.clearDataStoreContext(dataStoreOwnerKey);
        }
        return listOfRawImages;
    }

    @Override
    public List<IRawImage> getListOfRawImagesDSP(String applicationUUID, List<String> locationUUIDs, String imgSrc, String imgLabel)
            throws DSPException {

        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<IRawImage> listOfRawImages = new ArrayList<>();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);

            StringBuilder sqlQuery = new StringBuilder();

            sqlQuery.append("SELECT * FROM pa_raw_images WHERE application_uuid = ? AND image_location_uuid IN (");

            for (int i = 0; i < locationUUIDs.size(); i++) {
                if (i > 0 && i < locationUUIDs.size()) {
                    sqlQuery.append(",");
                }
                sqlQuery.append("'").append(locationUUIDs.get(i)).append("'");
            }
            sqlQuery.append(") AND (deleted= ? OR deleted IS NULL)");

            if (imgSrc != null && !imgSrc.isEmpty()) {
                sqlQuery.append(" AND (image_src = ?)");
            }

            if (imgLabel != null && !imgLabel.isEmpty()) {
                sqlQuery.append(" AND (image_label = ?)");
            }

            ps = dataStore.createPreparedStatement(sqlQuery.toString());

            ps.setString(1, applicationUUID);
            ps.setBoolean(2, false);

            if (imgSrc != null && !imgSrc.isEmpty() && imgLabel!=null && !imgLabel.isEmpty()) {
                ps.setString(3, imgSrc);
                ps.setString(4, imgLabel);
            }else if (imgSrc != null && !imgSrc.isEmpty()) {
                ps.setString(3, imgSrc);
            }else if (imgLabel != null && !imgLabel.isEmpty()) {
                ps.setString(3, imgLabel);
            }

            System.out.println("getListOfRawImagesDSP :: Prepared Statement: " + ps);

            rs = ps.executeQuery();

            while (rs.next()) {
                IRawImage rawImage = new RawImage();
                listOfRawImages.add(decodeRawImage(rs, rawImage));
            }
        }catch (SQLException e) {
            throw new DSPException("RawImageDSP :: getListOfRawImagesDSP(), "
                    + "unable to get a Raw Images with applicationUUID: " + applicationUUID + " and locations: " + locationUUIDs, e);
        }finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {}
                rs = null;
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {}
                ps = null;
            }

            DataStoreContext.clearDataStoreContext(dataStoreOwnerKey);
        }

        return listOfRawImages;
    }

    private IRawImage decodeRawImage(ResultSet rs, IRawImage rawImage)
            throws SQLException {

        rawImage.setImageUUID(rs.getString("uuid"));
        rawImage.setImageName(rs.getString("name"));
        rawImage.setCaptureTs(rs.getLong("capture_ts"));
        rawImage.setImagePath(rs.getString("image_path"));
        rawImage.setImageLocationUUID(rs.getString("image_location_uuid"));
        rawImage.setImageLocationType(rs.getString("image_location_type"));
        rawImage.setApplicationUUID(rs.getString("application_uuid"));
        rawImage.setImageSource(rs.getString("image_src"));
        rawImage.setUserUUID(rs.getString("user_id"));
        rawImage.setImageLabel(rs.getString("image_label"));
        rawImage.setDeleted(rs.getBoolean("deleted"));
        rawImage.setInsertTs(rs.getLong("insert_ts"));
        rawImage.setUpdateTs(rs.getLong("update_ts"));
        IImageDetails imageDetails = RawImage.convertJsonToImageDetails(rs.getString("image_detail_json"));
        rawImage.setImageDetails(imageDetails);
//        rawImage.setImageDetails_V2(rs.getString("image_detail_json"));

        return rawImage;
    }

    @Override
    public LinkedHashMap<String, String> getImagePaths(List<String> imageUUIDs) throws DSPException {

        LinkedHashMap<String, String> imagePaths = new LinkedHashMap<>();
        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            dataStoreOwnerKey = DataStoreContext.initReusableDataStoreContext(dataStore);


            String sql = "select * from pa_raw_images where uuid = any (?) and (deleted=false OR deleted IS NULL)";
            ps = dataStore.createPreparedStatement(sql);

            Array array = ps.getConnection().createArrayOf("VARCHAR", imageUUIDs.toArray(new Object[imageUUIDs.size()]));

            ps.setArray(1,array);

            System.out.println("PS::"+ps);
            rs = ps.executeQuery();



            while(rs.next() ) {

                String imageUUID = rs.getString("uuid");
                String imagePath = rs.getString("image_path");
                imagePaths.put(imageUUID , imagePath);
            }

            return imagePaths;
        } catch (SQLException se) {
            System.out.println("Error retrieving list of imagePath record ");
            throw new FetchingObjectException(
                    "Error retrieving list of imagePath record " + ": Exception : " + se);
        } catch (DSPException e) {
            System.out.println("Error retrieving list of imagePath record ");
            throw new DSPException("Error retrieving list of imagePath record " + ": Exception : " + e);
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
    }

    @Override
    public Map<String, IRawImage> getImagesByImageUUIDs(List<String> imageUUIDs) throws DSPException {

        Map<String, IRawImage> resultMap = new HashMap<>();
        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            dataStoreOwnerKey = DataStoreContext.initReusableDataStoreContext(dataStore);

            String sql = "select * from pa_raw_images where uuid = any (?) and (deleted=false OR deleted IS NULL)";
            ps = dataStore.createPreparedStatement(sql);

            Array array = ps.getConnection().createArrayOf("VARCHAR", imageUUIDs.toArray(new Object[imageUUIDs.size()]));

            ps.setArray(1,array);

            System.out.println("PS::"+ps);
            rs = ps.executeQuery();

            while(rs.next() ) {

                IRawImage rawImage = new RawImage();
                resultMap.put(rs.getString("uuid"), decodeRawImage(rs , rawImage));
            }

            return resultMap;
        } catch (SQLException se) {
            System.out.println("Error retrieving list of imagePath record ");
            throw new FetchingObjectException(
                    "Error retrieving list of image record " + ": Exception : " + se);
        } catch (DSPException e) {
            System.out.println("Error retrieving list of images record ");
            throw new DSPException("Error retrieving list of image record " + ": Exception : " + e);
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
    }

    @Override
    public List<IRawImage> getRawImagesByLocUUID(String locUUID, int year, String season, String source) throws DSPException {

        List<IRawImage> resultMap = new ArrayList<>();
        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            dataStoreOwnerKey = DataStoreContext.initReusableDataStoreContext(dataStore);

            String sql = "select * from pa_raw_images where image_location_uuid = ? and crop_year = ? and season_name = ?";
            if(source!=null && !source.isBlank()){
                sql+= " and image_src = ?";
            }
            sql+= " and (deleted=false OR deleted IS NULL)";

            ps = dataStore.createPreparedStatement(sql);

            ps.setString(1,locUUID);
            ps.setInt(2, year);
            ps.setString(3, season);
            if(source!=null && !source.isBlank()) {
                ps.setString(4, source);
            }
            System.out.println("PS::"+ps);
            rs = ps.executeQuery();

            while(rs.next() ) {
                IRawImage rawImage = new RawImage();
                resultMap.add(decodeRawImage(rs , rawImage));
            }

            return resultMap;
        } catch (SQLException se) {
            System.out.println("Error retrieving list of imagePath record ");
            throw new FetchingObjectException(
                    "Error retrieving list of image record " + ": Exception : " + se);
        } catch (DSPException e) {
            System.out.println("Error retrieving list of images record ");
            throw new DSPException("Error retrieving list of image record " + ": Exception : " + e);
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
    }

    @Override
    public void deleteRawImageByImageUUIDsList(List<String> imageUUIDsList) throws DSPException {

        System.out.println("In RawImageDSPImpl :: deleteRawImageByImageUUIDsList :: imageUUIDsList size :: "+imageUUIDsList.size());
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        boolean result = false;
        Long startTs = null;
        int affectedRows = 0;


        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);
            transactionId = dataStore.beginTransaction();

            String sql = "DELETE FROM pa_raw_images where uuid = any (?);";
            ps = dataStore.createPreparedStatement(sql);

            Array array = ps.getConnection().createArrayOf("VARCHAR", imageUUIDsList.toArray(new Object[imageUUIDsList.size()]));

            ps.setArray(1,array);
            startTs = System.currentTimeMillis();
            System.out.println("Sql Query is + " + ps);

            affectedRows = ps.executeUpdate();
            System.out.println(affectedRows + " rows are deleted");
            dataStore.commitTransaction(transactionId);
            result = true;

        } catch (SQLException se) {
            System.out.println("RM: RawImageDSPImpl - deleteRawImageByImageUUID(), " + " Error while deleting rawImages Data records for uuids of size = " + imageUUIDsList.size() + " : Exception : " + se.getMessage());
            se.printStackTrace();
            throw new ObjectCreationException("Error while deleting rawImages Data records for rawImages of size = " + imageUUIDsList.size(), se);
        } finally {
            if (!result) {
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
            System.out.println("Info: RawImageDSPImpl - deleteRawImageByImageUUID() deleted " + imageUUIDsList.size() + " rows in time " + (System.currentTimeMillis()-startTs) + "ms");
        }
    }

    @Override
    public void insertTempRawImage(List<IRawImage> rawImages) throws DSPException {

        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;
        Long startTs = null;
        Long currTs = System.currentTimeMillis();
        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);
            transactionId = dataStore.beginTransaction();

            String sql = "INSERT INTO temp_pa_raw_images(uuid,name,capture_ts,image_path,image_location_uuid,image_location_type,application_uuid,image_src,user_id,image_label,image_detail_json,deleted,insert_ts,update_ts,bearing_point,season_name,crop_year)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" + "::jsonb" + ", ?, ?, ?,ST_SetSRID(ST_Point(?,?),4326),?,?)";

            ps = dataStore.createPreparedStatement(sql);

            for (IRawImage rawImage : rawImages) {


                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2,rawImage.getImageName());
                if(rawImage.getCaptureTs()!=null){
                    ps.setLong(3,rawImage.getCaptureTs());
                }else {
                    ps.setLong(3,currTs);
                }
                ps.setString(4,rawImage.getImagePath());
                ps.setString(5,rawImage.getImageLocationUUID());
                ps.setString(6,rawImage.getImageLocationType());
                ps.setString(7,rawImage.getApplicationUUID());
                ps.setString(8,rawImage.getImageSource());
                ps.setString(9,rawImage.getUserUUID());
                ps.setString(10,rawImage.getImageLabel());
                ps.setObject(11,RawImage.getJsonForImageDetails(rawImage.getImageDetails()));
                ps.setBoolean(12,false);
                ps.setLong(13,currTs);
                ps.setLong(14,currTs);

                if(rawImage.getBearingPoint()!=null) {
                    ps.setDouble(15, Double.parseDouble(rawImage.getBearingPoint().split(",")[0].trim()));
                    ps.setDouble(16, Double.parseDouble(rawImage.getBearingPoint().split(",")[1].trim()));
                }else {
                    ps.setNull(15, Types.DOUBLE);
                    ps.setNull(16, Types.DOUBLE);
                }
                ps.setObject(17,rawImage.getSeasonName());
                ps.setObject(18,rawImage.getCropYear());
                System.out.println("ps : " + ps);
                ps.addBatch();
            }
            startTs = System.currentTimeMillis();
            ps.executeBatch();
            dataStore.commitTransaction(transactionId);
            result = true;

        } catch (SQLException se) {
            System.out.println("RM: RawImageDSPImpl - insertTempRawImage, " + " Error while inserting rawImages Data records =" + rawImages + " : Exception : " + se.getMessage());
            se.printStackTrace();
            throw new ObjectCreationException("Error inserting rawImages Data records =" + rawImages, se);
        } finally {
            if (!result) {
                dataStore.rollbackTransaction(transactionId);
            }
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
            System.out.println("Info: RawImageDSPImpl - insertTempRawImage, Inserted " + rawImages.size() + " rows in time " + (System.currentTimeMillis()-startTs) + "ms");
        }
    }

    @Override
    public List<IRawImage> getTempRawImagesByPointUUID(String locUUID, int year, String season, String source) throws DSPException {

        List<IRawImage> resultMap = new ArrayList<>();
        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            dataStoreOwnerKey = DataStoreContext.initReusableDataStoreContext(dataStore);

            String sql = "select * from temp_pa_raw_images where image_location_uuid = ? and crop_year = ? and season_name = ?";
            if(source!=null && !source.isBlank()){
                sql+= " and image_src = ?";
            }
            sql+= " and (deleted=false OR deleted IS NULL)";

            ps = dataStore.createPreparedStatement(sql);

            ps.setString(1,locUUID);
            ps.setInt(2, year);
            ps.setString(3, season);
            if(source!=null && !source.isBlank()) {
                ps.setString(4, source);
            }
            System.out.println("PS::"+ps);
            rs = ps.executeQuery();

            while(rs.next() ) {
                IRawImage rawImage = new RawImage();
                resultMap.add(decodeRawImage(rs , rawImage));
            }

            return resultMap;
        } catch (SQLException se) {
            System.out.println("Error retrieving list of imagePath record ");
            throw new FetchingObjectException(
                    "Error retrieving list of image record " + ": Exception : " + se);
        } catch (DSPException e) {
            System.out.println("Error retrieving list of images record ");
            throw new DSPException("Error retrieving list of image record " + ": Exception : " + e);
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
    }

    @Override
    public Map<String , Integer > getLocationWiseRawImagesCount(String userUUID, String locType,String cropYear, String season, String source) throws DSPException {
        Map<String , Integer > resultMap = new HashMap<>();
        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            dataStoreOwnerKey = DataStoreContext.initReusableDataStoreContext(dataStore);

            String sql = "select image_location_UUID , count(uuid) as imgCount from pa_raw_images " +
                    "WHERE image_location_type=? "
//                    +"AND (capture_ts BETWEEN ? AND ?)"
                    ;

            sql+= " and image_src = ? and user_id = ?";

            if(cropYear != null){
                sql += " and crop_year = ?";
                if(season != null){
                    sql += " and season_name = ?";
                }
            }

            sql+= " and (deleted=false OR deleted IS NULL)";

            sql += " group by image_location_uuid ";
            ps = dataStore.createPreparedStatement(sql);

            ps.setString(1,locType);
            ps.setString(2, source);
            ps.setString(3,userUUID);

            if(cropYear != null) {
                ps.setInt(4, Integer.parseInt(cropYear));
                if(season != null) {
                    ps.setString(5, season);
                }
            }
            System.out.println("PS::"+ps);
            rs = ps.executeQuery();

            while(rs.next() ) {

                resultMap.put(rs.getString("image_location_UUID"),
                        rs.getInt("imgCount"));
            }

            return resultMap;
        } catch (SQLException se) {
            System.out.println("Error retrieving list of imagePath record ");
            throw new FetchingObjectException(
                    "Error retrieving list of image record " + ": Exception : " + se);
        } catch (DSPException e) {
            System.out.println("Error retrieving list of images record ");
            throw new DSPException("Error retrieving list of image record " + ": Exception : " + e);
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
    }
}
