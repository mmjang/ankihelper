package com.mmjang.ankihelper.data.dict.JPDeinflector;

/**
 * This class stores the before and after of an inflection
 * and the form it represents, e.g. past tense.
 *
 * @author James Kirk
 */
public class Inflection {
    /**
     * The inflected end of the word.
     */
    private final String inflection;
    /**
     * The uninflected end of the word.
     */
    private final String base;
    /**
     * The class of the inflection (passive, past, negative, etc.)
     */
    private final int form;

    public Inflection(String inflection, String base, int form) {
        this.inflection = inflection;
        this.base = base;
        this.form = form;
    }

    public String getInflection() {
        return inflection;
    }

    public String getBase() {
        return base;
    }

    public int getForm() {
        return form;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%s) ",
                inflection, base, form);
    }
}

