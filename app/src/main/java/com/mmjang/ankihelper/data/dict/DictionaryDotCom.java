package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.com.baidu.translate.demo.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class DictionaryDotCom implements IDictionary {

    private static final String DICT_NAME = "dictionary.com";
    private static final String DICT_INTRO = "英英词典，收词量大，释义全面\n" +
            "复合项 css class：";
    private static final String[] EXP_ELE = new String[]{"单词", "音标", "发音", "释义", "复合项"};

    private static final String wordUrl = "http://restcdn.dictionary.com/v2/word.json/";
    private static final String urlParams = "/completeFormatted?api_key=I6SnT6uSpyaarEn&audio=mp3&entry=all&part=all&hotlinks=on&platform=android&app_id=dcomAndroidFreeV7513";

    public DictionaryDotCom(Context context) {

    }

    public DictionaryDotCom(){

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

//            String doc = HttpGet.get(wordUrl + key + urlParams, null);
            Request request = new Request.Builder().url(wordUrl + key + urlParams)
                    .header("User-Agent", Constant.UA)
                    .build();
            String doc = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            JSONObject docJson = new JSONObject(doc);
            JSONArray entries = docJson.getJSONArray("entries");
            ArrayList<Definition> defList = new ArrayList<>();
            for(int i = 0; i < entries.length(); i ++){
                JSONObject entry = entries.getJSONObject(i);
                String headWord = entry.getString("entry");
                String prounceIPA = entry.getString("pronunciationIpa");
                String audioURL = entry.getString("audioUrlMp3");
                audioURL = "[sound:" + audioURL + "]";
                JSONArray dataParts = entry.getJSONArray("dataParts");
                for(int j = 0; j < dataParts.length(); j ++){
                    JSONObject dataPartObj = dataParts.getJSONObject(j);
                    String type = dataPartObj.getString("type");
                    if(!type.equals("pos-block")){
                        continue;
                    }
                    String pos = dataPartObj.getString("pos");
                    String content = dataPartObj.getString("content");
                    Document contentDoc = Jsoup.parse("<html><body>" + content + "</body></html>");
                    Elements smallElements = contentDoc.select("li");
                    for(int n = 0; n < smallElements.size(); n ++){
                        String defHtml = smallElements.get(n).text().replaceAll("\n", "<br/>")
                                .replaceAll(":(.+?)$", ":<font color='gray'><i>$1</i></font>");
                        HashMap<String, String> expele = new HashMap<>();
                        expele.put(EXP_ELE[0], headWord);
                        expele.put(EXP_ELE[1], prounceIPA);
                        expele.put(EXP_ELE[2], audioURL);
                        expele.put(EXP_ELE[3], "<i>" + pos + "</i>" + "<br/>" + defHtml);
                        expele.put(EXP_ELE[4],
                                "<div class='div_dictionary'><div class='dictionary_hwd'>" + headWord + "</div>" +
                                        "<div class='dictionary_ipa'>" + "/" + prounceIPA +"/" + audioURL + "</div>" +
                                        "<div class='dictionary_def'><i>" + pos + "</i>" + "<br/>" + defHtml + "</div></div>"
                        );
                        String exportedHtml;
                        if(n == 0){
                            exportedHtml = "<b>" + headWord + " </b>" +
                                    "<i>" + pos + "</i>" + "<br/>" + defHtml;
                        }
                        else{
                            exportedHtml = defHtml;
                        }
                        Definition d = new Definition(expele, exportedHtml);
                        defList.add(d);
                    }
                }
            }
            return defList;
        } catch (Exception ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

    public static void main(String[] args){
        DictionaryDotCom dictionaryDotCom = new DictionaryDotCom();
        dictionaryDotCom.wordLookup("word");
    }
}