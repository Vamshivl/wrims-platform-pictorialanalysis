package com.vassarlabs.pictorialanalysis.utils;

import com.vassarlabs.config.spring.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "pictorialanalysis")
@PropertySource(value = PAUtils.CONFIG_FILE_LOCATION , factory = YamlPropertySourceFactory.class)

public class PAConstantProperties {

    private String PYTHON_SCRIPT_CMD;
    private String PYTHON_SCRIPTS_DIR;
    private String PYTHON_PREDICTION_SCRIPT;
    private String PYTHON_TRAINING_SCRIPT;
    private String PYTHON_APP_SCRIPT;
    private String ANNOTATIONS_FILES_DIR;
    private String PREDICTION_OUTPUT_DIR;
    private Map<String,Integer> PA_LABLE_CODES;
    private String TRAINING_OUTPUT_WEIGHT_FILE_DIR;

    private Map<String, Map<String, String>> stateUUIDVsSeasonToSeasonStartMonthMap;
    private Map<String, LinkedHashMap<String,String>> linkedStateUUIDVsSeasonToSeasonStartMonthMap;
    private Map<String, Map<String,String>> stateUUIDVsMonthToSeasonMap;
    private Map<String, Map<String, String>> stateVsCropYearMap;

    private List<String> imageSources;
    public String getPYTHON_SCRIPT_CMD() {
        return PYTHON_SCRIPT_CMD;
    }

    public void setPYTHON_SCRIPT_CMD(String PYTHON_SCRIPT_CMD) {
        this.PYTHON_SCRIPT_CMD = PYTHON_SCRIPT_CMD;
    }

    public String getPYTHON_SCRIPTS_DIR() {
        return PYTHON_SCRIPTS_DIR;
    }

    public void setPYTHON_SCRIPTS_DIR(String PYTHON_SCRIPTS_DIR) {
        this.PYTHON_SCRIPTS_DIR = PYTHON_SCRIPTS_DIR;
    }

    public String getPYTHON_PREDICTION_SCRIPT() {
        return PYTHON_PREDICTION_SCRIPT;
    }

    public void setPYTHON_PREDICTION_SCRIPT(String PYTHON_PREDICTION_SCRIPT) {
        this.PYTHON_PREDICTION_SCRIPT = PYTHON_PREDICTION_SCRIPT;
    }

    public String getPYTHON_TRAINING_SCRIPT() {
        return PYTHON_TRAINING_SCRIPT;
    }

    public void setPYTHON_TRAINING_SCRIPT(String PYTHON_TRAINING_SCRIPT) {
        this.PYTHON_TRAINING_SCRIPT = PYTHON_TRAINING_SCRIPT;
    }

    public String getPYTHON_APP_SCRIPT() {
        return PYTHON_APP_SCRIPT;
    }

    public void setPYTHON_APP_SCRIPT(String PYTHON_APP_SCRIPT) {
        this.PYTHON_APP_SCRIPT = PYTHON_APP_SCRIPT;
    }

    public String getANNOTATIONS_FILES_DIR() {
        return ANNOTATIONS_FILES_DIR;
    }

    public void setANNOTATIONS_FILES_DIR(String ANNOTATIONS_FILES_DIR) {
        this.ANNOTATIONS_FILES_DIR = ANNOTATIONS_FILES_DIR;
    }

    public String getPREDICTION_OUTPUT_DIR() {
        return PREDICTION_OUTPUT_DIR;
    }

    public void setPREDICTION_OUTPUT_DIR(String PREDICTION_OUTPUT_DIR) {
        this.PREDICTION_OUTPUT_DIR = PREDICTION_OUTPUT_DIR;
    }

    public Map<String, Integer> getPA_LABLE_CODES() {
        return PA_LABLE_CODES;
    }

    public void setPA_LABLE_CODES(Map<String, Integer> PA_LABLE_CODES) {
        this.PA_LABLE_CODES = PA_LABLE_CODES;
    }

