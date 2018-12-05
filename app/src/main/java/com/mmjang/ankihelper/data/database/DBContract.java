package com.mmjang.ankihelper.data.database;

public final class DBContract {
    private DBContract(){}

    public static class History{
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_TIME_STAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_SENTENCE = "sentence";
        public static final String COLUMN_DICTIONARY = "dictionary";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_TRANSLATION = "translation";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_TAG = "tag";
    }
}
