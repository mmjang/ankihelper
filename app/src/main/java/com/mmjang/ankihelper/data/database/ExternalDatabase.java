package com.mmjang.ankihelper.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.dict.JiSho;
import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryInformation;
import com.mmjang.ankihelper.data.history.HistoryPOJO;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;

import java.util.ArrayList;
import java.util.List;

public class ExternalDatabase {
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
    Context mContext;
    SQLiteDatabase mDatabase;
    private static ExternalDatabase instance;
    private ExternalDatabase(Context context){
        mContext = context;
        ExternalDatabaseHelper dbHelper = new ExternalDatabaseHelper(mContext);
        mDatabase = dbHelper.getWritableDatabase();
    }

    public static ExternalDatabase getInstance() {
        if(instance == null){
            instance = new ExternalDatabase(MyApplication.getContext());
        }
        return instance;
    }

    public void clearDB(){
        mDatabase.delete(TB_DICT, null, null);
        mDatabase.delete(TB_ENTRY, null, null);
    }

    public static String getHeadwordColumnName(){
        return CL_HEADWORD;
    }

    public void addDictionaryInformation(int id, String name, String lang, String[] elements, String description, String tmpl){
        SQLiteDatabase db = mDatabase;
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
        SQLiteDatabase db = mDatabase;
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
        Cursor cursor = mDatabase.query(
                TB_ENTRY,
                new String[] {CL_HEADWORD, CL_ENTRY_TEXTS},
                CL_DICT_ID + "= ? AND " + CL_HEADWORD + "= ? COLLATE NOCASE",
                new String[]{String.valueOf(dictId), word},
                null,null,null);
        return cursor;
    }

