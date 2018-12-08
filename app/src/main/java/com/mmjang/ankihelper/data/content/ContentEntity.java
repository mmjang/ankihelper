package com.mmjang.ankihelper.data.content;

public class ContentEntity {
    private String text;
    private String note;

    public ContentEntity(String text, String note) {
        this.text = text;
        this.note = note;
    }

    public String getText() {
        return text;

    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
