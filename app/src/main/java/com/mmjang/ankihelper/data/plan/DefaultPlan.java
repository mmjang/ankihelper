package com.mmjang.ankihelper.data.plan;

import android.content.Context;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.dict.Collins;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by liao on 2017/7/23.
 */

public class DefaultPlan {
    private static final String DEFAULT_VOCABULARY_MODEL_NAME = "划词助手默认单词模版";
    private static final String DEFAULT_CLOZE_MODEL_NAME = "ankihelper_default_cloze_card";
    private static final String DEFAULT_DECK_NAME = "划词助手默认牌组";
    public static final String DEFAULT_PLAN_NAME = "Collins(默认方案)";
    AnkiDroidHelper mAnkidroid;
    VocabularyCardModel vc;
    Context mContext;
    public DefaultPlan(Context context){
        mAnkidroid = MyApplication.getAnkiDroid();
        mContext = context;
    }

    public void addDefaultPlan(){
        Collins collins = new Collins(mContext);
        String[] elements = new String[]{
                "单词",
                "音标",
                "释义",
                "笔记",
                "加粗的例句",
                "URL",
                "有道美式发音"
        };
        String [] FILEDS = {
                "单词",
                "音标",
                "释义",
                "笔记",
                "例句",
                "url",
                "发音"
        };
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
        for(int i = 0; i < FILEDS.length; i ++){
            fieldMap.put(FILEDS[i], elements[i]);
        }
        OutputPlan defaultPlan = new OutputPlan();
        defaultPlan.setPlanName(DEFAULT_PLAN_NAME);
        defaultPlan.setOutputModelId(getDefaultModelId());
        defaultPlan.setOutputDeckId(getDefaultDeckId());
        defaultPlan.setDictionaryKey(collins.getDictionaryName());
        defaultPlan.setFieldsMap(fieldMap);
        defaultPlan.save();
    }


    long getDefaultDeckId(){
        Map<Long, String> deckList = mAnkidroid.getApi().getDeckList();
        for(Long id : deckList.keySet()){
            if(deckList.get(id).equals(DEFAULT_DECK_NAME)){
                return id;
            }
        }
        return mAnkidroid.getApi().addNewDeck(DEFAULT_DECK_NAME);
    }

    long getDefaultModelId(){
    	Long mid = mAnkidroid.findModelIdByName(DEFAULT_VOCABULARY_MODEL_NAME, VocabularyCardModel.FILEDS.length);
        if (mid == null) {
	        String modelName = DEFAULT_VOCABULARY_MODEL_NAME;
	        vc = new VocabularyCardModel(mContext);
	        mid = mAnkidroid.getApi().addNewCustomModel(modelName,
	                vc.FILEDS,
	                vc.Cards,
	                vc.QFMT,
	                vc.AFMT,
	                vc.CSS,
	                null,
	                null
	        );
        }
        return mid;
    }

    static private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
}
