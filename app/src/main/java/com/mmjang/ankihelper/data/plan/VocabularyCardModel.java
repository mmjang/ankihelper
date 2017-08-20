package com.mmjang.ankihelper.data.plan;

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
    private final int NUMBER_OF_MODEL_STRING = 3;

    private String front = "";
    private String css = "";
    private String back = "";

    String[] QFMT = new String[1];
    String[] AFMT = new String[1];
    String[] Cards = {"card1"};
    String CSS;
    String [] FILEDS = {
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
                front = defaultModelSplitted[0];
                css = defaultModelSplitted[1];
                back = defaultModelSplitted[2];
            }
            else{
                ;
            }
            QFMT[0] = front;
            AFMT[0] = back;
            CSS = css;

        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }
}

