package com.mmjang.duckmemo.data.note;

import java.util.List;
import java.util.Set;

public interface Addable {
    void setLanguage(String language);
    void setWord(String word);
    void setSentence(String sentence);
    void setTranslation(String translation);
    void setDefinition(List<String> definition);
    void setTag(Set<String> tag);
}
