package com.mmjang.duckmemo.data.card;

import com.mmjang.duckmemo.data.note.Note;

import static com.mmjang.duckmemo.data.card.CardType.CLOZE;
import static com.mmjang.duckmemo.data.card.CardType.SENTENCE_DEFINITION;

public class CardHtmlGenerator {
    public static String[] getCard(Note note, int cardType){
        switch (cardType){
            case SENTENCE_DEFINITION:
                return makeSenDefCard(note);
            case CLOZE:
                return makeClozeCard(note);
        }
        return new String[2];
    }

    private static String[] makeClozeCard(Note note) {
        String front = String.format(
                "<p>%s</p> <br/> <p>%s</p>", note.getSentence().replaceAll("<b>(.*?)</b>", "<b>____</b>"),
                note.getTranslation()
        );
        String back = String.format(
                "<p>%s</p><br/><p>%s</p><br/>%s<br/>%s",
                note.getSentence(), note.getTranslation(), note.getDefinition(), note.getExtra()
        );
        return new String[] {front, back};
    }

    private static String[] makeSenDefCard(Note note) {
        String front = String.format(
                "<p>%s</p>", note.getSentence()
        );
        String back = String.format(
                "<p>%s</p><br/><p>%s</p><br/><p>%s<p/><br/><p>%s<p/>",
                note.getSentence(), note.getTranslation(), note.getDefinition(), note.getExtra()
        );
        return new String[] {front, back};
    }
}
