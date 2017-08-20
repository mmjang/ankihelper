package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liao on 2017/8/11.
 */

public class CustomDictionary implements IDictionary {

    private Context mContext;
    private CustomDictionaryDbHelper mDbHelper;
    private int mDictId;
    public CustomDictionaryInformation mDictInformation;

    public CustomDictionary(Context context, CustomDictionaryDbHelper dbHelper, int dictId){
        mContext = context;
        mDbHelper = dbHelper;
        mDictId = dictId;
        mDictInformation = mDbHelper.getDictInfo(dictId);
    }

    public int getId(){
        return mDictId;
    }

    @Override
    public String getDictionaryName() {
        return mDictInformation.getDictName();
    }

    @Override
    public String getIntroduction() {
        return mDictInformation.getDictIntro();
    }

    @Override
    public String[] getExportElementsList() {
        return mDictInformation.getFields();
    }

    @Override
    public List<Definition> wordLookup(String key) {
        key = keyCleanup(key);
        List<Definition> re = queryDefinition(key);
        if(mDictInformation.getDictLang().equals("en")) {
            String[] deflectResult = FormsUtil.getInstance(mContext).getForms(key);
            if (deflectResult.length >= 0) {
                for (String deflectedWrod : deflectResult) {
                    re.addAll(queryDefinition(deflectedWrod));
                }
            }
        }

        return re;
    }

    @Override
    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(context, layout, null,
                        new String[] {CustomDictionaryDbHelper.getHeadwordColumnName()},
                        new int[] {android.R.id.text1},
                        0
                        );
        adapter.setFilterQueryProvider(
                new FilterQueryProvider() {
                    @Override
                    public Cursor runQuery(CharSequence constraint) {
                        return mDbHelper.getFilterCursor(mDictId, constraint.toString());
                    }
                }
        );

        adapter.setCursorToStringConverter(
                new SimpleCursorAdapter.CursorToStringConverter() {
                    @Override
                    public CharSequence convertToString(Cursor cursor) {
                        return cursor.getString(1);
                    }
                }
        );

        return adapter;
    }

    private List<Definition> queryDefinition(String q){
        ArrayList<Definition> re = new ArrayList<>();
        List<String[]> results =  mDbHelper.queryHeadword(mDictId, q);
        for(String[] result : results){
            re.add(fromResultsToDefinition(result));
        }
        return re;
    }

    private Definition fromResultsToDefinition(String[] result){
        String[] fields = getExportElementsList();
        HashMap<String, String> eleMap = new HashMap<>();
        for(int i = 0; i < fields.length; i ++){
            eleMap.put(fields[i], result[i]);
        }
        String displayedHtml = "";
        String tmpl = mDictInformation.getDefTpml();
        if(tmpl.isEmpty()){   //no tmpl just join fields
            StringBuilder sb = new StringBuilder();
            for(String s : result){
                sb.append(s);
            }
            displayedHtml = sb.toString();
        }else{
            displayedHtml = renderTmpl(tmpl, eleMap);
        }
        return new Definition(eleMap, displayedHtml);
    }


    private String renderTmpl(String tmpl, Map<String, String> dataMap) {
        tmpl = tmpl.trim();
        for (String key : dataMap.keySet()) {
            tmpl = tmpl.replace("{{" + key + "}}", dataMap.get(key));
        }
        return tmpl;
    }

    private String keyCleanup(String key) {
        return key.trim().replaceAll("[,.!?()\"'“”’？]", "").toLowerCase();
    }
}
