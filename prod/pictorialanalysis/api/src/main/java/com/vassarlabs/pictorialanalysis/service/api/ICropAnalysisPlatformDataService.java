package com.vassarlabs.pictorialanalysis.service.api;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.pictorialanalysis.pojo.api.ITempFarmMetaData;

public interface ICropAnalysisPlatformDataService {
    public void insertCropAndOwnerInfo (ITempFarmMetaData tempFarmMetaData, Boolean isUpdate) throws DSPException;

    public ITempFarmMetaData getTempFarmMetaData(String pointUUID, Integer cropYear, String seasonName) throws DSPException;
}