    public String getTRAINING_OUTPUT_WEIGHT_FILE_DIR() {
        return TRAINING_OUTPUT_WEIGHT_FILE_DIR;
    }

    public void setTRAINING_OUTPUT_WEIGHT_FILE_DIR(String TRAINING_OUTPUT_WEIGHT_FILE_DIR) {
        this.TRAINING_OUTPUT_WEIGHT_FILE_DIR = TRAINING_OUTPUT_WEIGHT_FILE_DIR;
    }

    public Map<String, Map<String, String>> getStateUUIDVsSeasonToSeasonStartMonthMap() {
        return stateUUIDVsSeasonToSeasonStartMonthMap;
    }

    public void setStateUUIDVsSeasonToSeasonStartMonthMap(Map<String, Map<String, String>> stateUUIDVsSeasonToSeasonStartMonthMap) {
        this.stateUUIDVsSeasonToSeasonStartMonthMap = stateUUIDVsSeasonToSeasonStartMonthMap;
    }

    public Map<String, LinkedHashMap<String, String>> getLinkedStateUUIDVsSeasonToSeasonStartMonthMap() {
        if (linkedStateUUIDVsSeasonToSeasonStartMonthMap==null || linkedStateUUIDVsSeasonToSeasonStartMonthMap.isEmpty()) {
            linkedStateUUIDVsSeasonToSeasonStartMonthMap = new LinkedHashMap<>();

            for (Map.Entry entry: stateUUIDVsSeasonToSeasonStartMonthMap.entrySet()) {
                TreeMap<Integer, Map<String, String>> customMap = new TreeMap<>();

                String stateKey = (String) entry.getKey();
                Map<String, String> stateSeasons = stateUUIDVsSeasonToSeasonStartMonthMap.get(stateKey);
                // System.out.println("stateKey: " + stateKey);
                // System.out.println("StateSeasons: " + stateSeasons);

                for (String seasonName: stateSeasons.keySet()) {
                    int index = Integer.parseInt(seasonName.substring(0, seasonName.indexOf('.')));//the key value is parsed as <index>.<actual key string>
                    customMap.put(index, Map.of(seasonName.substring(seasonName.indexOf('.')+1), stateSeasons.get(seasonName)));
                }

                // System.out.println("customMap: " + customMap);

                LinkedHashMap<String, String> stateSeasonData = new LinkedHashMap<>();

                for (Integer index: customMap.keySet()) {
                    // System.out.println("index: " + index);
                    Map<String, String> seasonData = customMap.get(index);
                    // System.out.println("Value: " + seasonData);

                    String seasonName = seasonData.keySet().iterator().next();
                    String seasonStart = seasonData.get(seasonName);

                    // System.out.println("seasonName: " + seasonName);
                    // System.out.println("seasonStart: " + seasonStart);

                    stateSeasonData.put(seasonName, seasonStart);
                }

                linkedStateUUIDVsSeasonToSeasonStartMonthMap.put(stateKey, stateSeasonData);
            }
        }
        stateUUIDVsSeasonToSeasonStartMonthMap = null;
        return linkedStateUUIDVsSeasonToSeasonStartMonthMap;
    }
    public Map<String,Map<String,List<String>>> getStateUUIDVsSeasonToListOfMonths(){
        Map<String,Map<String,List<String>>> data = new HashMap<>();
        Map<String,LinkedHashMap<String,String>> stateSeasonStartMonths = getLinkedStateUUIDVsSeasonToSeasonStartMonthMap();
        for (Map.Entry<String, LinkedHashMap<String, String>> details : stateSeasonStartMonths.entrySet()){
            String stateUUID = details.getKey();
            data.put(stateUUID,new HashMap<>());
            Map<String, String> seasonDetails = details.getValue();
            Map<String,List<String>> monthsList = convertToSeasonMonthsList(seasonDetails);
            data.put(stateUUID,monthsList);
        }
        return data;
    }
    private Map<String, List<String>> convertToSeasonMonthsList(Map<String, String> seasonStartMonths) {
        Map<String, List<String>> seasonMonthsListMap = new HashMap<>();
        List<String> allMonths = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        List<String> sortedStartMonths = new ArrayList<>(seasonStartMonths.values());
        if(sortedStartMonths.size()==1){
            seasonMonthsListMap.put(seasonStartMonths.keySet().toString(),new ArrayList<>(allMonths));
            return seasonMonthsListMap;
        }
       // Collections.sort(sortedStartMonths);

        for (int i = 0; i < sortedStartMonths.size(); i++) {

            int startIndex = allMonths.indexOf(sortedStartMonths.get(i));
            int endIndex = allMonths.indexOf(sortedStartMonths.get((i + 1) % sortedStartMonths.size()));
            List<String> seasonMonths = new ArrayList<>();
            while (startIndex != endIndex) {
                seasonMonths.add(allMonths.get(startIndex));
                startIndex = (startIndex + 1) % allMonths.size();
            }
            seasonMonthsListMap.put(getSeasonName(seasonStartMonths, sortedStartMonths.get(i)), seasonMonths);
        }
        return seasonMonthsListMap;
    }
    private String getSeasonName(Map<String, String> seasonStartMonths, String startMonth) {
        for (Map.Entry<String, String> entry : seasonStartMonths.entrySet()) {
            if (entry.getValue().equals(startMonth)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<String, Map<String, String>> getStateUUIDVsMonthToSeasonMap() {
        return stateUUIDVsMonthToSeasonMap;
    }

    public void setStateUUIDVsMonthToSeasonMap(Map<String, Map<String, String>> stateUUIDVsMonthToSeasonMap) {
        this.stateUUIDVsMonthToSeasonMap = stateUUIDVsMonthToSeasonMap;
    }

    public Map<String, Map<String, String>> getStateVsCropYearMap() {
        return stateVsCropYearMap;
    }

    public void setStateVsCropYearMap(Map<String, Map<String, String>> stateVsCropYearMap) {
        this.stateVsCropYearMap = stateVsCropYearMap;
    }


    public List<String> getImageSources() {
        return imageSources;
    }

    public void setImageSources(List<String> imageSources) {
        this.imageSources = imageSources;
    }

    @Override
    public String toString() {
        return "PAConstantProperties{" +
                "PYTHON_SCRIPT_CMD='" + PYTHON_SCRIPT_CMD + '\'' +
                ", PYTHON_SCRIPTS_DIR='" + PYTHON_SCRIPTS_DIR + '\'' +
                ", PYTHON_PREDICTION_SCRIPT='" + PYTHON_PREDICTION_SCRIPT + '\'' +
                ", PYTHON_TRAINING_SCRIPT='" + PYTHON_TRAINING_SCRIPT + '\'' +
                ", PYTHON_APP_SCRIPT='" + PYTHON_APP_SCRIPT + '\'' +
                ", ANNOTATIONS_FILES_DIR='" + ANNOTATIONS_FILES_DIR + '\'' +
                ", PREDICTION_OUTPUT_DIR='" + PREDICTION_OUTPUT_DIR + '\'' +
                ", PA_LABLE_CODES=" + PA_LABLE_CODES +
                ", TRAINING_OUTPUT_WEIGHT_FILE_DIR='" + TRAINING_OUTPUT_WEIGHT_FILE_DIR + '\'' +
                ", stateUUIDVsSeasonToSeasonStartMonthMap=" + stateUUIDVsSeasonToSeasonStartMonthMap +
                ", linkedStateUUIDVsSeasonToSeasonStartMonthMap=" + linkedStateUUIDVsSeasonToSeasonStartMonthMap +
                ", stateUUIDVsMonthToSeasonMap=" + stateUUIDVsMonthToSeasonMap +
                ", stateVsCropYearMap=" + stateVsCropYearMap +
                ", imageSources=" + imageSources +
                '}';
    }
}