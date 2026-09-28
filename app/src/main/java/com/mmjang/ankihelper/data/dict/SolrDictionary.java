package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class SolrDictionary implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "朗文双解例句";
    private static final String DICT_INTRO = "";
    private static final String[] EXP_ELE = new String[] {"单词", "英文", "中文", "音频", "来源", "复合项"};
    private static final String wordUrl = "http://35.241.69.245:8983/solr/anki/select?q=";
    private static final String mp3Url = "https://audio.vocab.com/1.0/us/";
    private static final String tplt_card = "<div class='solr_sentence'>" +
            "<div class='solr_en'>%s %s</div>" +
            "<div class='solr_cn'>%s</div>" +
            "<div class='solr_title'>%s</div>" +
             "</div>";
    private static final String tplt_ui = "" +
            "<div>%s</div>" +
            "<div>%s</div>" +
            "<span>%s</span>" +
            "";
    private static final String tplt_ui_audio = "" +
            "<div>%s\uD83D\uDD0A</div>" +
            "<div>%s</div>" +
            "<span>%s</span>" +
            "";

    public SolrDictionary(Context context){

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
            String q = generageSolrQueryFromKey(key);
            if(q.trim().isEmpty()){
                return new ArrayList<>();
            }
            Request request = new Request.Builder().url(wordUrl + q)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            JSONObject jsonObject = new JSONObject(rawhtml);
            JSONArray responceDocs = jsonObject.getJSONObject("response").getJSONArray("docs");
            JSONObject highlights = null;
            if(jsonObject.has("highlighting")){
                highlights = jsonObject.getJSONObject("highlighting");
            }
            List<Definition> definitionList = new ArrayList<>();
            for(int i = 0; i < responceDocs.length(); i ++){
                JSONObject oneResult = responceDocs.getJSONObject(i);
                String id = oneResult.getString("id");
                String en = oneResult.getString("en");
                String cn = "";
                if(oneResult.has("cn")) {
                    cn = oneResult.getString("cn");
                }
                //replace original with highlights
                if(highlights != null && highlights.has(id)){
                    JSONObject hl = highlights.getJSONObject(id);
                    if(hl.has("cn")){
                        String cn_hl = hl.getJSONArray("cn").getString(0);
                        cn = cn.replace(em2null(cn_hl), em2b(cn_hl));
                    }
                    if(hl.has("en")){
                        String en_hl = hl.getJSONArray("en").getString(0);
                        en = en.replace(em2null(en_hl), em2b(en_hl));
                    }
                }
                String source = oneResult.getString("source");
                String audio = "";
                String audioName = "";
                String audioTag = "";
                if(oneResult.has("audio")){
                    audio = oneResult.getString("audio");
                    if(source.equals("朗文双解")) {
                        audio = audio.replace("sound://media/", "http://35.241.69.245:8080/LDOCE/");
                        audioName = audio.replace("http://35.241.69.245:8080/LDOCE/", "").replace("/", "_");
                    }
                    if(source.equals("LAAD")){
                        audio = audio.replace("sound://", "http://35.241.69.245:8080/LAAD/");
                        audioName = audio.replace("http://35.241.69.245:8080/LAAD/", "").replace("/", "_");
                    }
                    audioTag = String.format("[sound:%s]", Constant.AUDIO_SUB_DIRECTORY + File.separator + audioName);
                }
                HashMap<String, String> eleMap = new HashMap<>();
                eleMap.put(EXP_ELE[0], key);
                eleMap.put(EXP_ELE[1], en);
                eleMap.put(EXP_ELE[2], cn);
                eleMap.put(EXP_ELE[3], audioTag);
                eleMap.put(EXP_ELE[4], source);
                String uiString = "";
                if(audio.isEmpty()) {
                    uiString = String.format(
                            tplt_ui,
                            en, cn, "<font color=grey>" + source + "</font>"
                    );
                }else{
                    uiString = String.format(
                            tplt_ui_audio,
                            en, cn, "<font color=grey>" + source + "</font>"
                    );
                }
                String cardString = String.format(
                        tplt_card, en, audioTag, cn, "<font color=grey>" + source + "</font>"

                );
                eleMap.put(EXP_ELE[5], cardString);
                definitionList.add(
                        new Definition(
                                eleMap,
                                uiString,
                                "",
                                "",
                                audio,
                                audioName
                        )
                );
            }
            return definitionList;

        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        } catch (JSONException ioe){
            return new ArrayList<>();
        }

    }

    String mLastKey = "";

    private String generageSolrQueryFromKey(String key) {
        boolean rand = false;

        if(mLastKey.equals(key)){
            rand = true;
        }
        mLastKey = key;
        String[] splitted = key.split(" ");
        for(int i = 0; i < splitted.length; i ++){
            splitted[i] = splitted[i].trim();
        }
        String query = "";
        if(splitted.length > 0) {
            if (RegexUtil.isChineseSentence(splitted[0])) {
                query += "cn:" + splitted[0];
            } else {
                query += "en:" + splitted[0];
            }
            for (int i = 1; i < splitted.length; i++) {
                String ss = splitted[i];
                if (RegexUtil.isChineseSentence(ss)) {
                    query += " AND cn:" + ss;
                } else {
                    query += " AND en:" + ss;
                }
            }
        }
        //query += "&sort=order asc&rows=100&hl=on&hl.fl=en,cn";
        if(rand){
            String seed = getRandomHexString(16);
            query += "&sort=random_" + seed + " asc&rows=100&hl=on&hl.fl=en,cn";
        }else {
            query += "&rows=100&hl=on&hl.fl=en,cn";
        }
        return query;
    }

    static private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

    public static String em2b(String b){
        return b.replaceAll("<em>","<b>")
                .replaceAll("</em>","</b>");
    }

    public static String em2null(String b){
        return b.replaceAll("<em>","")
                .replaceAll("</em>","");
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

    static String getSingleQueryResult(Document soup, String query, boolean toString){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            if(toString){
                return re.get(0).toString();
            }else {
                return re.get(0).text();
            }
        }else{
            return "";
        }
    }

    static String getSingleQueryResult(Element soup, String query, boolean toString){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            if(toString) {
                return re.get(0).toString();
            }
            else{
                return re.get(0).text();
            }
        }else{
            return "";
        }
    }
}

