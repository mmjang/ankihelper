package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class UrbanDict implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "俚语词典";
    private static final String DICT_INTRO = "数据来自 urban dictionary ";
    private static final String[] EXP_ELE = new String[] {"单词", "释义", "例句", "复合项"};

    private static final String wordUrl = "http://api.urbandictionary.com/v0/define?term=";
    //private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public UrbanDict(Context context) {

    }

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public String[] getExportElementsList() {
        return EXP_ELE;
    }

    public List<Definition> wordLookup(String key) {
        try {
//            Document doc = Jsoup.connect(wordUrl + key)
//                    .userAgent("Mozilla")
//                    .timeout(5000)
//                    .get();
            Request request = new Request.Builder().url(wordUrl + key)
                    .header("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            //Document doc = Jsoup.parse(rawhtml);
            JSONObject ub = new JSONObject(rawhtml);
            ArrayList<Definition> defList = new ArrayList<>();
            //Elements posList = doc.select("div.def-panel");

            ///
            JSONArray defArray = ub.getJSONArray("list");
            for(int i = 0; i < defArray.length(); i ++){
                JSONObject def = defArray.getJSONObject(i);
                String word = def.getString("word");
                String definition = def.getString("definition").replaceAll("[\\[\\]]", "")
                        .replaceAll("\r\n", "<br/>");
                String example = def.getString("example").replaceAll("[\\[\\]]", "")
                        .replaceAll("\r\n", "<br/>");
                HashMap<String, String> defMap = new HashMap<>();
                //String definition = posType + " " + def;
                defMap.put(EXP_ELE[0], word);
                defMap.put(EXP_ELE[1], definition);
                defMap.put(EXP_ELE[2], example);
                String html = "<div class=lwzj><b>" + word +" </b> " + "-" + "<span class=ys>" + definition +"</span><br/><font color=#4682B4><span class=yl>•" + example + "</span> </font></div>";
                defMap.put(EXP_ELE[3], html);
                defList.add(new Definition(defMap, html));
            }
            return defList;
        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        } catch (JSONException je){
            return new ArrayList<>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return new UrbanAutoCompleteAdapter(context, layout);
    }


}
