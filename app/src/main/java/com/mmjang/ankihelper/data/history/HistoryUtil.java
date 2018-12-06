package com.mmjang.ankihelper.data.history;

import com.mmjang.ankihelper.data.database.ExternalDatabase;

//private long timeStamp;
//private int type;
//private String word;
//private String sentence;
//private String dictionary;
//private String definition;
//private String translation;
//
//private String note;
//private String tag;
public class HistoryUtil {
    public static void savePopupOpen(String sentence){
        HistoryPOJO history = new HistoryPOJO();
        history.setType(HistoryType.POPUP_OPEN);
        history.setTimeStamp(System.currentTimeMillis());
        history.setSentence(sentence);
        ExternalDatabase.getInstance().insertHistory(history);
        }

    public static void saveWordlookup(String sentence, String word){
        HistoryPOJO history = new HistoryPOJO();
        history.setType(HistoryType.WORD_LOOK_UP);
        history.setTimeStamp(System.currentTimeMillis());
        history.setSentence(sentence);
        history.setWord(word);
        ExternalDatabase.getInstance().insertHistory(history);
    }

    public static void saveNoteAdd(String sentence, String word,
                                   String dictionary, String definition,
                                   String translation, String note, String tag){
        HistoryPOJO history = new HistoryPOJO();
        history.setType(HistoryType.NOTE_ADD);
        history.setTimeStamp(System.currentTimeMillis());
        history.setSentence(sentence);
        history.setWord(word);
        history.setDictionary(dictionary);
        history.setDefinition(definition);
        history.setTranslation(translation);
        history.setNote(note);
        history.setTag(tag);
        ExternalDatabase.getInstance().insertHistory(history);
    }
}
