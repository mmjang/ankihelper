package com.mmjang.ankihelper.data.dict;

import java.util.Map;

/**
 * Created by liao on 2017/4/20.
 */

public class Definition {

    private Map<String, String> exportElements;
    private String displayHtml;
    private String imageUrl;
    private String imageName;

    private String audioUrl;
    private String audioName;

    public Definition(Map<String, String> expEle, String dspHtml) {
        exportElements = expEle;
        displayHtml = dspHtml;
    }

    public Definition(Map<String, String> expEle, String dspHtml, String imageUrl, String imageName, String audioUrl, String audioName) {
        exportElements = expEle;
        displayHtml = dspHtml;
        this.imageUrl = imageUrl;
        this.imageName = imageName;

        this.audioUrl = audioUrl;
        this.audioName = audioName;
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

    public String getImageUrl(){
        return imageUrl;
    }

    public String getImageName(){
        return imageName;
    }

    public String getAudioUrl(){
        return audioUrl;
    }

    public String getAudioName(){
        return audioName;
    }
}
