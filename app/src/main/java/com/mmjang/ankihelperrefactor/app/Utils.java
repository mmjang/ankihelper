package com.mmjang.ankihelperrefactor.app;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.mmjang.ankihelperrefactor.R;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by liao on 2017/4/27.
 */

public class Utils {
    private static final String FIELDS_SEPERATOR = "@@@@";
    public static String fieldsMap2Str(HashMap<String, String> map){
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

    public static HashMap<String, String> fieldsStr2Map(String str){
        HashMap<String, String> results = new HashMap<>();
        String[] fields = str.split(FIELDS_SEPERATOR);
        int pairs = fields.length / 2;
        for(int i = 0; i < pairs; i ++){
            results.put(fields[i * 2], fields[i * 2 + 1]);
        }
        return results;
    }

    public static LinkedHashMap<Long, String> hashMap2LinkedHashMap(Map<Long, String> hashMap) {
        LinkedHashMap<Long, String> linkedHashMap = new LinkedHashMap<Long, String>();
        Long[] keyArray = new Long[hashMap.size()];
        int i = 0;
        for(Long id : hashMap.keySet()){
            keyArray[i] = id;
            i ++;
        }
        Arrays.sort(keyArray);
        for (Long k : hashMap.keySet()) {
            linkedHashMap.put(k, hashMap.get(k));
        }
        return linkedHashMap;
    }

    public static long[] getMapKeyArray(Map<Long, String> map){
        long[] keyArr = new long[map.size()];
        int i = 0;
        for(long id : map.keySet()){
            keyArr[i] = id;
            i ++;
        }
        return keyArr;
    }

    public static String[] getMapValueArray(Map<Long, String> map){
        String[] valArr = new String[map.size()];
        int i = 0;
        for(String val : map.values()){
            valArr[i] = val;
            i ++;
        }
        return valArr;
    }

    public static long findMapKeyByVal(Map<Long, String> map, String val){
        for(long key : map.keySet()){
            if(map.get(key).equals(val))
                return key;
        }
        return -1;
    }

    public static <T> T[] concatenate (T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
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

}
