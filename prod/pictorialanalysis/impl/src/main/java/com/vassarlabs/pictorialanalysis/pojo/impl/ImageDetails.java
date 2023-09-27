package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.google.gson.Gson;
import com.vassarlabs.pictorialanalysis.pojo.api.IImageDetails;

public class ImageDetails implements IImageDetails {

    /**
     * This accepts "width, height" (eg: "25,40")
     */
    private String dimension;

    /**
     * This accepts size of image in terms of bytes (B)
     */
    private String size;

    private Double bearingAngle;

    private static Gson GSON = new Gson();


    public ImageDetails() {
    }

    public ImageDetails(String dimension, String size) {
        this.dimension = dimension;
        this.size = size;
    }

    public ImageDetails(String dimension, String size, Double bearingAngle) {
        this.dimension = dimension;
        this.size = size;
        this.bearingAngle = bearingAngle;
    }

    @Override
    public String getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public Double getBearingAngle() {
        return bearingAngle;
    }

    @Override
    public void setBearingAngle(Double bearingAngle) {
        this.bearingAngle = bearingAngle;
    }

    public static String convertImageDetailsToJsonData(IImageDetails imageDetails) {
        return GSON.toJson(imageDetails);
    }

    public static ImageDetails convertJsonToTaskInputData(String imageDetails) {
        ImageDetails imageDetailsData = null;
        if (imageDetails != null && !imageDetails.isEmpty()) {
            imageDetailsData = (ImageDetails)GSON.fromJson(imageDetails, ImageDetails.class);
        }
        return imageDetailsData;
    }

    @Override
    public String toString() {
        return "ImageDetails{" +
                "dimension='" + dimension + '\'' +
                ", size='" + size + '\'' +
                ", bearingAngle=" + bearingAngle +
                '}';
    }
}
