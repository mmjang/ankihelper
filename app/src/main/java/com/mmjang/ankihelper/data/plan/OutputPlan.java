package com.mmjang.ankihelper.data.plan;

import com.mmjang.ankihelper.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liao on 2017/4/20.
 */

public class OutputPlan extends DataSupport {
    private String planName;
    private String dictionaryKey;
    private long outputDeckId;
    private long outputModelId;
    private String fieldsMap;

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

    public void setFieldsMap(HashMap<String, String> fieldsMap) {
        this.fieldsMap = Utils.fieldsMap2Str(fieldsMap);
    }

    public Map<String, String> getFieldsMap() {
        return Utils.fieldsStr2Map(fieldsMap);
    }
}
