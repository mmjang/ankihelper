package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by liao on 2017/8/13.
 */

public class FormsUtil extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "forms.db";
    private static final int DATABASE_VERSIOn = 1;
    private static FormsUtil instance = null;
    Context mContext;
    SQLiteDatabase db;

    protected FormsUtil(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSIOn);
        mContext = context;
        db = getReadableDatabase();
    }

    public static FormsUtil getInstance(Context context){
        if(instance == null){
            instance = new FormsUtil(context);
        }
        return instance;
    }


    public String[] getForms(String q) {
        //SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("forms", new String[]{"bases"}, "hwd=? ", new String[]{q.toLowerCase()}, null, null, null);
        String bases = "";
        while (cursor.moveToNext()) {
            bases = cursor.getString(0);
        }
        return bases.split("@@@");
    }
}
