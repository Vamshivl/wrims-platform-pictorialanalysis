package com.vassarlabs.applicationRun;


import com.vassarlabs.boundarydetection.service.api.IBoundaryPredictionService;
import com.vassarlabs.boundarydetection.service.impl.BoundaryPredictionService;
import com.vassarlabs.common.dsp.err.DSPException;
import com.vassarlabs.common.init.err.AppInitializationException;
import com.vassarlabs.common.init.service.RESTInitServiceImpl;
import com.vassarlabs.common.init.service.api.IApplicationInitService;
import com.vassarlabs.config.spring.AppContext;
import com.vassarlabs.location.service.impl.LocationHierarchyServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplicationRun {

    private static ApplicationContext ctx;

    public static void main(String[] args) {
        try {
            IApplicationInitService applicationInitService = null;
            applicationInitService = AppContext.getApplicationContext().getBean(RESTInitServiceImpl.class);
            applicationInitService.initialize();

            System.out.println("ApplicationRun Started!!");

           test();


        } catch (AppInitializationException e) {
            throw new RuntimeErrorException(new Error(e),
                    "Error initializing application");
        } catch (DSPException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void test() throws DSPException {
        LocationHierarchyServiceImpl ls = AppContext.getApplicationContext().getBean(LocationHierarchyServiceImpl.class);
        System.out.println("XXXXXXXXXX: "+ls.getLocationTypeIDNameMap());
        System.out.println("XXXXXXXXXX: "+ls.getLocNameForLocUUID("9553a7af-56f8-495f-ba0c-c2d2324e6759","KLSW"));

        Map<String, List<String>> parentChildMap = ls.getAllLocForParentChildTypes("STATE", "RAINFALL");
    }
}
