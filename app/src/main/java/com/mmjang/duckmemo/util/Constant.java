package com.mmjang.duckmemo.util;

/**
 * Created by liao on 2017/4/27.
 */

public class Constant {
    private static final String[] SHARED_EXPORT_ELEMENTS = new String[]{
            "空",
            "例句",
            "加粗的例句",
            "挖空的例句",
            "笔记",
            "URL",
            "所有释义",
            "句子翻译"
            //"FBReader跳转链接"
    };

    public static String[] getSharedExportElements() {
        return SHARED_EXPORT_ELEMENTS;
    }

    public static final String INTENT_DUCKMEMO_TARGET_WORD = "com.mmjang.duckmemo.target_word";
    public static final String INTENT_DUCKMEMO_TARGET_URL = "com.mmjang.duckmemo.url";
    public static final String INTENT_DUCKMEMO_NOTE_ID = "com.mmjang.duckmemo.note_id";
    public static final String INTENT_DUCKMEMO_UPDATE_ACTION = "com.mmjang.duckmemo.note_update_action";//replace;append;
    public static final String INTENT_DUCKMEMO_BASE64 = "com.mmjang.duckmemo.base64";
    public static final String INTENT_DUCKMEMO_PLAN_NAME = "com.mmjang.duckmemo.plan_name";
    public static final String INTENT_DUCKMEMO_FBREADER_BOOKMARK_ID = "com.mmjang.duckmemo.fbreader.bookmark.id";
    public static final String ANKI_PACKAGE_NAME = "com.ichi2.anki";
    public static final String FBREADER_URL_TMPL = "<a href=\"intent:#Intent;action=android.fbreader.action.VIEW;category=android.intent.category.DEFAULT;type=text/plain;component=org.geometerplus.zlibrary.ui.android/org.geometerplus.android.fbreader.FBReader;S.fbreader.bookmarkid.from.external=%s;end;\">查看原文</a>";
    static final public String INTENT_DUCKMEMO_NOTE = "com.mmjang.duckmemo.note";
    public static final int VIBRATE_DURATION = 10;

    public static final float FLOAT_ACTION_BUTTON_ALPHA = 0.3f;

    public static final String EXTERNAL_STORAGE_DIRECTORY = "duckmemo";
    public static final String EXTERNAL_STORAGE_CONTENT_SUBDIRECTORY = "content";
    public static final String LEFT_BOLD_SUBSTITUDE = "☾";
    public static final String RIGHT_BOLD_SUBSTITUDE = "☽";

    public static final String INTENT_DUCKMEMO_CONTENT_INDEX = "com.mmjang.duckmemo.content_index";

    public static final String INTENT_DUCKMEMO_NEWS_ID = "com.mmjang.duckmemo.news_id";
    public static final String INTENT_DUCKMEMO_NEWS_ENTRY_POSITION_ID = "com.mmjang.duckmemo.news_entry_position_id";
    public static final String INTENT_DUCKMEMO_NEWS_POSITION_INDEX = "com.mmjang.duckmemo.news_position_index";


}
