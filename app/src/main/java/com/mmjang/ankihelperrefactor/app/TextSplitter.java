package com.mmjang.ankihelperrefactor.app;

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

    private void process(){
        //this is a finite state machine to split sentence

        int sInit = 0;//0-init 1-in word  2- in nonwordInit
        int sWord = 1;
        int sNonWord = 2;

        int state = sInit;
        StringBuilder sb = new StringBuilder();
        for(char ch : mSentence.toCharArray()){
            if(isWordcharacter(ch)) {
                if (state == sInit) {
                    sb.append(ch);
                }
                if (state == sWord){
                    sb.append(ch);
                }
                if(state == sNonWord){
                    mSegmentList.add(new TextSegment(sb.toString(), mStateNonText));
                    sb = new StringBuilder();
                    sb.append(ch);
                }
                state = sWord;
            }else{
                if(state == sInit){
                    sb.append(ch);
                }
                if(state == sWord){
                    mSegmentList.add(new TextSegment(sb.toString(), mStateText));
                    sb = new StringBuilder();
                    sb.append(ch);
                }
                if(state == sNonWord){
                    sb.append(ch);
                }
                state = sNonWord;
            }
        }
        if(state == sWord){
            mSegmentList.add(new TextSegment(sb.toString(), mStateText));
        }
        if(state == sNonWord){
            mSegmentList.add(new TextSegment(sb.toString(), mStateNonText));
        }
    }

    private boolean isWordcharacter(char ch){
        //(Ó	ó)	P	p	Q	q	R	r	S	s	T	t	U	u	(Ú	ú)	(Ü	ü)
        //V	v	W	w	X	x	Y	y	Z	z

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
                ch == 'ú' ||
                ch == 'Ü' ||
                ch == 'ü'
                ){
            return true;
        }
        else{
            return false;
        }
    }
}
