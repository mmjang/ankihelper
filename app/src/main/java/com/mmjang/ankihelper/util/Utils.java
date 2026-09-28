package com.mmjang.ankihelper.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ichi2.anki.FlashCardsContract;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.dict.Definition;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.ui.LauncherActivity;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by liao on 2017/4/27.
 */

public class Utils {
    private static final String FIELDS_SEPERATOR = "@@@@";

    public static String fieldsMap2Str(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append(FIELDS_SEPERATOR);
            sb.append(value);
            sb.append(FIELDS_SEPERATOR);
        }
        return sb.toString();
    }

    public static Map<String, String> fieldsStr2Map(String str) {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        String[] fields = str.split(FIELDS_SEPERATOR);
        int pairs = fields.length / 2;
        for (int i = 0; i < pairs; i++) {
            results.put(fields[i * 2], fields[i * 2 + 1]);
        }
        return results;
    }

    public static LinkedHashMap<Long, String> hashMap2LinkedHashMap(Map<Long, String> hashMap) {
        LinkedHashMap<Long, String> linkedHashMap = new LinkedHashMap<Long, String>();
        Long[] keyArray = new Long[hashMap.size()];
        int i = 0;
        for (Long id : hashMap.keySet()) {
            keyArray[i] = id;
            i++;
        }
        Arrays.sort(keyArray);
        for (Long k : hashMap.keySet()) {
            linkedHashMap.put(k, hashMap.get(k));
        }
        return linkedHashMap;
    }

    public static long[] getMapKeyArray(Map<Long, String> map) {
        long[] keyArr = new long[map.size()];
        int i = 0;
        for (long id : map.keySet()) {
            keyArr[i] = id;
            i++;
        }
        return keyArr;
    }

    public static String[] getMapValueArray(Map<Long, String> map) {
        String[] valArr = new String[map.size()];
        int i = 0;
        for (String val : map.values()) {
            valArr[i] = val;
            i++;
        }
        return valArr;
    }

    public static long findMapKeyByVal(Map<Long, String> map, String val) {
        for (long key : map.keySet()) {
            if (map.get(key).equals(val))
                return key;
        }
        return -1;
    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static int getPX(Context context, int dp) {
        // margin in dips
        //int dpValue = 5; // margin in dips
        float d = context.getResources().getDisplayMetrics().density;
        int margin = (int) (dp * d);
        return margin;
    }

    public static void hideSoftKeyboard(Activity context) {
        //Hides the SoftKeyboard
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = context.getCurrentFocus();
        if(currentFocus != null){
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public static int getArrayIndex(long[] arr, long value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value) return i;
        return -1;
    }

    public static String getAllHtmlFromDefinitionList(List<Definition> defList){
        if(defList.size() <= 1){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for(Definition def : defList){
            sb.append("<div>");
            sb.append(def.getDisplayHtml());
            sb.append("</div>");
            sb.append("<br/>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    public static boolean containsTranslationField(OutputPlanPOJO outputPlan){
        if(outputPlan == null){
            return false;
        }
        Map<String, String> map = outputPlan.getFieldsMap();
        for(String key : map.keySet()){
            if(map.get(key).equals("句子翻译")){
                return true;
            }
        }
        return false;
    }

    public static int getResIdFromAttribute(final Activity activity,final int attr)
    {
        TypedArray a = activity.getTheme().obtainStyledAttributes(
                activity.getApplicationInfo().theme, new int[] {attr});
        int attributeResourceId = a.getResourceId(0, 0);
        a.recycle();
        return attributeResourceId;
    }

    public static boolean deleteNote(Context context, long noteid){
        ContentResolver cr = context.getContentResolver();
        if(cr == null) return false;
        Uri noteUri = Uri.withAppendedPath(FlashCardsContract.Note.CONTENT_URI, Long.toString(noteid));
        int i = cr.delete(noteUri, null, null);
        if(i == 1) {
            return true;
        }
        else{
            return false;
        }
    }

    public static String renderTmpl(String tmpl, Map<String, String> dataMap) {
        tmpl = tmpl.trim();
        for (String key : dataMap.keySet()) {
            tmpl = tmpl.replace("{{" + key + "}}", dataMap.get(key));
        }
        return tmpl;
    }

    public static String keyCleanup(String key) {
        return key.trim().replaceAll("[,.!?()\"'“”’？]", "").toLowerCase();
    }

    public static void showMessage(Context context, String message){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                }).show();
    }

    public static String fromTagSetToString(Set<String> tagSet){
        String tags = "";
        int size = tagSet.size();
        int i = 0;
        for(String tag : tagSet){
            if(i < size - 1){
                tags = tags + tag.trim() + ",";
            }else{
                tags = tags + tag.trim();
            }
            i ++;
        }
        return tags;
    }

    public static Set<String> fromStringToTagSet(String s){
        Set<String> set = new HashSet<>();
        for(String item : s.split(",")){
            set.add(item);
        }
        return set;
    }

    public static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
}
