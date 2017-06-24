package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liao on 2017/5/6.
 */

public class Cloze implements IDictionary {
    private static final String[] EXP_ELE_LIST = new String[]{
    };

    public Cloze(Context context) {

    }

    public String getDictionaryName() {
        return "制作填空";
    }

    public String getIntroduction() {
        return "无释义，用户快速制作填空";
    }

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    public List<Definition> wordLookup(String key) {
        ArrayList<Definition> result = new ArrayList<>();
        result.add(new Definition(
                new HashMap<String, String>(),
                "制作填空卡片"
        ));
        return result;
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

}
