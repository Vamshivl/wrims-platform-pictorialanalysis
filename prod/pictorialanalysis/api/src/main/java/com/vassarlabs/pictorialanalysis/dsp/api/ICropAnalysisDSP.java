package com.vassarlabs.pictorialanalysis.dsp.api;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.pictorialanalysis.pojo.api.ITempFarmMetaData;

public interface ICropAnalysisDSP{
    public void insertCropAndOwnerInfoDSP (ITempFarmMetaData tempFarmMetaData) throws DSPException;

    public void updateCropAndOwnerInfoDSP(ITempFarmMetaData tempFarmMetaData) throws DSPException;

    public ITempFarmMetaData getTempFarmMetaData(String pointUUID, Integer cropYear, String seasonName) throws DSPException;
}
