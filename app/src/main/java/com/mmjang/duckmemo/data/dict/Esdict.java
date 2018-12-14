package com.mmjang.duckmemo.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.duckmemo.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liao on 2017/4/28.
 */

public class Esdict implements IDictionary {

    private static final String DICT_NAME = "欧路西班牙语在线";
    private static final String DICT_INTRO = "测试~~";
    private static final String[] EXP_ELE = new String[]{"单词", "音标", "西汉词典", "西英词典"};

    private static final String wordUrl = "https://www.esdict.cn/mdicts/es/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public Esdict(Context context) {

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
            String headWrod = "";
            String phonetics = "";
            String esCnDef = "";
            String esEnDef = "";

            Document doc = Jsoup.connect(wordUrl + key)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements word = doc.select("h2.word > span.word");
            if (word.size() == 1) {
                headWrod = word.get(0).text().trim();
            }
            Elements phonitic = doc.select("span.Phonitic");
            if (phonitic.size() == 1) {
                phonetics = phonitic.get(0).text().trim();
            }

            Elements cnDiv = doc.select("#FCChild");
            if (cnDiv.size() == 1) {
                esCnDef = cnDiv.get(0).html().trim();
            }

            Elements enDiv = doc.select("#FEChild");
            if (enDiv.size() == 1) {
                esEnDef = enDiv.get(0).html().trim();
            }

            HashMap<String, String> expele = new HashMap<>();
            expele.put(EXP_ELE[0], headWrod);
            expele.put(EXP_ELE[1], phonetics);
            expele.put(EXP_ELE[2], esCnDef);
            expele.put(EXP_ELE[3], esEnDef);

            String exportedHtml = headWrod + "</br>" + phonetics + "</br>" + esCnDef + "</br>" + esEnDef;

            Definition d = new Definition(expele, exportedHtml);

            ArrayList<Definition> defList = new ArrayList<>();
            defList.add(d);
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
