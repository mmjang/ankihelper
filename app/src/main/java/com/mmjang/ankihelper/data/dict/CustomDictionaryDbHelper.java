package com.mmjang.ankihelper.data.dict;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liao on 2017/8/11.
 */

public class CustomDictionaryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CustomDictionary.db";
    private static final String SQL_CREATE_DICT_TABLE = "CREATE TABLE IF NOT EXISTS dict" +
                                                        "(id integer, name text, lang text, " +
                                                        "elements text, description text, tmpl text)";
    private static final String SQL_CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS entry" +
                                                        "(dict_id integer, headword text, entry_texts text)";
    private static final String SQL_CLEAR_DB = "DELETE FROM dict; DELETE FROM entry; VACUUM";
    private static final String TB_DICT = "dict";
    private static final String TB_ENTRY = "entry";
    private static final String CL_ID = "id";
    private static final String CL_NAME = "name";
    private static final String CL_LANG = "lang";
    private static final String CL_ELEMENTS = "elements";
    private static final String CL_TMPL = "tmpl";
    private static final String CL_DESCRIPTION = "description";
    private static final String CL_DICT_ID = "dict_id";
    private static final String CL_HEADWORD = "headword";
    private static final String CL_ENTRY_TEXTS = "entry_texts";
    private static final String SPLITTER = "\t"; //original file is splitted by \t, so it's safe.
    public CustomDictionaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DICT_TABLE);
        db.execSQL(SQL_CREATE_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clearDB(){
        getWritableDatabase().delete(TB_DICT, null, null);
        getWritableDatabase().delete(TB_ENTRY, null, null);
    }

    public static String getHeadwordColumnName(){
        return CL_HEADWORD;
    }

    public void addDictionaryInformation(int id, String name, String lang, String[] elements, String description, String tmpl){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CL_ID, id);
        values.put(CL_NAME, name);
        values.put(CL_LANG, lang);
        values.put(CL_ELEMENTS, joinFields(elements));
        values.put(CL_DESCRIPTION, description);
        values.put(CL_TMPL, tmpl);
        db.insert(TB_DICT, null, values);
    }

    public void addEntries(int dictId, List<String[]> entries){ //assume first column is headword
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for(String[] entry : entries){
            if(entry.length < 2){
                continue;
            }
            ContentValues values = new ContentValues();
            values.put(CL_DICT_ID, dictId);
            values.put(CL_HEADWORD, entry[0]);   //must have at least 2 columns, enforced in manager
            String[] elements = entry;//Arrays.copyOfRange(entry, 1, entry.length - 1);
            values.put(CL_ENTRY_TEXTS, joinFields(elements));
            db.insert(TB_ENTRY, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor getWordLookupCursor(int dictId, String word){
        Cursor cursor = getReadableDatabase().query(
                TB_ENTRY,
                new String[] {CL_HEADWORD, CL_ENTRY_TEXTS},
                CL_DICT_ID + "= ? AND " + CL_HEADWORD + "= ? COLLATE NOCASE",
                new String[]{String.valueOf(dictId), word},
                null,null,null);
        return cursor;
    }

    public Cursor getFilterCursor(int dictId, String query){
        Cursor cursor = getReadableDatabase().query(
                TB_ENTRY,
                new String[]{"rowid _id", CL_HEADWORD},
                CL_DICT_ID + "=? AND " + CL_HEADWORD + " LIKE ?",
                new String[]{String.valueOf(dictId), query + "%"},
                CL_HEADWORD,
                null,
                null
        );
        return cursor;
    }

    public List<Integer> getDictIdList(){
        Cursor cursor = getReadableDatabase().query(
                TB_DICT,
                new String[] {CL_ID},
                "",
                null,
                null,
                null,
                null
        );
        List<Integer> re = new ArrayList<Integer>();
        while(cursor.moveToNext()){
            re.add(cursor.getInt(0));
        }
        return re;
    }

    @Nullable  //if null, not found
    public CustomDictionaryInformation getDictInfo(int dictId){
        Cursor cursor = getReadableDatabase().query(
                TB_DICT,
                new String[] {CL_ID, CL_NAME, CL_DESCRIPTION, CL_LANG, CL_TMPL, CL_ELEMENTS},
                CL_ID + "=" + dictId,
                null,
                null,
                null,
                null
        );
        CustomDictionaryInformation customDictionaryInformation = null;
        while(cursor.moveToNext()){
            customDictionaryInformation = new CustomDictionaryInformation(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    fromFieldsString(cursor.getString(5))
            );
        }
        return customDictionaryInformation;
    }

    public List<String[]> queryHeadword(int dictId, String q){
        Cursor cursor = getReadableDatabase().query(
                TB_ENTRY,
                new String[] {CL_ENTRY_TEXTS},
                CL_DICT_ID + "=? AND " + CL_HEADWORD + "=? COLLATE NOCASE",
                new String[]{String.valueOf(dictId), q}
                ,null,null,null);
        List<String[]> result = new ArrayList<>();
        while(cursor.moveToNext()){
            result.add(fromFieldsString(cursor.getString(0)));
        }
        return result;
    }
    private static String joinFields(String[] fields){
        StringBuilder sb = new StringBuilder();
        for(String s : fields){
            sb.append(s);
            sb.append(SPLITTER);
        }
        return sb.toString().trim();
    }

    private static String[] fromFieldsString(String fieldsString){
        return fieldsString.split(SPLITTER);
    }
}
