package com.mmjang.duckmemo.data.plan;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liao on 2017/3/19.
 */

public class VocabularyCardModel {
    private final String MODEL_FILE = "vocabulary_card_model.html";
    private final String MODEL_SPLITTER = "@@@";
    private final String CODING = "UTF-8";
    private final int NUMBER_OF_MODEL_STRING = 5;

    private String[] front = new String[2];
    private String css = "";
    private String[] back = new String[2];

    String[] QFMT = new String[2];
    String[] AFMT = new String[2];
    String[] Cards = {"recite", "type"};
    String CSS;
    public static final String [] FILEDS = {
            "单词",
            "音标",
            "释义",
            "笔记",
            "例句",
            "url",
            "发音"
    };


    VocabularyCardModel(Context ct){

        try {
            InputStream ips = ct.getResources().getAssets().open(MODEL_FILE);
            byte[] data = new byte[ips.available()];
            ips.read(data);
            String defaultModelStr = new String(data, CODING);
            String[] defaultModelSplitted = defaultModelStr.split(MODEL_SPLITTER);
            if(defaultModelSplitted.length == NUMBER_OF_MODEL_STRING) {
                front[0] = defaultModelSplitted[0];
                back[0] = defaultModelSplitted[1];
                front[1] = defaultModelSplitted[2];
                back[1] = defaultModelSplitted[3];
                css = defaultModelSplitted[4];
            }
            else{
                ;
            }
            QFMT[0] = front[0];
            QFMT[1] = front[1];
            AFMT[0] = back[0];
            AFMT[1] = back[1];
            CSS = css;

        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }
}

