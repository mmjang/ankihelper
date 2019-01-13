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

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class IdiomDict implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "短语词典";
    private static final String DICT_INTRO = "数据来自 freedictionary ";
    private static final String[] EXP_ELE = new String[] {"单词", "释义"};

    private static final String wordUrl = "https://idioms.thefreedictionary.com/";

    public IdiomDict(Context context) {

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
            Request request = new Request.Builder().url(wordUrl + key).build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);
            ArrayList<Definition> defList = new ArrayList<>();
            Elements posList = doc.select("#Definition section");
            for(Element pos : posList){
                String word = VocabCom.getSingleQueryResult(pos, "h2", false);
                String copyright = VocabCom.getSingleQueryResult(pos, "div.brand_copy", false);
                Elements defs = pos.select(".ds-list");
                for (Element def : defs){
                    //TODO: .ownText() is executable and recall the right definition？？？
                    String definition = def.text();
                    String illus = def.select("span.illustration").text();
                    definition = definition.replace(illus, "<i><font color=#808080>" + illus + "</font></i>");
                    HashMap<String, String> defMap = new HashMap<>();

                    defMap.put(EXP_ELE[0], word);
                    defMap.put(EXP_ELE[1], definition);
                    //TODO: setting proper font size for the copyright. The main purpose of
                    //setting copyright is to getting the reference.
                    String html = "<div class='idiom'><danci>" + word +" </danci> " + "-"+ copyright + "-" + "<span class=ys>" + definition +"</span></div>";
                    defList.add(new Definition(defMap, html));


                }
               
            }
            return defList;
        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


}
