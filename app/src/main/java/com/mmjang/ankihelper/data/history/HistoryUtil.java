package com.mmjang.ankihelper.data.history;

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
        History history = new History();
        history.setType(HistoryType.POPUP_OPEN);
        history.setTimeStamp(System.currentTimeMillis());
        history.setSentence(sentence);
        history.save();
    }

    public static void saveWordlookup(String sentence, String word){
        History history = new History();
        history.setType(HistoryType.WORD_LOOK_UP);
        history.setTimeStamp(System.currentTimeMillis());
        history.setSentence(sentence);
        history.setWord(word);
        history.save();
    }

    public static void saveNoteAdd(String sentence, String word,
                                   String dictionary, String definition,
                                   String translation, String note, String tag){
        History history = new History();
        history.setType(HistoryType.NOTE_ADD);
        history.setTimeStamp(System.currentTimeMillis());
        history.setSentence(sentence);
        history.setWord(word);
        history.setDictionary(dictionary);
        history.setDefinition(definition);
        history.setTranslation(translation);
        history.setNote(note);
        history.setTag(tag);
        history.save();
    }
}
