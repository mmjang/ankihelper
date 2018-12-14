package com.mmjang.duckmemo.data.dict;

import java.util.Map;

/**
 * Created by liao on 2017/4/20.
 */

public class Definition {

    private Map<String, String> exportElements;
    private String displayHtml;

    public Definition(Map<String, String> expEle, String dspHtml) {
        exportElements = expEle;
        displayHtml = dspHtml;
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
}
