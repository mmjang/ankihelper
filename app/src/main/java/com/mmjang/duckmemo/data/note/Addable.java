package com.mmjang.duckmemo.data.note;

import java.util.List;
import java.util.Set;

public interface Addable {
    void setLanguage(String language);
    void setWord(String word);
    void setSentence(String sentence);
    void setTranslation(String translation);
    void setDefinition(String definition);
    void setExtra(String extra);
    void setTag(String tag);
    void setNewsEntryPositionId(long id);
}
