package com.mmjang.duckmemo.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.duckmemo.MyApplication;

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

public class JiSho implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "Japenese (jisho.org)";
    private static final String DICT_INTRO = "数据来自 jisho.org. audio项是形如 [sound:xxx.mp3] 的发音，使用之前默认模版的用户需要编辑模版并在里面加入{{audio}}";
    private static final String[] EXP_ELE = new String[] {"word", "reading", "audio", "definition"};

    private static final String wordUrl = "http://jisho.org/search/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public JiSho(Context context) {

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
            Elements entrys = doc.select("div.concept_light");
            ArrayList<Definition> defList = new ArrayList<>();
            if (entrys.size() > 0) {
                for (Element ele : entrys){
                    String furigana = "";
                    String writing = "";
                    String mp3_url = "";
                    //String meaning_tag = "";
                    //String definition = "";
                    Elements furigana_soup = ele.select("span.furigana");
                    if(furigana_soup.size() > 0){
                        furigana = furigana_soup.get(0).text().trim();
                    }

                    Elements writing_soup = ele.select("span.text");
                    if(writing_soup.size() > 0){
                        writing = writing_soup.get(0).text().trim();
                    }

                    Elements audio_soup = ele.select("audio > source");
                    if(audio_soup.size() > 0){
                        mp3_url = "[sound:" +audio_soup.get(0).attr("src") + "]";
                    }

                    Elements meaning_tags_soup = ele.select("div.meaning-tags");
                    for(Element tag : meaning_tags_soup){
                        String meaning_tag = tag.text().trim();
                        Element word_def_soup = tag.nextElementSibling();
                        if(word_def_soup != null){
                            for(Element defSoup : word_def_soup.select("div.meaning-definition > span.meaning-meaning")){
                                HashMap<String, String> defMap = new HashMap<>();
                                String definition = "<i><font color='grey'>" + meaning_tag + "</font></i> " + defSoup.text().trim();
                                defMap.put(EXP_ELE[0], writing);
                                defMap.put(EXP_ELE[1], furigana);
                                defMap.put(EXP_ELE[2], mp3_url);
                                //defMap.put(EXP_ELE[3], meaning_tag);
                                defMap.put(EXP_ELE[3], definition);
                                String audioIndicator = "";
                                if(!mp3_url.isEmpty()){
                                    audioIndicator = "<font color='#227D51' >"+AUDIO_TAG + "</font>";
                                }
                                String export_html = "<b>" + writing + "</b> <font color='grey'>" + furigana + "</font> " + audioIndicator + "<br/>" + definition;
                                defList.add(new Definition(defMap, export_html));
                            }
                        }
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

