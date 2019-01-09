package com.mmjang.ankihelper.util;

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

    public static final String INTENT_ANKIHELPER_TARGET_WORD = "com.mmjang.ankihelper.target_word";
    public static final String INTENT_ANKIHELPER_TARGET_URL = "com.mmjang.ankihelper.url";
    public static final String INTENT_ANKIHELPER_NOTE_ID = "com.mmjang.ankihelper.note_id";
    public static final String INTENT_ANKIHELPER_UPDATE_ACTION = "com.mmjang.ankihelper.note_update_action";//replace;append;
    public static final String INTENT_ANKIHELPER_BASE64 = "com.mmjang.ankihelper.base64";
    public static final String INTENT_ANKIHELPER_PLAN_NAME = "com.mmjang.ankihelper.plan_name";
    public static final String INTENT_ANKIHELPER_FBREADER_BOOKMARK_ID = "com.mmjang.ankihelper.fbreader.bookmark.id";
    public static final String ANKI_PACKAGE_NAME = "com.ichi2.anki";
    public static final String FBREADER_URL_TMPL = "<a href=\"intent:#Intent;action=android.fbreader.action.VIEW;category=android.intent.category.DEFAULT;type=text/plain;component=org.geometerplus.zlibrary.ui.android/org.geometerplus.android.fbreader.FBReader;S.fbreader.bookmarkid.from.external=%s;end;\">查看原文</a>";
    static final public String INTENT_ANKIHELPER_NOTE = "com.mmjang.ankihelper.note";
    public static final int VIBRATE_DURATION = 10;

    public static final float FLOAT_ACTION_BUTTON_ALPHA = 0.3f;

    public static final String EXTERNAL_STORAGE_DIRECTORY = "ankihelper";
    public static final String EXTERNAL_STORAGE_CONTENT_SUBDIRECTORY = "content";
    public static final String LEFT_BOLD_SUBSTITUDE = "☾";
    public static final String RIGHT_BOLD_SUBSTITUDE = "☽";

    public static final String INTENT_ANKIHELPER_CONTENT_INDEX = "com.mmjang.ankihelper.content_index";
    public static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
}
