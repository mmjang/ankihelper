package com.mmjang.duckmemo.domain;

/**
 * Created by Gao on 2017/7/15.
 */

public class PronounceManager {

    public static final String LANGUAGE_ENGLISH = "英语发音";
    public static final String LANGUAGE_FRENCHE = "法语发音";
    public static final String LANGUAGE_JAPANESE = "日语发音";
    public static final String LANGUAGE_KOREAN = "韩语发音";
    public static final String LANGUAGE_GERMANY = "德语发音";

    public static final int LANGUAGE_ENGLISH_INDEX = 0;
    public static final int LANGUAGE_FRENCH_INDEX = 1;
    public static final int LANGUAGE_JAPANESE_INDEX = 2;
    public static final int LANGUAGE_KOREAN_INDEX = 3;
    public static final int LANGUAGE_GERMANY_INDEX = 4;


    public static final String YOUDAO_PRONOUNCE_TYPE_ENGLISH = "eng";
    public static final String YOUDAO_PRONOUNCE_TYPE_FRENCH = "fr";
    public static final String YOUDAO_PRONOUNCE_TYPE_JAPANESE = "jap";
    public static final String YOUDAO_PRONOUNCE_TYPE_KOREAN = "ko";
    public static final String YOUDAO_PRONOUNCE_TYPE_GERMANY = "ger";

    public static String[] getAvailablePronounceLanguage() {
        String[] languages = new String[5];
        languages[LANGUAGE_ENGLISH_INDEX] = LANGUAGE_ENGLISH;
        languages[LANGUAGE_FRENCH_INDEX] = LANGUAGE_FRENCHE;
        languages[LANGUAGE_JAPANESE_INDEX] = LANGUAGE_JAPANESE;
        languages[LANGUAGE_KOREAN_INDEX] = LANGUAGE_KOREAN;
        languages[LANGUAGE_GERMANY_INDEX] = LANGUAGE_GERMANY;
        return languages;
    }

    public static String getYoudaoTypeFromLanguageIndex(int index) {
        switch (index) {
            case LANGUAGE_ENGLISH_INDEX:
                return YOUDAO_PRONOUNCE_TYPE_ENGLISH;
            case LANGUAGE_FRENCH_INDEX:
                return YOUDAO_PRONOUNCE_TYPE_FRENCH;
            case LANGUAGE_JAPANESE_INDEX:
                return YOUDAO_PRONOUNCE_TYPE_JAPANESE;
            case LANGUAGE_KOREAN_INDEX:
                return YOUDAO_PRONOUNCE_TYPE_KOREAN;
            case LANGUAGE_GERMANY_INDEX:
                return YOUDAO_PRONOUNCE_TYPE_GERMANY;
            default:
                return YOUDAO_PRONOUNCE_TYPE_ENGLISH;
        }

    }
}
