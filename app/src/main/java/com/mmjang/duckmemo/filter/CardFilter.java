package com.mmjang.duckmemo.filter;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.card.Card;
import com.mmjang.duckmemo.data.card.CardDao;

import java.util.List;

public class CardFilter {
    CardDao cardDao;

    public CardFilter(){
        cardDao = MyApplication.getDaoSession().getCardDao();
    }

    public List<Card> getCardList(long now){
        return cardDao.queryBuilder()
                .where(CardDao.Properties.NextReviewTime.lt(now))
                .orderAsc(CardDao.Properties.NextReviewTime)
                .orderDesc(CardDao.Properties.Interval).list();
    }
}
