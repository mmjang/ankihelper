package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mmjang.ankihelper.data.database.ExternalDatabaseContext;
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
        super(new ExternalDatabaseContext(context), DATABASE_NAME,
                new ExternalDatabaseContext(context).getDatabaseDir(DATABASE_NAME),
                null, DATABASE_VERSIOn);
        mContext = context;
        try {
            db = getReadableDatabase();
        } catch (Exception e) {
            Log.e("FormsUtil", "Failed to open database " + DATABASE_NAME, e);
            db = null;
        }
    }

    public static FormsUtil getInstance(Context context){
        if(instance == null){
            instance = new FormsUtil(context);
        }
        return instance;
    }


    public String[] getForms(String q) {
        if (db == null || !db.isOpen()) {
            return new String[0];
        }
        Cursor cursor = db.query("forms", new String[]{"bases"}, "hwd=? ", new String[]{q.toLowerCase()}, null, null, null);
        String bases = "";
        while (cursor.moveToNext()) {
            bases = cursor.getString(0);
        }
        if(bases.isEmpty()){
            return new String[0];
        }
        return bases.split("@@@");
    }
}
