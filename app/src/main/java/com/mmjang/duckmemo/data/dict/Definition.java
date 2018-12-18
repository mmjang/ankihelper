package com.mmjang.duckmemo.data.dict;

import java.util.Map;

/**
 * Created by liao on 2017/4/20.
 */

public class Definition {

    private String word = "";
    private Map<String, String> exportElements;
    private String displayHtml = "";
    private String combinedDefinition = "";

    public Definition(Map<String, String> expEle, String dspHtml) {
        exportElements = expEle;
        displayHtml = dspHtml;
    }

    public Definition(String word, Map<String, String> expEle, String dspHtml, String combined) {
        this.word = word;
        exportElements = expEle;
        displayHtml = dspHtml;
        combinedDefinition = combined;
    }

    public String getExportElement(String key) {
        return exportElements.get(key);
    }

    public boolean hasElement(String key) {
        return exportElements.containsKey(key);
    }

    public String getDisplayHtml() {
        return displayHtml;
    }

    public String getCombinedDefinition(){
        return combinedDefinition;
    }

    public String getWord(){
        return word;
    }
}
