package com.mmjang.ankihelper.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liao on 2017/5/4.
 */

public class TextSplitter {
    private String mSentence;
    private List<TextSegment> mSegmentList = new ArrayList<>();
    private int mStateText;
    private int mStateNonText;
    public TextSplitter(String sentence, int stateNonText, int stateText){
        mSentence = sentence;
        mStateText = stateText;
        mStateNonText = stateNonText;
        process();
    }

    public List<TextSegment> getSegmentList(){
        return mSegmentList;
    }

    public String getStringFromState(int state){
        StringBuilder sb = new StringBuilder();
        for(TextSegment ts : mSegmentList){
            if(ts.getState() == state){
                sb.append(ts.getText());
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    public String getBoldSentence(int state){
        StringBuilder sb = new StringBuilder();
        for(TextSegment ts : mSegmentList){
            if(ts.getState() == state){
                sb.append("<b>");
                sb.append(ts.getText());
                sb.append("</b>");
                //sb.append(" ");
            }else{
                sb.append(ts.getText());
            }
        }
        return sb.toString().trim();
    }

    public String getBlankSentence(int state){
            StringBuilder sb = new StringBuilder();
            for(TextSegment ts : mSegmentList){
                if(ts.getState() == state){
                    sb.append("{{c1::" + ts.getText() + "}}");
                }else{
                    sb.append(ts.getText());
                }
            }
            return sb.toString().trim();
    }

    private void process(){
        //this is a finite state machine to split sentence

        int sInit = 0;//0-init 1-in word  2- in nonwordInit
        int sWord = 1;
        int sNonWord = 2;

        int state = sInit;
        StringBuilder sb = new StringBuilder();
        for(char ch : mSentence.toCharArray()) {

            if (isWordcharacter(ch)) {
                if (state == sInit) {
                    sb.append(ch);
                }
                if (state == sWord) {
                    sb.append(ch);
                }
                if (state == sNonWord) {
                    mSegmentList.add(new TextSegment(sb.toString(), mStateNonText));
                    sb = new StringBuilder();
                    sb.append(ch);
                }
                state = sWord;
            } else {
                if (isChinese(ch)) {
                    if (state == sInit) {
                        mSegmentList.add(new TextSegment(Character.toString(ch), mStateText));
                        state = sInit;
                    }else {
                        if (state == sWord) {
                            mSegmentList.add(new TextSegment(sb.toString(), mStateText));
                            mSegmentList.add(new TextSegment(Character.toString(ch), mStateText));
                            sb = new StringBuilder();
                            state = sNonWord;
                        } else {
                            mSegmentList.add(new TextSegment(sb.toString(), mStateNonText));
                            mSegmentList.add(new TextSegment(Character.toString(ch), mStateText));
                            sb = new StringBuilder();
                            state = sNonWord;
                        }
                    }
                } else {
                    if (state == sInit) {
                        sb.append(ch);
                    }
                    if (state == sWord) {
                        mSegmentList.add(new TextSegment(sb.toString(), mStateText));
                        sb = new StringBuilder();
                        sb.append(ch);
                    }
                    if (state == sNonWord) {
                        sb.append(ch);
                    }
                    state = sNonWord;
                }
            }
        }
            if (state == sWord) {
                mSegmentList.add(new TextSegment(sb.toString(), mStateText));
            }
            if (state == sNonWord) {
                mSegmentList.add(new TextSegment(sb.toString(), mStateNonText));
            }
    }

    private boolean isWordcharacter(char ch){
        //(Ó	ó)	P	p	Q	q	R	r	S	s	T	t	U	u	(Ú	ú)	(Ü	ü)
        //V	v	W	w	X	x	Y	y	Z	z
        char[] frSpecial =  "ÄäàaçèéêëîïôùûœÖöß".toCharArray();
        for(char c : frSpecial){
            if(ch == c){
                return true;
            }
        }
        if(ch == '-' || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')||
                ch == 'Á' ||
                ch == 'á' ||
                ch == 'É' ||
                ch == 'é' ||
                ch == 'Í' ||
                ch == 'í' ||
                ch == 'Ó' ||
                ch == 'ó' ||
                ch == 'Ú' ||
                ch == 'ú' ||
                ch == 'Ü' ||
                ch == 'ü'
                ){
            return true;
        }
        if(ch >= '0' && ch <= '9')
        {
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isChinese(char ch){
        if(ch >= 0x0800 && ch <= 0x9FBB){
            return true;
        }
        else{
            return false;
        }
    }
}
