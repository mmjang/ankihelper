package com.mmjang.ankihelperrefactor.app;
import android.content.Context;
import android.widget.Adapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liao on 2017/4/13.
 */

public interface IDictionary {
    String getDictionaryName();
    String getIntroduction();
    String[] getExportElementsList();
    List<Definition> wordLookup(String key);
    ListAdapter getAutoCompleteAdapter(Context context, int layout);
}
