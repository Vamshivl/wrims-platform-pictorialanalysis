package com.vassarlabs.pictorialanalysis.service.impl;

import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.utils.StringUtils;
import com.vassarlabs.pictorialanalysis.dsp.api.ICropAnalysisDSP;
import com.vassarlabs.pictorialanalysis.pojo.api.ITempFarmMetaData;
import com.vassarlabs.pictorialanalysis.service.api.ICropAnalysisPlatformDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CropAnalysisPlatformDataService implements ICropAnalysisPlatformDataService {

    @Autowired
    protected ICropAnalysisDSP cropAnalysisDSP;

    @Override
    public void insertCropAndOwnerInfo(ITempFarmMetaData tempFarmMetaData, Boolean isUpdate) throws DSPException {
        System.out.println("Entering :: CropAnalysisPlatformDataService : insertCropAndOwnerInfo");
        if(isUpdate){
            cropAnalysisDSP.updateCropAndOwnerInfoDSP(tempFarmMetaData);
        }
        else {
            cropAnalysisDSP.insertCropAndOwnerInfoDSP(tempFarmMetaData);
        }
        System.out.println("Returning :: CropAnalysisPlatformDataService : insertCropAndOwnerInfo");
    }

    @Override
    public ITempFarmMetaData getTempFarmMetaData(String pointUUID, Integer cropYear, String seasonName) throws DSPException {
        System.out.println("In CropAnalysisPlatformDataService :: getTempFarmMetaData() :: pointUUID :: " + pointUUID + " cropYear :: " + cropYear + " seasonName :: " + seasonName);

        if(StringUtils.isNullOrEmpty(pointUUID) || cropYear==null || StringUtils.isNullOrEmpty(seasonName)){
            System.out.println("params is null or empty!!");
            return null;
        }
        return cropAnalysisDSP.getTempFarmMetaData(pointUUID, cropYear, seasonName);
    }
}
