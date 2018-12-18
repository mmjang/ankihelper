package com.mmjang.duckmemo.data.note;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Note implements Addable{
    @Id
    private long id;
    private long lastedEditTime;
    private String language;
    private String word;
    private String sentence;
    private String translation;
    private String definition;
    private String extra;
    private String tag; //space separated
    private String data; //for storing additional data;


    @Generated(hash = 925823766)
    public Note(long id, long lastedEditTime, String language, String word,
            String sentence, String translation, String definition, String extra,
            String tag, String data) {
        this.id = id;
        this.lastedEditTime = lastedEditTime;
        this.language = language;
        this.word = word;
        this.sentence = sentence;
        this.translation = translation;
        this.definition = definition;
        this.extra = extra;
        this.tag = tag;
        this.data = data;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLastedEditTime() {
        return lastedEditTime;
    }

    public void setLastedEditTime(long lastedEditTime) {
        this.lastedEditTime = lastedEditTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
