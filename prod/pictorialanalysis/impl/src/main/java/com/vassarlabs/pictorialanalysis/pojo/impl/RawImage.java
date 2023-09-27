package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.vassarlabs.pictorialanalysis.pojo.api.IImageDetails;
import com.vassarlabs.pictorialanalysis.pojo.api.IRawImage;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class RawImage implements IRawImage {


    private String imageUUID;

    private String imageName;

    private Long captureTs;

    private String imagePath;

    private String imageLocationUUID;

    private String imageLocationType;

    private String applicationUUID;

    private String imageSource;

    private String userUUID;

    private String imageLabel;

    @JsonIgnore
    private Boolean isDeleted;

    @JsonIgnore
    private Long insertTs;

    @JsonIgnore
    private Long updateTs;

    private IImageDetails imageDetails;

    /**
     * Latitude longitude seperated by ","
     */
    private String bearingPoint;

    private String seasonName;

    private Integer cropYear;

    private static Gson GSON = new Gson();

    public RawImage() {
    }

    public RawImage(String imageName, Long captureTs, String imagePath, String imageLocationUUID, String imageLocationType, String applicationUUID, String imageSource, String userUUID, String imageLabel, IImageDetails imageDetails) {
        this.imageName = imageName;
        this.captureTs = captureTs;
        this.imagePath = imagePath;
        this.imageLocationUUID = imageLocationUUID;
        this.imageLocationType = imageLocationType;
        this.applicationUUID = applicationUUID;
        this.imageSource = imageSource;
        this.userUUID = userUUID;
        this.imageLabel = imageLabel;
        this.imageDetails = imageDetails;
    }

    public RawImage(String imageName, Long captureTs, String imagePath, String imageLocationUUID, String imageLocationType, String applicationUUID, String imageSource, String userUUID, String imageLabel, IImageDetails imageDetails, String bearingPoint) {
        this.imageName = imageName;
        this.captureTs = captureTs;
        this.imagePath = imagePath;
        this.imageLocationUUID = imageLocationUUID;
        this.imageLocationType = imageLocationType;
        this.applicationUUID = applicationUUID;
        this.imageSource = imageSource;
        this.userUUID = userUUID;
        this.imageLabel = imageLabel;
        this.imageDetails = imageDetails;
        this.bearingPoint = bearingPoint;
    }

    public RawImage(String imageName, Long captureTs, String imagePath, String imageLocationUUID, String imageLocationType, String applicationUUID, String imageSource, String userUUID, String imageLabel, IImageDetails imageDetails, String bearingPoint, String seasonName, Integer cropYear) {
        this.imageName = imageName;
        this.captureTs = captureTs;
        this.imagePath = imagePath;
        this.imageLocationUUID = imageLocationUUID;
        this.imageLocationType = imageLocationType;
        this.applicationUUID = applicationUUID;
        this.imageSource = imageSource;
        this.userUUID = userUUID;
        this.imageLabel = imageLabel;
        this.imageDetails = imageDetails;
        this.bearingPoint = bearingPoint;
        this.seasonName = seasonName;
        this.cropYear = cropYear;
    }

    @Override
    public String getImageUUID() {
        return imageUUID;
    }

    @Override
    public void setImageUUID(String imageUUID) {
        this.imageUUID = imageUUID;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public Long getCaptureTs() {
        return captureTs;
    }

    @Override
    public void setCaptureTs(Long captureTs) {
        this.captureTs = captureTs;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String getImageLocationUUID() {
        return imageLocationUUID;
    }

    @Override
    public void setImageLocationUUID(String imageLocationUUID) {
        this.imageLocationUUID = imageLocationUUID;
    }

    @Override
    public String getImageLocationType() {
        return imageLocationType;
    }

    @Override
    public void setImageLocationType(String imageLocationType) {
        this.imageLocationType = imageLocationType;
    }

    @Override
    public String getApplicationUUID() {
        return applicationUUID;
    }

    @Override
    public void setApplicationUUID(String applicationUUID) {
        this.applicationUUID = applicationUUID;
    }

    @Override
    public String getImageSource() {
        return imageSource;
    }

    @Override
    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    @Override
    public String getUserUUID() {
        return userUUID;
    }

    @Override
    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    @Override
    public String getImageLabel() {
        return imageLabel;
    }

    @Override
    public void setImageLabel(String imageLabel) {
        this.imageLabel = imageLabel;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.isDeleted = deleted;
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
    public Long getUpdateTs() {return updateTs;
    }

    @Override
    public void setUpdateTs(Long updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public IImageDetails getImageDetails() {
        return imageDetails;
    }

    @Override
    public void setImageDetails(IImageDetails imageDetails) {
        this.imageDetails = imageDetails;
    }

    @Override
    public String getBearingPoint() {
        return bearingPoint;
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
    public void setBearingPoint(String bearingPoint) {
        this.bearingPoint = bearingPoint;
    }

    /**
     * This method accepts ImageDetails POJO and returns JSON string of it
     * @param imageDetails IImageDetails
     * @return
     */
    public static String getJsonForImageDetails(IImageDetails imageDetails) {
        return GSON.toJson(imageDetails);

    }

    /**
     * This method accepts JSON string of ImageDetails and returns ImageDetails POJO
     * @param imageDetails
     * @return
     */
    public static IImageDetails convertJsonToImageDetails(String imageDetails) {
        IImageDetails imageDetails1 = GSON.fromJson(imageDetails,ImageDetails.class);
        return imageDetails1;
    }


    @Override
    public String toString() {
        return "RawImage{" +
                "imageUUID='" + imageUUID + '\'' +
                ", imageName='" + imageName + '\'' +
                ", captureTs=" + captureTs +
                ", imagePath='" + imagePath + '\'' +
                ", imageLocationUUID='" + imageLocationUUID + '\'' +
                ", imageLocationType='" + imageLocationType + '\'' +
                ", applicationUUID='" + applicationUUID + '\'' +
                ", imageSource='" + imageSource + '\'' +
                ", userUUID='" + userUUID + '\'' +
                ", imageLabel='" + imageLabel + '\'' +
                ", isDeleted=" + isDeleted +
                ", insertTs=" + insertTs +
                ", updateTs=" + updateTs +
                ", imageDetails=" + imageDetails +
                ", bearingPoint='" + bearingPoint + '\'' +
                ", seasonName='" + seasonName + '\'' +
                ", cropYear=" + cropYear +
                '}';
    }
}
