package com.mmjang.ankihelper.util;

import com.mmjang.ankihelper.util.com.baidu.translate.demo.TransApi;

import org.json.JSONException;
import org.json.JSONObject;

public class Translator {
    private static final String APP_ID = "20160220000012831";
    private static final String SECURITY_KEY = "ISSPx0K_ZyrUN9IAOKel";
    private static TransApi api;
    public static String translate(String query, String from, String to){
        if(api == null) api = new TransApi(APP_ID, SECURITY_KEY);
        String jsonStr = api.getTransResult(query, from , to);
        try {
            JSONObject json = new JSONObject(jsonStr);
            String result =json.getJSONArray("trans_result").getJSONObject(0).getString("dst");
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static void main(String[] args) {
//        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
//        String query = "高度600米";
//        System.out.println(api.getTransResult(query, "auto", "cn"));
        System.out.println(Translator.translate("i am a big fat guy", "auto", "zh"));
    }
}
