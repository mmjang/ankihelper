package com.mmjang.ankihelper.data.history;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class History extends DataSupport {
    @Column(unique = true)
    private long timeStamp;
    private int type;
    private String word;
    private String sentence;
    private String dictionary;
    private String definition;
    private String translation;

    private String note;
    private String tag;

    History(){
        word = "";
        sentence = "";
        dictionary = "";
        definition = "";
        translation = "";

        note = "";
        tag = "";
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
