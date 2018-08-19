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

public class BingOxford implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "必应牛津英汉双解";
    private static final String DICT_INTRO = "数据来自 Bing 在线词典";
    private static final String[] EXP_ELE = new String[] {"单词", "音标", "释义"};

    private static final String wordUrl = "https://cn.bing.com/dict/search?q=";
    //private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public BingOxford(Context context) {

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
            ArrayList<Definition> defList = new ArrayList<>();
            String word = VocabCom.getSingleQueryResult(doc, ".hd_div h1", false);
            String yinbiao = VocabCom.getSingleQueryResult(doc, "div.hd_p1_1", false);
            Elements posList = doc.select("div.li_pos");
            for(Element pos : posList){
                String posType = VocabCom.getSingleQueryResult(pos, "div.pos", false);
                Elements defs = pos.select("div.def_pa");
                for(Element ele : defs){
                    String def = ele.text();
                    HashMap<String, String> defMap = new HashMap<>();
                    String definition = posType + " " + def;
                    defMap.put(EXP_ELE[0], word);
                    defMap.put(EXP_ELE[1], yinbiao);
                    defMap.put(EXP_ELE[2], definition);
                    String html = "<b>" + posType + " " + "</b>" + def;
                    defList.add(new Definition(defMap, html));
                }
            }

            String qDef = VocabCom.getSingleQueryResult(doc, "div.qdef > ul", false);
            HashMap<String, String> defMap = new HashMap<>();
            defMap.put(EXP_ELE[0], word);
            defMap.put(EXP_ELE[1], yinbiao);
            defMap.put(EXP_ELE[2], qDef);
            defList.add(new Definition(defMap, qDef));

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

