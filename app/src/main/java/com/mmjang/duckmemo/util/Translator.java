package com.mmjang.duckmemo.util;

import android.widget.Toast;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.util.com.baidu.translate.demo.RandomAPIKeyGenerator;
import com.mmjang.duckmemo.util.com.baidu.translate.demo.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Translator {
    private static final String APP_ID = "20160220000012831";
    private static final String SECURITY_KEY = "ISSPx0K_ZyrUN9IAOKel";
    private static TransApi api;
    public static String translate(String query, String from, String to){
        //remove line break
        //query = query.replaceAll("\n","");
        if(api == null) {
            String[] appAndKey = RandomAPIKeyGenerator.next();
            api = new TransApi(appAndKey[0], appAndKey[1]);
        }
        String jsonStr = "";
        try {
            jsonStr = api.getTransResult(query, from , to);
            JSONObject json = new JSONObject(jsonStr);
            JSONArray resultArray = json.getJSONArray("trans_result");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < resultArray.length() - 1; i++) {
                sb.append(resultArray.getJSONObject(i).getString("dst"));
                sb.append("\n");
            }
            if (resultArray.length() > 0) {
                sb.append(resultArray.getJSONObject(resultArray.length() - 1).getString("dst"));
            }
            return sb.toString();
        } catch (JSONException e) {
            //Toast.makeText(MyApplication.getContext(), e.getMessage() + jsonStr, Toast.LENGTH_LONG).show();
            return "error\n" + e.getMessage() + "\n" + jsonStr;
        }
    }
    public static void main(String[] args) {
//        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
//        String query = "高度600米";
//        System.out.println(api.getTransResult(query, "auto", "cn"));
        System.out.println(Translator.translate("i am a big fat guy", "auto", "zh"));
    }
}
