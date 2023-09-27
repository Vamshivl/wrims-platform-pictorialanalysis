package com.vassarlabs.pictorialanalysis.dsp.impl;

import com.vassarlabs.pictorialanalysis.dsp.api.ILabeledImageDSP;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vassarlabs.pictorialanalysis.pojo.impl.LabeledImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vassarlabs.common.dsp.context.DataStoreContext;
import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.dsp.rdbms.api.IRDBMSDataStore;

import com.vassarlabs.pictorialanalysis.pojo.api.ILabeledImage;

@Component
public class LabeledImageDSP implements ILabeledImageDSP{

    @Autowired
    @Qualifier("business_data")
    protected IRDBMSDataStore dataStore;

    @Override
    public void insertLabeledImagesDSP(List<ILabeledImage> newLabeledImagesList)
        throws DSPException {

        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        long startTs = System.currentTimeMillis();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);
            long currentTs = DataStoreContext.getCurrentTS();
            transactionId = dataStore.beginTransaction();

            String sqlQuery = "INSERT INTO pa_labeled_images(raw_image_uuid, "
                    + "image_bbox, image_label, label_type, deleted) VALUES("
                    + " ?, to_jsonb(? :: jsonb), ?, ?, ?);";

            ps = dataStore.createPreparedStatement(sqlQuery);

            for (ILabeledImage labeledImage: newLabeledImagesList) {
                ps.setString(1, labeledImage.getRawImageUUID());
                ps.setString(2, labeledImage.getImageBBOX());
                ps.setString(3, labeledImage.getImageLabel());
                ps.setString(4, labeledImage.getLabelType());
                ps.setBoolean(5, false);
//                ps.setLong(5, currentTs);
//                ps.setLong(6, currentTs);

                ps.addBatch();
            }

            System.out.println("ps: " + ps);

            ps.executeBatch();

            startTs = System.currentTimeMillis();
            dataStore.commitTransaction(transactionId);
            transactionResult = true;
        }catch (SQLException se) {
            throw new DSPException("RM: LabeledImageDSP - insertLabeledImagesDSP(), "
                    + "Error while inserting labeled images = " + newLabeledImagesList, se);

        } finally {
            if (!transactionResult) {
                dataStore.rollbackTransaction(transactionId);
            }
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
            System.out.println("RM: Inserted " + newLabeledImagesList.size() + " rows in time " + (System.currentTimeMillis() - startTs));
        }
    }

    @Override
    public List<ILabeledImage> getListOfLabeledImagesDSP (Long startTs, Long endTs, String applicationUUID)
            throws DSPException {

        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<ILabeledImage> listOfLabeledImages = new ArrayList<>();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);
            String sqlQuery = "SELECT * FROM pa_labeled_images WHERE "
                    + "insert_ts BETWEEN ? AND ? AND (deleted= ? OR deleted IS NULL);";


            ps = dataStore.createPreparedStatement(sqlQuery);

//            ps.setString(1, applicationUUID);
            ps.setLong(1, startTs);
            ps.setLong(2, endTs);
            ps.setBoolean(3, false);

            System.out.println("getListOfLabeledImagesDSP :: Prepared Statement: " + ps);

            rs = ps.executeQuery();


            while (rs.next()) {
                listOfLabeledImages.add(decodeToLabeledImage(rs));
            }

        }catch (SQLException e) {
            throw new DSPException("LabeledImageDSP :: getListOfLabeledImagesDSP(), "
                    + "unable to get a Labeled Images with user start time =" + startTs  + ", end time =" + endTs + " and applicationUUID: " + applicationUUID, e);
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

        return listOfLabeledImages;
    }


    @Override
    public List<ILabeledImage> getListOfLabeledImagesByUUIDsAndLabelTypeForTraining (List<String> imageUUIDs, String labelType)
        throws DSPException {

        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        List<ILabeledImage> listOfLabeledImages = new ArrayList<>();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);

            StringBuilder sqlQuery = new StringBuilder();

            sqlQuery.append("SELECT * FROM pa_labeled_images WHERE raw_image_uuid IN (");
            for (int i = 0; i < imageUUIDs.size(); i++) {
                if (i > 0) {
                    sqlQuery.append(",");
                }
                sqlQuery.append("'").append(imageUUIDs.get(i)).append("'");
            }
            sqlQuery.append(") AND (label_type=?) AND (deleted= ? OR deleted IS NULL)");

//            if (labelType!=null && !labelType.isEmpty()) {
//                sqlQuery.append(") AND (label_type=?) AND (deleted= ? OR deleted IS NULL)");
//            }else {
//                sqlQuery.append(") AND (deleted= ? OR deleted IS NULL)");
//            }

            ps = dataStore.createPreparedStatement(sqlQuery.toString());
            ps.setString(1, labelType);
            ps.setBoolean(2, false);

