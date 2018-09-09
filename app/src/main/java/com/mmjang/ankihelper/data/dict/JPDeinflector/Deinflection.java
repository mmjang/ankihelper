package com.mmjang.ankihelper.data.dict.JPDeinflector;

/**
 * This class stores data related to a deinflection.
 *
 * @author James Kirk
 */
public class Deinflection {

    /**
     * The original inflected term.
     */
    private final String inflectedWord;
    /**
     * The deinflected base form of the word.
     */
    private final String baseForm;
    /**
     * The inflection (passive, past, causative, etc.)
     */
    private final int form;
    /**
     * The class of inflection (adjective, godan, kuru, etc.)
     */
    private final int inflectionType;

    public Deinflection(String inflectedWord, String baseForm,
                        int form, int inflectionType) {
        this.inflectedWord = inflectedWord;
        this.baseForm = baseForm;
        this.form = form;
        this.inflectionType = inflectionType;
    }

    public String getInflectedWord() {
        return inflectedWord;
    }

    public String getBaseForm() {
        return baseForm;
    }

    public int getForm() {
        return form;
    }

    public int getInflectionType() {
        return inflectionType;
    }

    @Override
    public int hashCode() {
        return baseForm.hashCode() * 7;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Deinflection) &&
                ((Deinflection)obj).getBaseForm().equals(baseForm);
    }

    @Override
    public String toString() {
        return String.format("%s [%d]", baseForm, inflectionType);
    }
}