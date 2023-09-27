package com.vassarlabs.pictorialanalysis.service.api;

import com.vassarlabs.common.utils.err.InvalidDataFoundException;
import com.vassarlabs.pictorialanalysis.pojo.api.IFilterParams;
import com.vassarlabs.pictorialanalysis.pojo.api.ITrainingParams;

public interface IModelTrainingService {

    boolean trainPAModel(ITrainingParams trainingParams) throws InvalidDataFoundException;
}
