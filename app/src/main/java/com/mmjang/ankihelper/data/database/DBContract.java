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

    public static class Plan{
        public static final String TABLE_NAME = "plan";
        public static final String COLUMN_PLAN_NAME = "planname";
        public static final String COLUMN_DICTIONARY_KEY = "dictionarykey";
        public static final String COLUMN_OUTPUT_DECK_ID = "outputdeckid";
        public static final String COLUMN_OUTPUT_MODEL_ID = "outputmodelid";
        public static final String COLUMN_FIELDS_MAP = "fieldsmap";
    }

    public static class Book{
        public static final String TABLE_NAME = "book";
        public static final String COLUMN_ID = "id"; //creation OPOCH time in millis
        public static final String COLUMN_LAST_OPEN_TIME =  "lastopentime";
        public static final String COLUMN_BOOK_NAME = "bookname";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_BOOK_PATH = "bookpath";
        public static final String COLUMN_READ_POSITION = "readposition"; //stored in json format
    }
}
