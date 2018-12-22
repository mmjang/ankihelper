package com.mmjang.duckmemo.data;

/**
 * Created by liao on 2017/4/13.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.mmjang.duckmemo.domain.PronounceManager;

/**
 * 单例，getInstance()得到实例
 */
public class Settings {

    private static Settings settings = null;

    private final static String PREFER_NAME = "settings";    //应用设置名称
    private final static String MODEL_ID = "model_id";       //应用设置项 模版id
    private final static String DECK_ID = "deck_id";         //应用设置项 牌组id
    private final static String DEFAULT_MODEL_ID = "default_model_id"; //默认模版id，如果此选项存在，则已写入配套模版
    private final static String FIELDS_MAP = "fields_map";   //字段映射
    private final static String MONITE_CLIPBOARD_Q = "show_clipboard_notification_q";   //是否监听剪切板
    private final static String AUTO_CANCEL_POPUP_Q = "auto_cancel_popup";              //点加号后是否退出
    private final static String DEFAULT_PLAN = "default_plan";
    private final static String LAST_SELECTED_DICT = "last_selected_plan";
    private final static String DEFAULT_TAG = "default_tag";
    private final static String SET_AS_DEFAULT_TAG = "set_as_default_tag";
    private final static String LAST_PRONOUNCE_LANGUAGE = "last_pronounce_language";
    private final static String LEFT_HAND_MODE_Q = "left_hand_mode_q";
    private final static String PINK_THEME_Q = "pink_theme_q";
    private final static String OLD_DATA_MIGRATED = "old_data_migrated";
    private final static String SHOW_CONTENT_ALREADY_READ = "show_content_already_read";
    private final static String FIRST_TIME_RUNNING_READER = "first_time_running_reader";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    private Settings(Context context) {
        sp = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 获得单例
     *
     * @return
     */
    public static Settings getInstance(Context context) {
        if (settings == null) {
            settings = new Settings(context);
        }
        return settings;
    }

    /*************/

    Long getModelId() {
        return sp.getLong(MODEL_ID, 0);
    }

    void setModelId(Long modelId) {
        editor.putLong(MODEL_ID, modelId);
        editor.commit();
    }

    /**************/

    Long getDeckId() {
        return sp.getLong(DECK_ID, 0);
    }

    void setDeckId(Long deckId) {
        editor.putLong(DECK_ID, deckId);
        editor.commit();
    }

    /**************/

    Long getDefaultModelId() {
        return sp.getLong(DEFAULT_MODEL_ID, 0);
    }

    void setDefaultModelId(Long defaultModelId) {
        editor.putLong(DEFAULT_MODEL_ID, defaultModelId);
        editor.commit();
    }

    /**************/

    String getFieldsMap() {
        return sp.getString(FIELDS_MAP, "");
    }

    void setFieldsMap(String filedsMap) {
        editor.putString(FIELDS_MAP, filedsMap);
        editor.commit();
    }

    /**************/

    public boolean getMoniteClipboardQ() {
        return sp.getBoolean(MONITE_CLIPBOARD_Q, false);
    }

    public void setMoniteClipboardQ(boolean moniteClipboardQ) {
        editor.putBoolean(MONITE_CLIPBOARD_Q, moniteClipboardQ);
        editor.commit();
    }

    /**************/

    public boolean getAutoCancelPopupQ() {
        return sp.getBoolean(AUTO_CANCEL_POPUP_Q, false);
    }

    public void setAutoCancelPopupQ(boolean autoCancelPopupQ) {
        editor.putBoolean(AUTO_CANCEL_POPUP_Q, autoCancelPopupQ);
        editor.commit();
    }

    /**************/
    public String getDefaultPlan() {
        return sp.getString(DEFAULT_PLAN, "");
    }

    public void setDefaultPlan(String defaultPlan) {
        editor.putString(DEFAULT_PLAN, defaultPlan);
        editor.commit();
    }

    /******************/

    public String getLastSelectedDict() {
        return sp.getString(LAST_SELECTED_DICT, "");
    }

    public void setLastSelectedDict(String lastSelectedPlan) {
        editor.putString(LAST_SELECTED_DICT, lastSelectedPlan);
        editor.commit();
    }

    /*****************/
    public String getDefaulTag() {
        return sp.getString(DEFAULT_TAG, "");
    }

    public void setDefaultTag(String defaultTag) {
        editor.putString(DEFAULT_TAG, defaultTag);
        editor.commit();
    }

    /****************/
    public boolean getSetAsDefaultTag() {
        return sp.getBoolean(SET_AS_DEFAULT_TAG, false);
    }

    public void setSetAsDefaultTag(boolean setAsDefaultTag) {
        editor.putBoolean(SET_AS_DEFAULT_TAG, setAsDefaultTag);
        editor.commit();
    }

    public int getLastPronounceLanguage() {
        return sp.getInt(LAST_PRONOUNCE_LANGUAGE, PronounceManager.LANGUAGE_ENGLISH_INDEX);
    }

    public void setLastPronounceLanguage(int lastPronounceLanguageIndex) {
        editor.putInt(LAST_PRONOUNCE_LANGUAGE, lastPronounceLanguageIndex);
        editor.commit();
    }

    public boolean getLeftHandModeQ(){
        return  sp.getBoolean(LEFT_HAND_MODE_Q, false);
    }

    public void setLeftHandModeQ(boolean leftHandModeQ){
        editor.putBoolean(LEFT_HAND_MODE_Q, leftHandModeQ);
        editor.commit();
    }

    public boolean getPinkThemeQ(){
        return  sp.getBoolean(PINK_THEME_Q, false);
    }

    public void setPinkThemeQ(boolean pinkThemeQ){
        editor.putBoolean(PINK_THEME_Q, pinkThemeQ);
        editor.commit();
    }

    public boolean getOldDataMigrated(){
        return sp.getBoolean(OLD_DATA_MIGRATED, false);
    }

    public void setOldDataMigrated(boolean oldDataMigrated){
        editor.putBoolean(OLD_DATA_MIGRATED, oldDataMigrated);
        editor.commit();
    }

    public boolean getShowContentAlreadyRead(){
        return sp.getBoolean(SHOW_CONTENT_ALREADY_READ, false);
    }

    public void setShowContentAlreadyRead(boolean showContentAlreadyRead){
        editor.putBoolean(SHOW_CONTENT_ALREADY_READ, showContentAlreadyRead);
        editor.commit();
    }

    public boolean getFirstTimeRunningReader(){
        return sp.getBoolean(FIRST_TIME_RUNNING_READER, true);
    }

    public void setFirstTimeRunningReader(boolean firstTimeRunningReader){
        editor.putBoolean(FIRST_TIME_RUNNING_READER, firstTimeRunningReader);
        editor.commit();
    }

    boolean hasKey(String key) {
        return sp.contains(key);
    }



}