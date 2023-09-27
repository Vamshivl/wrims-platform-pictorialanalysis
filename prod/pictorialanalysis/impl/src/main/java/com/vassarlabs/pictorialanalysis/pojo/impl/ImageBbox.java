package com.vassarlabs.pictorialanalysis.pojo.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.vassarlabs.pictorialanalysis.pojo.api.IImageBbox;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageBbox implements IImageBbox {

    @JsonProperty("bbox_x")
    @SerializedName("bbox_x")
    private Double Bbox_X;

    @JsonProperty("bbox_y")
    @SerializedName("bbox_y")
    private Double Bbox_Y;

    @JsonProperty("bbox_width")
    @SerializedName("bbox_width")
    private Double Bbox_W;

    @JsonProperty("bbox_height")
    @SerializedName("bbox_height")
    private Double Bbox_H;


    public ImageBbox(Double bbox_X, Double bbox_Y, Double bbox_W, Double bbox_H) {
        Bbox_X = bbox_X;
        Bbox_Y = bbox_Y;
        Bbox_W = bbox_W;
        Bbox_H = bbox_H;
    }

    public Double getBbox_X() {
        return Bbox_X;
    }

    public void setBbox_X(Double bbox_X) {
        Bbox_X = bbox_X;
    }

    public Double getBbox_Y() {
        return Bbox_Y;
    }

    public void setBbox_Y(Double bbox_Y) {
        Bbox_Y = bbox_Y;
    }

    public Double getBbox_W() {
        return Bbox_W;
    }

    public void setBbox_W(Double bbox_W) {
        Bbox_W = bbox_W;
    }

    public Double getBbox_H() {
        return Bbox_H;
    }

    public void setBbox_H(Double bbox_H) {
        Bbox_H = bbox_H;
    }
}
