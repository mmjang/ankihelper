package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liao on 2017/4/28.
 */

public class HujiangJapanese implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "沪江日语在线";
    private static final String DICT_INTRO = "数据来自沪江日语词典. “音频”项是形如 [sound:xxx.mp3] 的发音，使用之前默认模版的用户需要编辑模版并在里面加入{{audio}}";
    private static final String[] EXP_ELE = new String[] {"单词", "注音", "音频", "释义"};

    private static final String wordUrl = "https://www.hjdict.com/jp/jc/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public HujiangJapanese(Context context) {

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
            Document doc = Jsoup.connect(wordUrl + key)
                    .userAgent("Mozilla")
                    .timeout(5000)
                    .get();
            Elements entrys = doc.select("div.word-details-pane");
            ArrayList<Definition> defList = new ArrayList<>();
            if (entrys.size() > 0) {
                for (Element ele : entrys){
                    String word = "";
                    String reading = "";
                    String mp3_url = "";
                    //String meaning_tag = "";
                    //String definition = "";
                    Elements furigana_soup = ele.select(".word-text");
                    if(furigana_soup.size() > 0){
                        word = furigana_soup.get(0).text().trim();
                    }

                    Elements writing_soup = ele.select("div.pronounces");
                    if(writing_soup.size() > 0){
                        reading = writing_soup.get(0).text().trim();
                    }

                    Elements audio_soup = ele.select("span.word-audio");
                    if(audio_soup.size() > 0){
                        mp3_url = "[sound:" +audio_soup.get(0).attr("data-src") + "]";
                    }

                    Elements meaning_tags_soup = ele.select(".simple li");
                    for(Element tag : meaning_tags_soup){
                        String meaning = tag.text().trim();
                        HashMap<String, String> defMap = new HashMap<>();
                        String definition = meaning;
                        defMap.put(EXP_ELE[0], word);
                        defMap.put(EXP_ELE[1], reading);
                        defMap.put(EXP_ELE[2], mp3_url);
                        //defMap.put(EXP_ELE[3], meaning_tag);
                        defMap.put(EXP_ELE[3], definition);
                        String audioIndicator = "";
                        if(!mp3_url.isEmpty()){
                            audioIndicator = "<font color='#227D51' >"+AUDIO_TAG + "</font>";
                        }
                        String export_html = "<b>" + word + " </b>" +
                                "<font color = '#ba400d'>" + reading + "</font>" + "<br/>" + definition;
                        defList.add(new Definition(defMap, export_html));
                    }
                }
            }

            return defList;
        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


}

