package com.vassarlabs.pictorialanalysis.pojo.api;

public interface IRawImage{

    public String getImageUUID();

    public void setImageUUID(String imageUUID);

    public String getImageName();

    public void setImageName(String imageName);

    public Long getCaptureTs();

    public void setCaptureTs(Long captureTs);

    public String getImagePath();

    public void setImagePath(String imagePath);

    public String getImageLocationUUID();

    public void setImageLocationUUID(String imageLocationUUID);

    public String getImageLocationType();

    public void setImageLocationType(String imageLocationType);

    public String getApplicationUUID();

    public void setApplicationUUID(String applicationUUID);

    public String getImageSource();

    public void setImageSource(String imageSource);

    public String getUserUUID();

    public void setUserUUID(String userUUID);

    public String getImageLabel();

    public void setImageLabel(String imageLabel);

    public boolean isDeleted();

    public void setDeleted(Boolean deleted);

    public Long getInsertTs();

    public void setInsertTs(Long insertTs);

    public Long getUpdateTs();

    public void setUpdateTs(Long updateTs);

    public IImageDetails getImageDetails();

    public void setImageDetails(IImageDetails imageDetails);

    public String getBearingPoint();

    public String getSeasonName();

    public void setSeasonName(String seasonName);

    public Integer getCropYear();

    public void setCropYear(Integer cropYear);

    public void setBearingPoint(String bearingPoint);
}
