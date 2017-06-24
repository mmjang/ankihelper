package com.mmjang.ankihelper.util;

/**
 * Created by liao on 2017/5/4.
 */

public class TextSegment {
    private String text;
    private int state;

    public TextSegment(String seg, int sta) {
        text = seg;
        state = sta;
    }

    public String getText() {
        return text;
    }

    public int getState() {
        return state;
    }

    public void setState(int sta) {
        state = sta;
    }
}
