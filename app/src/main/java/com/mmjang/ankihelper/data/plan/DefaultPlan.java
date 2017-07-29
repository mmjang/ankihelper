package com.mmjang.ankihelper.data.plan;

import android.content.Context;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;

import java.util.Map;

/**
 * Created by liao on 2017/7/23.
 */

public class DefaultPlan {
    private static final String DEFAULT_VOCABULARY_MODEL_NAME = "ankihelper_default_vocabulary_card";
    private static final String DEFAULT_CLOZE_MODEL_NAME = "ankihelper_default_cloze_card";
    private static final String DEFAULT_DECK_NAME = "ankihelper_default_deck";
    AnkiDroidHelper mAnkidroid;
    public DefaultPlan(Context context){
        mAnkidroid = MyApplication.getAnkiDroid();
    }


    boolean hasDefaultDeck(){
        Map<Long, String> deckList = mAnkidroid.getApi().getDeckList();
        for(Long id : deckList.keySet()){
            if(deckList.get(id).equals(DEFAULT_DECK_NAME)){
                return true;
            }
        }
        return false;
    }

    boolean hasModel(String model){
        //mAnkidroid.getApi().updateNoteFields()
        return true;
    }
}
