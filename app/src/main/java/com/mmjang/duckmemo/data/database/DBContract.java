package com.mmjang.duckmemo.data.database;

public final class DBContract {
    private DBContract(){}

    //deprecated
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

    //deprecated
    public static class Plan{
        public static final String TABLE_NAME = "plan";
        public static final String COLUMN_PLAN_NAME = "planname";
        public static final String COLUMN_DICTIONARY_KEY = "dictionarykey";
        public static final String COLUMN_OUTPUT_DECK_ID = "outputdeckid";
        public static final String COLUMN_OUTPUT_MODEL_ID = "outputmodelid";
        public static final String COLUMN_FIELDS_MAP = "fieldsmap";
    }


    public static class Tag{
        public static final String TABLE_NAME = "tag";
        public static final String COLUMN_ID = "id"; //unique unix time
        public static final String COLUMN_LAST_USED_TIME = "lastusedtime";
        public static final String COLUMN_NAME = "name"; //unique
    }

    /**
     * the entity for stored information in cards
     * a note can generate multiple cards
     * word and sentence column can't both be empty
     */
    public static class Note{
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_ID = "id"; //unique unix epoch time
        public static final String COLUMN_LAST_EDIT_TIME = "lastedittime"; //unix epoch time
        public static final String COLUMN_LANGUAGE = "language"; //ISO language tag, cn / en / fr / jp
        public static final String COLUMN_WORD = "word"; //
        public static final String COLUMN_SENTENCE = "sentence"; //
        public static final String COLUMN_TRANSLATION = "translation"; //may be empty
        public static final String COLUMN_DEFINITION = "definition"; //support empty value and multiple definitions
        public static final String COLUMN_EXTRA = "extra"; //extra information, maybe note
        public static final String COLUMN_TAG = "tag"; //store tag id list, separated by comma or something else
        public static final String COLUMN_DATA = "data"; //for store json information
    }

    /**
     * the entity for card displayed in reviewer
     */
    public static class Card{
        public static final String TABLE_NAME = "card";
        public static final String COLUMN_ID = "id"; //unique unix epoch time
        public static final String COLUMN_NOTE_ID = "noteid"; //note id corresponding to this card
        public static final String COLUMN_CARD_TYPE = "cardtype"; //integer enum, predefined front and back templates
        public static final String COLUMN_NEXT_REVIEW_TIME = "nextreviewtime"; // 0 if it was never reviewed
        public static final String COLUMN_INTERVAL = "interval"; //0 not, 1 yes.
        public static final String COLUMN_REPETITIONS = "repetitions"; // integer days
        public static final String COLUMN_EASINESSFACTOR = "easinessfactor"; //integer minutes
        public static final String COLUMN_INITIAL_STEPS = "initialsteps";// 1, 10
    }

    /**
     * for logging all user reviews
     */
    public static class Review{
        public static final String TABLE_NAME = "review";
        public static final String COLUMN_ID = "id"; //unique unix epoch time
        public static final String COLUMN_CARD_ID = "card_id";
        public static final String COLUMN_QUALITY = "quality"; //review quality, from 1 - 5
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
