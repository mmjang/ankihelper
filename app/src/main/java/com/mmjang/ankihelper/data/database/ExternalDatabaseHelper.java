package com.mmjang.ankihelper.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryInformation;
import com.mmjang.ankihelper.util.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExternalDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ankihelper.db";
    private static final int VERSION = 1;
    Context mContext;

    private static final String SQL_CREATE = "create table if not exists " + DBContract.History.TABLE_NAME + " (" +
            DBContract.History.COLUMN_TIME_STAMP + " integer primary key, " +
            DBContract.History.COLUMN_TYPE + " integer, " +
            DBContract.History.COLUMN_WORD + " text, " +
            DBContract.History.COLUMN_SENTENCE + "text, " +
            DBContract.History.COLUMN_DICTIONARY + "text, " +
            DBContract.History.COLUMN_DEFINITION + "text, " +
            DBContract.History.COLUMN_TRANSLATION + "text, " +
            DBContract.History.COLUMN_NOTE + "text, " +
            DBContract.History.COLUMN_TAG + "text)";

    private static final String SQL_CREATE_DICT_TABLE = "CREATE TABLE IF NOT EXISTS dict" +
            "(id integer, name text, lang text, " +
            "elements text, description text, tmpl text)";
    private static final String SQL_CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS entry" +
            "(dict_id integer, headword text, entry_texts text)";

    public ExternalDatabaseHelper(Context context) {
        super(new ExternalDatabaseContext(context), DB_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
        db.execSQL(SQL_CREATE_DICT_TABLE);
        db.execSQL(SQL_CREATE_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
