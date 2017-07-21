package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liao on 2017/3/15.
 */

public class Collins extends SQLiteAssetHelper implements IDictionary {
    //private static final String DATABASE_NAME = ".db";
    private static final String DATABASE_NAME = "collins_v2.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_DICT = "dict";
    private static final String FIELD_HWD = "hwd";
    private static final String FIELD_DISPLAYED_HWD = "display_hwd";
    private static final String FIELD_PHRASE = "phrase";
    private static final String FIELD_PHONETICS = "phonetics";
    private static final String FIELD_SENSE = "sense";
    private static final String FIELD_EXT = "ext";
    private static final String FIELD_DEF_EN = "def_en";
    private static final String FIELD_DEF_CN = "def_cn";

    private static final String DICT_NAME = "柯林斯英汉双解";

    private SQLiteDatabase db;


    public Collins(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }

    private static final String[] EXP_ELE_LIST = new String[]{
            "单词",
            "音标",
            "释义"
    };

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return "柯林斯词典，释义简单，适合初学者。";
    }

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    public List<Definition> wordLookup(String key) {
        //db = getReadableDatabase(); // according to stackoverflow, it's alright to let the database open
        key = keyCleanup(key);
        List<Definition> re = queryDefinition(key);
        Log.d("", "单词需要查找变形表");
        String[] deflectResult = getForms(key);
        for (String s : deflectResult) {
            Log.d("", "已变形单词" + s);
        }
        if (deflectResult.length == 0) {
            //
        } else {
            for (String deflectedWord : deflectResult) {
                re.addAll(queryDefinition(deflectedWord));
            }
        }

        // db.close();
        return re;
    }

    /**
     * @param context this
     * @param layout  support_simple_spinner_dropdown_item
     * @return
     */
    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(context, layout,
                        null,
                        new String[]{FIELD_HWD},
                        new int[]{android.R.id.text1},
                        0
                );
        adapter.setFilterQueryProvider(
                new FilterQueryProvider() {
                    @Override
                    public Cursor runQuery(CharSequence constraint) {
                        return getFilterCursor(constraint.toString());
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

    /**
     * @param q word to lookup
     * @return a array of definitions, retrun ArrayList<>() if none was found
     */
    private ArrayList<Definition> queryDefinition(String q) {
        //SQLiteDatabase db = getReadableDatabase();
        ArrayList<Definition> re = new ArrayList<>();
        Cursor cursor = db.query(TABLE_DICT,
                new String[]{FIELD_HWD, FIELD_DISPLAYED_HWD, FIELD_PHRASE,
                        FIELD_PHONETICS, FIELD_SENSE, FIELD_EXT, FIELD_DEF_EN, FIELD_DEF_CN},
                FIELD_HWD + "=? COLLATE NOCASE", new String[]{q}, null, null, null);
        while (cursor.moveToNext()) {
            Definition def = getDefFromCursor(cursor);
            re.add(def);
        }
        return re;
    }

    private Definition getDefFromCursor(Cursor cursor) {
        HashMap<String, String> eleMap = new HashMap<>();
        String hwd = cursor.getString(0);
        // df.setDisplayedHeadWord(cursor.getString(1).trim());
        String phrase = cursor.getString(2).trim();
        String phonetics = cursor.getString(3).trim();
        String sense = cursor.getString(4).trim();
        String ext = cursor.getString(5).trim();
        String defEn = cursor.getString(6).trim();
        String defCn = cursor.getString(7).trim();

        //如果不是词组
        if (phrase.equals("")) {
            eleMap.put(EXP_ELE_LIST[0], hwd);
        } else {
            eleMap.put(EXP_ELE_LIST[0], phrase);
        }
        eleMap.put(EXP_ELE_LIST[1], phonetics);
        eleMap.put(EXP_ELE_LIST[2], sense + "<br/>" + ext + "<br/>" + defEn + "<br/>" + defCn);

        String displayHtml;
        if (phrase.equals("")) {
            StringBuilder sb = new StringBuilder();
            if (defEn.startsWith("■") || defEn.startsWith("●")) {
                // don't add sense
            } else {
                //sb.append("<b>" + hwd + "</b>");
                //sb.append(" ");
                sb.append("<i>" + colorizeSense(sense) + "</i>");
                sb.append("<br/>");
                //sb.append(" ");
                //sb.append(def.Ext);
                //sb.append("<br/>");
            }

            sb.append(defEn + " " + defCn);
            displayHtml = sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            //sb.append(def.Phonetics);
            //sb.append(" ");
            //sb.append(def.Sense);
            //sb.append(" ");
            //sb.append(def.Ext);
            sb.append("<b><i>" + phrase + "</i></b>");
            sb.append("<br/>");
            sb.append(defEn + " " + defCn);
            Log.d("phrase", sb.toString());
            displayHtml = sb.toString();
        }

        return new Definition(eleMap, displayHtml);
    }

    private String[] getForms(String q) {
        //SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("forms", new String[]{"bases"}, "hwd=? ", new String[]{q.toLowerCase()}, null, null, null);
        String bases = "";
        while (cursor.moveToNext()) {
            bases = cursor.getString(0);
        }
        return bases.split("@@@");
    }

    private String colorizeSense(String sense) {
        String result = sense.replaceAll("noun", "<font color=#e3412f>n.</font>");
        result = result.replaceAll("adjective", "<font color=#f8b002>adj.</font>");
        result = result.replaceAll("verb", "<font color=#539007>v.</font>");
        result = result.replaceAll("adverb", "<font color=#684b9d>adv.</font>");
        return result;
    }

    private Cursor getFilterCursor(String q) {
        Log.d("databse", "getFilterCursor" + q);
        Cursor cursor = db.query("hwds", new String[]{"rowid _id", "hwd"}, "hwd LIKE ?", new String[]{q + "%"}, null, null, null);
        return cursor;
    }

    /**
     * 去除左右两边空格，标点
     *
     * @param key
     * @return
     */
    private String keyCleanup(String key) {
        return key.trim().replaceAll("[,.!?()\"'“”’？]", "").toLowerCase();
    }

    /*    String[] gethwds()
    {
        //SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, "dict", new String[] {"hwd"}, null, null, null, null, null,null);
        ArrayList<String> re = new ArrayList<>();
        int n = -1;
        while(cursor.moveToNext())
        {
            //n ++;
            String current = cursor.getString(0);
            //if(n > 0)
            //{
            //    if(current.equals(re.get(n - 1))) {
            //        continue;
            //    }
            //}
            re.add(current);
        }
        String[] hwds = new String[re.size()];
        for(int i = 0; i < re.size(); i ++){
            hwds[i] = re.get(i);
        }
        return  hwds;
    }*/
}
