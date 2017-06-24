package com.mmjang.ankihelper.util;

/**
 * Created by liao on 2017/4/27.
 */

public class Constant {
    private static final String[] SHARED_EXPORT_ELEMENTS = new String[]{
            "空",
            "加粗的例句",
            "挖空的例句",
            "笔记",
            "URL"
    };

    public static String[] getSharedExportElements() {
        return SHARED_EXPORT_ELEMENTS;
    }

    public static final String INTENT_ANKIHELPER_TARGET_WORD = "com.mmjang.ankihelper.target_word";
    public static final String INTENT_ANKIHELPER_TARGET_URL = "com.mmjang.ankihelper.url";

}