//            if (labelType!=null && !labelType.isEmpty()) {
//                ps.setString(1, labelType);
//                ps.setBoolean(2, false);
//            }else {
//                ps.setBoolean(1, false);
//            }

            System.out.println("getListOfLabeledImagesByUUIDsAndLabelTypeForTraining :: Prepared Statement: " + ps);

            rs = ps.executeQuery();

            while (rs.next()) {
                listOfLabeledImages.add(decodeToLabeledImage(rs));
            }
        }catch (SQLException e) {
            throw new DSPException("LabeledImageDSP :: getListOfLabeledImagesByUUIDsForTraining(), "
                    + "unable to get a Labeled Images with imageUUIDs =" + imageUUIDs , e);
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
        return listOfLabeledImages;
    }

    @Override
    public List<ILabeledImage> getListOfLabeledImages (Long startTs, Long endTs, List<String> imageUUIDs, String labelType)
            throws DSPException {

        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<ILabeledImage> listOfLabeledImages = new ArrayList<>();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);
            StringBuilder sqlQuery = new StringBuilder();

            sqlQuery.append("SELECT * FROM pa_labeled_images WHERE raw_image_uuid IN (");
            if(imageUUIDs.size() == 0){
                sqlQuery.append("'").append("'");
            }else {
                for (int i = 0; i < imageUUIDs.size(); i++) {
                    if (i > 0) {
                        sqlQuery.append(",");
                    }
                    sqlQuery.append("'").append(imageUUIDs.get(i)).append("'");
                }
            }

            sqlQuery.append(") AND (label_type=?) AND (deleted= ? OR deleted IS NULL) AND (update_ts BETWEEN ? AND ?)");


            ps = dataStore.createPreparedStatement(sqlQuery.toString());

            ps.setString(1, labelType);
            ps.setBoolean(2, false);
            ps.setLong(3, startTs);
            ps.setLong(4, endTs);

            System.out.println("getListOfLabeledImagesDSP :: Prepared Statement: " + ps);

            rs = ps.executeQuery();

            while (rs.next()) {
                listOfLabeledImages.add(decodeToLabeledImage(rs));
            }
        }catch (SQLException e) {
            throw new DSPException("LabeledImageDSP :: getListOfLabeledImagesDSP(), "
                    + "unable to get a Labeled Images with user start time =" + startTs  + ", end time =" + endTs + " and labelType: " + labelType + " and imageUUIDs: " + imageUUIDs, e);
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

        return listOfLabeledImages;
    }

    @Override
    public List<ILabeledImage> getLabeledImagesByTaskUUID(String taskUUID) throws DSPException {
        System.out.println("In LabeledImageDSP :: getLabeledImagesByTaskUUID :: taskUUID :: " + taskUUID);

        String dataStoreOwnerKey = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ILabeledImage> listOfLabeledImages = new ArrayList<>();

        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);

            String sqlQuery = "SELECT * FROM pa_labeled_images WHERE "
                    + "task_uuid = ? AND (deleted= ? OR deleted IS NULL);";

            ps = dataStore.createPreparedStatement(sqlQuery);
            ps.setString(1, taskUUID);
            ps.setBoolean(2, false);
            System.out.println("getListOfLabeledImagesDSP :: Prepared Statement: " + ps);

            rs = ps.executeQuery();
            while (rs.next()) {
                listOfLabeledImages.add(decodeToLabeledImage(rs));
            }

        }catch (SQLException e) {
            throw new DSPException("LabeledImageDSP :: getListOfLabeledImagesDSP(), "
                    + "unable to get a Labeled Images with taskUUID "  + taskUUID + " ", e);
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

        return listOfLabeledImages;
    }


    @Override
    public boolean deleteLabeledImagesForARawImageDSP (List<String> rawImageUUIDs)
            throws DSPException {
        boolean transactionResult = false;
        String dataStoreOwnerKey = null;
        String transactionId = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try {
            dataStoreOwnerKey = DataStoreContext.initDataStoreContext(dataStore);
            transactionId = dataStore.beginTransaction();

            String sqlQuery = "DELETE FROM pa_labeled_images WHERE "
                    + "raw_image_uuid = any(?);";

            ps = dataStore.createPreparedStatement(sqlQuery);
            Array array = ps.getConnection().createArrayOf("VARCHAR", rawImageUUIDs.toArray(new Object[rawImageUUIDs.size()]));

            ps.setArray(1, array);
            System.out.println("deleteLabeledImagesForARawImage :: Prepared Statement: " + ps);

            int effectedRows = ps.executeUpdate();
            System.out.println("mysql prepared statement executed :: "+ effectedRows +" rows effected");

            dataStore.commitTransaction(transactionId);
            transactionResult = true;
        }catch (SQLException e) {
            throw new DSPException("LabeledImageDSP :: deleteLabeledImagesForARawImage(), "
                    + "unable to delete labeled images with raw image uuid =" + rawImageUUIDs, e);
        }finally {
            if (!transactionResult) {
                dataStore.rollbackTransaction(transactionId);
            }

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
        return true;
    }

    private ILabeledImage decodeToLabeledImage(ResultSet rs) throws SQLException {

        ILabeledImage labeledImage = new LabeledImage();

        labeledImage.setLabeledImageID(rs.getLong("id"));
        labeledImage.setRawImageUUID(rs.getString("raw_image_uuid"));
        labeledImage.setImageBBOX(rs.getString("image_bbox"));
        labeledImage.setImageLabel(rs.getString("image_label"));
        labeledImage.setLabelType(rs.getString("label_type"));
        labeledImage.setTaskUUID(rs.getString("task_uuid"));
        labeledImage.setDeleted(rs.getBoolean("deleted"));
        labeledImage.setInsertTS(rs.getLong("insert_ts"));
        labeledImage.setUpdateTS(rs.getLong("update_ts"));

        return labeledImage;
    }

}
