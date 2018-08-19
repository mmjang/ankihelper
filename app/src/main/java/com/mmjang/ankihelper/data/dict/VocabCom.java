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

public class VocabCom implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "Vocabulary.com";
    private static final String DICT_INTRO = "";
    private static final String[] EXP_ELE = new String[] {"单词", "发音", "释义"};

    private static final String wordUrl = "http://app.vocabulary.com/app/1.0/dictionary/search?word=";
    private static final String mp3Url = "https://audio.vocab.com/1.0/us/";

    public VocabCom(Context context){

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
            String headWord = getSingleQueryResult(doc, "h1.dynamictext", false);
            String defShort = getSingleQueryResult(doc, "p.short", true).replace("<i>","<b>").replace("</i>","</b>");
            String defLong = getSingleQueryResult(doc, "p.long", true).replace("<i>","<b>").replace("</i>","</b>");
            Elements mp3Soup = doc.select("a.audio");
            String mp3Id = "";
            if(mp3Soup.size() > 0){
                mp3Id = mp3Soup.get(0).attr("data-audio");
            }

            List<Definition> definitionList = new ArrayList<>();

            if(headWord.isEmpty()){
                return definitionList;
            }
            if(!defShort.isEmpty()){
                HashMap<String, String> eleMap = new HashMap<>();
                eleMap.put(EXP_ELE[0], headWord);
                eleMap.put(EXP_ELE[1], getMp3Url(mp3Id));
                eleMap.put(EXP_ELE[2], defShort + defLong);
                definitionList.add(new Definition(eleMap, defShort + defLong));
            }

            for(Element def : doc.select("h3.definition")){
                HashMap<String, String> eleMap = new HashMap<>();
                String defText = getDefHtml(def);
                eleMap.put(EXP_ELE[0], headWord);
                eleMap.put(EXP_ELE[1], getMp3Url(mp3Id));
                eleMap.put(EXP_ELE[2], defText);
                definitionList.add(new Definition(eleMap, defText));
            }

            return definitionList;

        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
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

    private String getMp3Url(String id){
        return "[sound:" + mp3Url + id + ".mp3]";
    }

    private String getDefHtml(Element def){
        String sense = def.toString().replaceAll("<h3.+?>","<div>").replace("</h3>","</div>").replaceAll("<a.+?>","<b>").replace("</a>","</b>");
        //String defString = def.child(1).text().trim();
        return sense;
    }
}

