package com.mmjang.ankihelper.data.quote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.Random;

/**
 * Created by liao on 2017/8/13.
 */

public class QuoteDb extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "quote.db";
    private static final int DATABASE_VERSIOn = 1;
    private static QuoteDb instance = null;
    private static final int ID_MAX = 15972;
    Context mContext;
    SQLiteDatabase db;

    protected QuoteDb(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSIOn);
        mContext = context;
        db = getReadableDatabase();
    }

    public static QuoteDb getInstance(Context context){
        if(instance == null){
            instance = new QuoteDb(context);
        }
        return instance;
    }


    public String getQuote() {
        //SQLiteDatabase db = getReadableDatabase();
        int randomKey = randInt(0, ID_MAX);
//        Cursor cursor = db.query("quote", new String[]{"content"}, "id=?", new String[]{Integer.toString(randomKey)}, null, null, null);
        Cursor cursor = db.rawQuery("select content from quote where id=" + randomKey, null);
        String content = "";
        while (cursor.moveToNext()) {
            content = cursor.getString(0);
        }
        return content;
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