    public Cursor getFilterCursor(int dictId, String query){
        Cursor cursor = mDatabase.query(
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
        Cursor cursor = mDatabase.query(
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
        Cursor cursor = mDatabase.query(
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
        Cursor cursor = mDatabase.query(
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

    public boolean insertHistory(HistoryPOJO history){
        ContentValues values = new ContentValues();
        values.put(DBContract.History.COLUMN_TIME_STAMP, history.getTimeStamp());
        values.put(DBContract.History.COLUMN_DEFINITION, history.getDefinition());
        values.put(DBContract.History.COLUMN_DICTIONARY, history.getDictionary());
        values.put(DBContract.History.COLUMN_NOTE, history.getNote());
        values.put(DBContract.History.COLUMN_TAG, history.getTag());
        values.put(DBContract.History.COLUMN_SENTENCE, history.getSentence());
        values.put(DBContract.History.COLUMN_TYPE, history.getType());
        values.put(DBContract.History.COLUMN_WORD, history.getWord());
        long result = mDatabase.insert(DBContract.History.TABLE_NAME, null, values);
        return result >= 0;
    }

    public void insertManyHistory(List<HistoryPOJO> historyPOJOS){
        mDatabase.beginTransaction();
        for(HistoryPOJO history : historyPOJOS){
            insertHistory(history);
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public List<HistoryPOJO> getHistoryAfter(long timeStamp){
        Cursor cursor = mDatabase.rawQuery(
                String.format("select %s, %s, %s, %s, %s, %s, %s, %s from %s where %s > %s",
                        DBContract.History.COLUMN_TIME_STAMP,
                        DBContract.History.COLUMN_DEFINITION,
                        DBContract.History.COLUMN_DICTIONARY,
                        DBContract.History.COLUMN_NOTE,
                        DBContract.History.COLUMN_TAG,
                        DBContract.History.COLUMN_SENTENCE,
                        DBContract.History.COLUMN_TYPE,
                        DBContract.History.COLUMN_WORD,
                        DBContract.History.TABLE_NAME,
                        DBContract.History.COLUMN_TIME_STAMP,
                        timeStamp
                        ),
                null
        );
        List<HistoryPOJO> result = new ArrayList<>();
        while (cursor.moveToNext()){
            HistoryPOJO historyPOJO = new HistoryPOJO();
            historyPOJO.setTimeStamp(cursor.getLong(0));
            historyPOJO.setDefinition(cursor.getString(1));
            historyPOJO.setDictionary(cursor.getString(2));
            historyPOJO.setNote(cursor.getString(3));
            historyPOJO.setTag(cursor.getString(4));
            historyPOJO.setSentence(cursor.getString(5));
            historyPOJO.setType(cursor.getInt(6));
            historyPOJO.setWord(cursor.getString(7));
            result.add(historyPOJO);
        }
        return result;
    }

    public List<OutputPlanPOJO> getAllPlan(){
        Cursor cursor = mDatabase.rawQuery(
                String.format("select %s, %s, %s, %s, %s from %s",
                        DBContract.Plan.COLUMN_PLAN_NAME,
                        DBContract.Plan.COLUMN_DICTIONARY_KEY,
                        DBContract.Plan.COLUMN_OUTPUT_DECK_ID,
                        DBContract.Plan.COLUMN_OUTPUT_MODEL_ID,
                        DBContract.Plan.COLUMN_FIELDS_MAP,
                        DBContract.Plan.TABLE_NAME
                        ),
                null
        );
        List<OutputPlanPOJO> result = new ArrayList<>();
        while(cursor.moveToNext()){
            OutputPlanPOJO outputPlanPOJO = new OutputPlanPOJO();
            outputPlanPOJO.setPlanName(cursor.getString(0));
            outputPlanPOJO.setDictionaryKey(cursor.getString(1));
            outputPlanPOJO.setOutputDeckId(cursor.getLong(2));
            outputPlanPOJO.setOutputModelId(cursor.getLong(3));
            outputPlanPOJO.setFieldsMapString(cursor.getString(4));
            result.add(outputPlanPOJO);
        }
        return result;
    }

    public OutputPlanPOJO getPlanByName(String planName){
        Cursor cursor = mDatabase.rawQuery(
                String.format("select %s, %s, %s, %s, %s from %s where %s='%s'",
                        DBContract.Plan.COLUMN_PLAN_NAME,
                        DBContract.Plan.COLUMN_DICTIONARY_KEY,
                        DBContract.Plan.COLUMN_OUTPUT_DECK_ID,
                        DBContract.Plan.COLUMN_OUTPUT_MODEL_ID,
                        DBContract.Plan.COLUMN_FIELDS_MAP,
                        DBContract.Plan.TABLE_NAME,
                        DBContract.Plan.COLUMN_PLAN_NAME,
                        planName
                ),
                null
        );
        OutputPlanPOJO result = null;
        while(cursor.moveToNext()){
            OutputPlanPOJO outputPlanPOJO = new OutputPlanPOJO();
            outputPlanPOJO.setPlanName(cursor.getString(0));
            outputPlanPOJO.setDictionaryKey(cursor.getString(1));
            outputPlanPOJO.setOutputDeckId(cursor.getLong(2));
            outputPlanPOJO.setOutputModelId(cursor.getLong(3));
            outputPlanPOJO.setFieldsMapString(cursor.getString(4));
            result = outputPlanPOJO;
        }
        return result;
    }

    public void refreshPlanWith(List<OutputPlanPOJO> outputPlanPOJOS){
        mDatabase.beginTransaction();
        mDatabase.delete(DBContract.Plan.TABLE_NAME, null, null);
        for(OutputPlanPOJO outputPlanPOJO : outputPlanPOJOS){
            insertPlan(outputPlanPOJO);
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public int deletePlanByName(String planName){
        return mDatabase.delete(DBContract.Plan.TABLE_NAME,
                "" + DBContract.Plan.COLUMN_PLAN_NAME + "='" + planName +"'",
                null
                );
    }

    public int updatePlan(OutputPlanPOJO outputPlanPOJO){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Plan.COLUMN_PLAN_NAME, outputPlanPOJO.getPlanName());
        contentValues.put(DBContract.Plan.COLUMN_DICTIONARY_KEY, outputPlanPOJO.getDictionaryKey());
        contentValues.put(DBContract.Plan.COLUMN_OUTPUT_DECK_ID, outputPlanPOJO.getOutputDeckId());
        contentValues.put(DBContract.Plan.COLUMN_OUTPUT_MODEL_ID, outputPlanPOJO.getOutputModelId());
        contentValues.put(DBContract.Plan.COLUMN_FIELDS_MAP, outputPlanPOJO.getFieldsMapString());
        return mDatabase.update(DBContract.Plan.TABLE_NAME, contentValues,
                "" + DBContract.Plan.COLUMN_PLAN_NAME + "='" + outputPlanPOJO.getPlanName() +"'", null);
    }

    public long insertPlan(OutputPlanPOJO outputPlanPOJO){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Plan.COLUMN_PLAN_NAME, outputPlanPOJO.getPlanName());
        contentValues.put(DBContract.Plan.COLUMN_DICTIONARY_KEY, outputPlanPOJO.getDictionaryKey());
        contentValues.put(DBContract.Plan.COLUMN_OUTPUT_DECK_ID, outputPlanPOJO.getOutputDeckId());
        contentValues.put(DBContract.Plan.COLUMN_OUTPUT_MODEL_ID, outputPlanPOJO.getOutputModelId());
        contentValues.put(DBContract.Plan.COLUMN_FIELDS_MAP, outputPlanPOJO.getFieldsMapString());
        return mDatabase.insert(DBContract.Plan.TABLE_NAME, null, contentValues);
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
