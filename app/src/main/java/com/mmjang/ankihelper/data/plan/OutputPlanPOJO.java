package com.mmjang.ankihelper.data.plan;

import com.mmjang.ankihelper.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.Map;

/**
 * Created by liao on 2017/4/20.
 */

public class OutputPlanPOJO{
//    private int order;
    private String planName;
    private String dictionaryKey;
    private long outputDeckId;
    private long outputModelId;
    private String fieldsMap;

//    public void setOrder(int order) {
//        this.order = order;
//    }
//
//    public int getOrder() {
//        return order;
//    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setDictionaryKey(String dictKey) {
        this.dictionaryKey = dictKey;
    }

    public String getDictionaryKey() {
        return dictionaryKey;
    }

    public void setOutputDeckId(long outputDeckId) {
        this.outputDeckId = outputDeckId;
    }

    public long getOutputDeckId() {
        return outputDeckId;
    }

    public void setOutputModelId(long outputModelId) {
        this.outputModelId = outputModelId;
    }

    public long getOutputModelId() {
        return outputModelId;
    }

    public void setFieldsMap(Map<String, String> fieldsMap) {
        this.fieldsMap = Utils.fieldsMap2Str(fieldsMap);
    }

    public Map<String, String> getFieldsMap() {
        return Utils.fieldsStr2Map(fieldsMap);
    }

    public String getFieldsMapString(){
        return fieldsMap;
    }

    public void setFieldsMapString(String s){
        this.fieldsMap = s;
    }
}
