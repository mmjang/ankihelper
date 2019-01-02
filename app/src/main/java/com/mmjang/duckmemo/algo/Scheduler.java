package com.mmjang.duckmemo.algo;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.card.Card;
import com.mmjang.duckmemo.data.card.CardDao;
import com.mmjang.duckmemo.data.note.NoteDao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class QualityOption{
    public final int Quality;
    public final int Interval;
    public QualityOption(int quality, int interval){
        Quality = quality;
        Interval = interval;
    }
}

class CardCount{
    public final int NumNew;
    public final int NumOld;
    public CardCount(int numNew, int numOld){
        NumNew = numNew;
        NumOld = numOld;
    }
}

public class Scheduler {

    private LinkedList<Card> mCardQueue;
    private MemoAlgorithm mAlgorithm;
    private CardDao cardDao;
    private int numNew;
    private int numOld;

    public Scheduler(List<Card> cards, MemoAlgorithm algorithm){
        mCardQueue = new LinkedList<>();
        mCardQueue.addAll(cards);
        mAlgorithm = algorithm;
        cardDao = MyApplication.getDaoSession().getCardDao();

        numNew = 0;
        numOld = 0;
        for(Card card : cards){
            if(card.getInterval() < 0){
                numNew ++;
            }else{
                numOld ++;
            }
        }
    }

    public Card getCard(){
        if(mCardQueue.isEmpty()){
            return null;
        }
        return mCardQueue.get(0);
    }

    public List<QualityOption> getQualityOptions(Card card, long time){
        List<QualityOption> qualityOptionList = new ArrayList<>();
        if(card.getInterval() < 0){
            qualityOptionList.add(new QualityOption(1, 0));//failed
            qualityOptionList.add(new QualityOption(3, 1));//good
        }else{
            int interval;
            interval = mAlgorithm.getIntervalByQuality(card, 1, time); //failed
            qualityOptionList.add(new QualityOption(1, interval));
            interval = mAlgorithm.getIntervalByQuality(card, 3, time); //good
            qualityOptionList.add(new QualityOption(3, interval));
            interval = mAlgorithm.getIntervalByQuality(card, 5, time); //easy
            qualityOptionList.add(new QualityOption(5, interval));
        }
        return qualityOptionList;
    }

    public List<QualityOption> getQualityOptions(Card card){
        long time = System.currentTimeMillis();
        return getQualityOptions(card, time);
    }

    public void doReview(Card card, int quality, long time){
        boolean isNewCard = card.getInterval() < 0;
        mAlgorithm.calculate(card, quality, time);
        if(card.getInterval() < 0){
            mCardQueue.addLast(mCardQueue.pop());
            //new card failed, count not change
        }else{
            mCardQueue.pop();
            if(isNewCard){
                numNew --;
            }else{
                numOld --;
            }
        }
        cardDao.update(card);
    }

    public void removeCardAt(int index){
        Card card = mCardQueue.get(index);
        if(card.getInterval() < 0){
            numNew --;
        }else{
            numOld --;
        }
        cardDao.delete(card);
        //there's no card attached to this note, delete it
        if(cardDao.queryBuilder().where(CardDao.Properties.NoteId.eq(card.getNoteId())).buildCount().count() == 0){
            MyApplication.getDaoSession().getNoteDao()
                    .queryBuilder().where(NoteDao.Properties.Id.eq(card.getNoteId()));
        }
        mCardQueue.remove(index);
    }

    public void doReview(Card card, int quality){
        long time = System.currentTimeMillis();
        doReview(card, quality, time);
    }

    public int getNumberOfNewCard(){
        return numNew;
    }

    public int getNumberOfOldCard(){
        return numOld;
    }
}
