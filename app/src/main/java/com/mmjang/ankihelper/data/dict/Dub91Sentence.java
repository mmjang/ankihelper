package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class Dub91Sentence implements IDictionary {

    private static final Pattern jsonPattern = Pattern.compile("window\\.__NUXT__=(.*?);</script>");
    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "英语随心配例句";
    private static final String DICT_INTRO = "www.91dub.com";
    private static final String[] EXP_ELE = new String[] {"单词", "随心配例句"};

    private static final String wordUrl = "https://www.91dub.com/api/sub_seek_ytb.php?keyword=%s&pageno=%s";
    private static final String mp3Url = "https://audio.vocab.com/1.0/us/";
    private static final String tplt_card = "<div class='eudic_sentence'>" +
            "<div class='dub91_en'>%s</div>" +
            "<div class='dub91_cn'>%s</div>" +
            "<div class='dub91_title'>%s</div>" +
            "<img class='dub91_img' src='%s'/>" +
             "</div>";
    private static final String tplt_ui = "" +
            "<div>%s</div>" +
            "<div>%s</div>" +
            "<span>%s</span>" +
            "";

    public Dub91Sentence(Context context){

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
            Request request = new Request.Builder().url(String.format(wordUrl, key, 1))
                    //.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36")
                    .addHeader("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            List<Definition> definitionList = new ArrayList<>();
            JSONObject json = new JSONObject(rawhtml);
            JSONArray results = json.getJSONArray("seek_list");
            for(int i = 0; i < results.length(); i ++){
                JSONObject item = results.getJSONObject(i);
                String en = item.getString("sub_orig")
                        .replaceAll("<em>","<b>")
                        .replaceAll("</em>","</b>");
                String cn = item.getString("sub_trans")
                        .replaceAll("<em>","<b>")
                        .replaceAll("</em>","</b>");
                String channel = item.getString("chn_nm");
                String imgUrl = item.getString("cover_img").replace("style/w3", "style/w1");
                String imgName = key + "_91dub_" + Utils.getRandomHexString(8) + ".png";

                HashMap<String, String> eleMap = new HashMap<>();
                eleMap.put(EXP_ELE[0], key);
                eleMap.put(EXP_ELE[1], String.format(tplt_card,
                        en,
                        cn,
                       "<font color=#808080>" + channel + "</font>",
                        Constant.IMAGE_SUB_DIRECTORY + File.separator + imgName
                ));
                String html = String.format(tplt_ui,
                        en,
                        cn,
                        "<font color=#808080>" + channel + "</font>"
                        );
                definitionList.add(new Definition(eleMap, html, imgUrl, imgName, "", ""));
            }
            return definitionList;

        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        } catch (JSONException e){
            return new ArrayList<>();
        }

    }

    public void processOneSentence(String key, Element audioEle, List<Definition> definitionList) throws IOException{
        HashMap<String, String> eleMap = new HashMap<>();

        String fileName = key.trim() + "_" + Utils.getRandomHexString(8) + ".mp3";
        String audioId = audioEle.attr("source");
        audioId = URLDecoder.decode(audioId, "UTF-8");
        audioId = audioId.replace("/","_");
        String audioUrl = String.format("https://fs-gateway.esdict.cn/store_main/sentencemp3/%s.mp3", audioId);
//                Elements a = audioEle.select("div.content > p.line a");
//                String audioUrl = "";
//                if(a.size() > 0){
//                    audioUrl = "http://api.frdic.com/api/v2/speech/speakweb?" + a.get(0).attr("data-rel");
//                }else{
//                    continue;
//                }
        //audioUrl = URLDecoder.decode(audioUrl, "UTF-8");
        String audioTag = String.format("[sound:%s]", Constant.AUDIO_SUB_DIRECTORY + File.separator + fileName);
        String channelTitle = getSingleQueryResult(audioEle, "div.channel > span.channel_title", false);
        String en = getSingleQueryResult(audioEle, "div.content > p.line", true);
        en = en.replaceAll("<a href=.*?</a>$", "");
        en = en.replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>");
        String cn = getSingleQueryResult(audioEle, "div.content > p.exp", true);
        cn = cn.replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>");
        String html = String.format(tplt_card,
                en,
                audioTag,
                cn,
                "<font color=grey>" + channelTitle + "</font>"
        );

        String html_ui = String.format(tplt_ui,
                en,
                cn,
                "<font color=grey>" + channelTitle + "</font>"
        );
        eleMap.put(EXP_ELE[0], key);
        eleMap.put(EXP_ELE[1], html);
        definitionList.add(new Definition(eleMap, html_ui, "", "", audioUrl, fileName));
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

    private String getMp3Url(String id){
        return "[sound:" + mp3Url + id + ".mp3]";
    }

    private String getDefHtml(Element def){
        String sense = def.toString().replaceAll("<h3.+?>","<div class='vocab_def'>").replace("</h3>","</div>").replaceAll("<a.+?>","<b>").replace("</a>","</b>");
        //String defString = def.child(1).text().trim();
        return sense;
    }
}

