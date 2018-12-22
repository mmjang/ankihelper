package com.mmjang.duckmemo.data.note;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.card.Card;
import com.mmjang.duckmemo.data.card.CardDao;
import com.mmjang.duckmemo.data.card.CardType;

public class DBExporter implements Exporter{
    private NoteDao noteDao;
    private CardDao cardDao;

    public DBExporter(){
        noteDao = MyApplication.getDaoSession().getNoteDao();
        cardDao = MyApplication.getDaoSession().getCardDao();
    }

    @Override
    public ExportResult add(Addable addable) {
        //insert note
        Note note = (Note) addable;
        note.setData("");
        note.setExtra("");
        note.setId(System.currentTimeMillis());
        note.setLastedEditTime(System.currentTimeMillis());
        long result = noteDao.insert(note);
        //insert card1
        Card card = new Card();
        card.setId(System.currentTimeMillis());
        card.setNoteId(note.getId());
        long time1 = System.currentTimeMillis();
        card.setNextReviewTime(time1);
        card.setCardType(CardType.SENTENCE_DEFINITION);
        card.setEasinessFactor(2.5f);
        card.setInterval(-1);
        card.setInitialSteps("1 10");
        card.setRepetitions(0);
        cardDao.insert(card);
        //insert card2
        Card card2 = new Card();
        long time2 = System.currentTimeMillis() + 1;
        card2.setId(time2);
        card2.setNoteId(note.getId());
//        card2.setNextReviewTime(System.currentTimeMillis() + 1000L * 60L * 60L * 24L);
        card2.setNextReviewTime(System.currentTimeMillis());
        card2.setCardType(CardType.CLOZE);
        card2.setEasinessFactor(2.5f);
        card2.setInterval(-1);
        card2.setInitialSteps("1 10");
        card2.setRepetitions(0);
        cardDao.insert(card2);
        return new ExportResult(true, "");
    }
}
