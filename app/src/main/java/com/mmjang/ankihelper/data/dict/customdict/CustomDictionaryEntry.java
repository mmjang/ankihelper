package com.mmjang.ankihelper.data.dict.customdict;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mmjang.ankihelper.data.dict.CustomDictionary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liao on 2017/8/17.
 */

public class CustomDictionaryEntry {
    private static int MIN_ELEMENT_NUMBER = 2;
    private String headWord;
    private List<String> extraHeadWordList; //maybe there're more than one headword point to the same definition;
    private LinkedHashMap<String, String> elementsMap; //use linkedHashMap to preserve order

    public CustomDictionaryEntry(String hwd, LinkedHashMap<String, String> lhm){
        if(hwd == null || hwd.trim().isEmpty())
        {
            throw new IllegalArgumentException("headword can't be null or empty!");
        }
        if(lhm == null || lhm.size() < MIN_ELEMENT_NUMBER){
            throw new IllegalArgumentException("elements map can't be null or size less than " + MIN_ELEMENT_NUMBER);
        }

        headWord = hwd;
        extraHeadWordList = new ArrayList<>();
        elementsMap = lhm;
    }

    public CustomDictionaryEntry(String hwd, List<String> extras, LinkedHashMap<String, String> lhm){
        this(hwd, lhm);
        if(extras == null){
            extraHeadWordList = new ArrayList<>();
        }else{
            extraHeadWordList = extras;
        }
    }

    public String getHeadWord(){
        return headWord;
    }

    public List<String> getExtraHeadWordList(){
        return extraHeadWordList;
    }

    public Map<String, String> getElementsMap(){
        return elementsMap;
    }
}
