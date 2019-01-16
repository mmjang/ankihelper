package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;

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

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class RenRenCiDianSentence implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "人人词典原声例句";
    private static final String DICT_INTRO = "";
    private static final String[] EXP_ELE = new String[] {"单词", "原声例句"};

    private static final String wordUrl = "http://www.91dict.com/words?w=";
    private static final String tplt_card = "<div class='rrcd_sentence'>" +
            "<div class='rrcd_en'>%s %s</div>" +
            "<div class='rrcd_cn'>%s</div>" +
            "<div class='rrcd_title'>%s</div>" +
            "<img src='%s' class='rrcd_img'/>" +
            "<a class='rrcd_context' href='%s'>Context</a>" +
             "</div>";
    private static final String tplt_ui = "" +
            "<span>\uD83D\uDD0A%s</span>" +
            "<span>%s</span>" +
            "<span>%s</span>" +
            "";

    public RenRenCiDianSentence(Context context){

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
                    //.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36")
                    .addHeader("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);
            List<Definition> definitionList = new ArrayList<>();

            for(Element audioEle : doc.select("ul.slides > li")){
                HashMap<String, String> eleMap = new HashMap<>();
                String audioUrl = "";
                Elements audioElements = audioEle.select("audio");
                if(audioElements.size() > 0){
                    audioUrl = audioElements.get(0).attr("src");
                }
                String audioName = key + "_rrcd_" + Utils.getRandomHexString(8) + ".mp3";
                String imageUrl = "";
                Elements imageElements = audioEle.select("img");
                if(imageElements.size() > 0){
                    imageUrl = imageElements.get(0).attr("src");
                }
                String imageName = key + "_rrcd_" + Utils.getRandomHexString(8) + ".png";
                String channel = getSingleQueryResult(audioEle, "div.mTop", false).trim();
                String en = getSingleQueryResult(audioEle, "div.mBottom", true)
                        .replaceAll("<em>", "<b>")
                        .replaceAll("</em>", "</b>");
                String cn = getSingleQueryResult(audioEle, "div.mFoot", true)
                         .replaceAll("<em>", "<b>")
                        .replaceAll("</em>", "</b>");
                String context = getSingleQueryResult(audioEle, "div.mTextend", true);
                String detailUrl = "http://www.91dict.com" + audioEle.select("a.viewdetail").get(0).attr("href");
                String audioTag = String.format("[sound:%s]", Constant.AUDIO_SUB_DIRECTORY + File.separator + audioName);
                String html = String.format(tplt_card,
                        en,
                        audioTag,
                        cn,
                        "<font color=grey>" + channel + "</font>",
                        Constant.IMAGE_SUB_DIRECTORY + File.separator + imageName,
                        detailUrl
                        );

                String html_ui = String.format(tplt_ui,
                        en,
                        cn,
                        "<font color=grey>" + channel + "</font>"
                );
                eleMap.put(EXP_ELE[0], key);
                eleMap.put(EXP_ELE[1], html);
                definitionList.add(new Definition(eleMap, html_ui, imageUrl, imageName, audioUrl, audioName));
            }

            return definitionList;

        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

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

