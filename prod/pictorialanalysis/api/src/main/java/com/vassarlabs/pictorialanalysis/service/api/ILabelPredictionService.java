package com.vassarlabs.pictorialanalysis.service.api;

import java.util.List;
import java.util.Map;

public interface ILabelPredictionService {
    public boolean predictLabel(Map<String,String> imgUUIDVsImagePathMap, String modelPath, String userUUID, String taskInputData, String predictType);
}
