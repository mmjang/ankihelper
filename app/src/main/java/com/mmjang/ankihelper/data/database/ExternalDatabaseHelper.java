package com.mmjang.ankihelper.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExternalDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ankihelper.db";
    private static final int VERSION = 3;
    Context mContext;

    private static final String SQL_CREATE_HISTORY = String.format(
            "create table if not exists %s (%s integer primary key, %s integer, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
            DBContract.History.TABLE_NAME, DBContract.History.COLUMN_TIME_STAMP,
            DBContract.History.COLUMN_TYPE, DBContract.History.COLUMN_WORD,
            DBContract.History.COLUMN_SENTENCE, DBContract.History.COLUMN_DICTIONARY,
            DBContract.History.COLUMN_DEFINITION, DBContract.History.COLUMN_TRANSLATION,
            DBContract.History.COLUMN_NOTE, DBContract.History.COLUMN_TAG);

    private static final String SQL_CREATE_PLAN = String.format(
            "create table if not exists %s (%s text, %s text, %s integer, %s integer, %s text)",
            DBContract.Plan.TABLE_NAME,
            DBContract.Plan.COLUMN_PLAN_NAME,
            DBContract.Plan.COLUMN_DICTIONARY_KEY,
            DBContract.Plan.COLUMN_OUTPUT_DECK_ID,
            DBContract.Plan.COLUMN_OUTPUT_MODEL_ID,
            DBContract.Plan.COLUMN_FIELDS_MAP
    );

    private static final String SQL_CREATE_DICT_TABLE = "CREATE TABLE IF NOT EXISTS dict" +
            "(id integer, name text, lang text, " +
            "elements text, description text, tmpl text)";
    private static final String SQL_CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS entry" +
            "(dict_id integer, headword text, entry_texts text)";

    private static final String SQL_CREATE_INDEX = "CREATE INDEX IF NOT EXISTS headword_index ON entry (headword)";
    private static final String SQL_DROP_INDEX = "DROP INDEX IF EXISTS headword_index";


    private static final String SQL_CREATE_BOOK_TABLE = String.format(
            "create table if not exists %s (%s integer, %s integer, %s text, %s text, %s text, %s text)",
                DBContract.Book.TABLE_NAME,
                DBContract.Book.COLUMN_ID,
                DBContract.Book.COLUMN_LAST_OPEN_TIME,
                DBContract.Book.COLUMN_BOOK_NAME,
                DBContract.Book.COLUMN_AUTHOR,
                DBContract.Book.COLUMN_BOOK_PATH,
                DBContract.Book.COLUMN_READ_POSITION);

    public ExternalDatabaseHelper(Context context) {
        super(new ExternalDatabaseContext(context), DB_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HISTORY);
        db.execSQL(SQL_CREATE_PLAN);
        db.execSQL(SQL_CREATE_DICT_TABLE);
        db.execSQL(SQL_CREATE_ENTRY_TABLE);
        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2){
            db.execSQL(SQL_CREATE_BOOK_TABLE);
        }

        if((oldVersion == 2) && newVersion == 3){
            db.execSQL(SQL_CREATE_INDEX);
        }
    }


}
