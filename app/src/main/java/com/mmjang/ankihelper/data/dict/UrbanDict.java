package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;

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

    private static final String wordUrl = "https://www.urbandictionary.com/define.php?term=";
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
            Document doc = Jsoup.parse(rawhtml);
            ArrayList<Definition> defList = new ArrayList<>();
         //   String word = VocabCom.getSingleQueryResult(doc, "#content . h1", false);
         //   String yinbiao = VocabCom.getSingleQueryResult(doc, "div.hd_p1_1", false);
            Elements posList = doc.select("div.def-panel");
            for(Element pos : posList){
                String word = VocabCom.getSingleQueryResult(doc, ".def-header a.word", false);
         //       String posType = VocabCom.getSingleQueryResult(pos, "div.pos", false);
                Elements defs = pos.select("div.meaning");
                String def = defs.text();
                String example = pos.select("div.example").text();
                HashMap<String, String> defMap = new HashMap<>();
                //String definition = posType + " " + def;
                defMap.put(EXP_ELE[0], word);
                defMap.put(EXP_ELE[1], def);
                defMap.put(EXP_ELE[2], example);
                String html = "<div class=lwzj><danci>" + word +" </danci> " + "-" + "<span class=ys>" + def +"</span><br><font color=#4682B4><span class=yl>•" + example + "</span> </font></div>";
                defMap.put(EXP_ELE[3], html);
                defList.add(new Definition(defMap, html));
            }
            return defList;
        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return new UrbanAutoCompleteAdapter(context, layout);
    }


}
