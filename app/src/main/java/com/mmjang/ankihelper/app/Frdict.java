package com.mmjang.ankihelper.app;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

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

public class Frdict implements IDictionary{

    private static final String DICT_NAME = "欧路法语在线";
    private static final String DICT_INTRO = "数据来自 frdic.com/mdicts/fr/";
    private static final String[] EXP_ELE = new String[] {"单词", "音标","法汉词典","法法词典","法英词典"};

    private static final String wordUrl = "https://www.frdic.com/mdicts/fr/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public Frdict(Context context){

    }

    public String getDictionaryName(){
        return DICT_NAME;
    }

    public String getIntroduction(){
        return DICT_INTRO;
    }

    public String[] getExportElementsList(){
        return EXP_ELE;
    }

    public List<Definition> wordLookup(String key){
        try {
            String headWrod = "";
            String phonetics = "";
            String frCnDef = "";
            String frFrDef = "";
            String frEnDef = "";

            Document doc = Jsoup.connect(wordUrl + key)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements word = doc.select("h2.word > span.word");
            if(word.size() == 1){
                headWrod = word.get(0).text().trim();
            }
            Elements phonitic = doc.select("span.Phonitic");
            if(phonitic.size() == 1){
                phonetics = phonitic.get(0).text().trim();
            }

            Elements cnDiv = doc.select("#FCChild");
            if(cnDiv.size() == 1){
                frCnDef = cnDiv.get(0).html().trim();
            }

            Elements frDiv = doc.select("#FFChild");
            if(frDiv.size() == 1){
                frFrDef = frDiv.get(0).html().trim();
            }

            Elements enDiv = doc.select("#FEChild");
            if(enDiv.size() == 1){
                frEnDef = enDiv.get(0).html().trim();
            }

            HashMap<String, String> expele = new HashMap<>();
            expele.put(EXP_ELE[0], headWrod);
            expele.put(EXP_ELE[1], phonetics);
            expele.put(EXP_ELE[2], frCnDef);
            expele.put(EXP_ELE[3], frFrDef);
            expele.put(EXP_ELE[4], frEnDef);

            String exportedHtml = headWrod + "</br>" + phonetics + "</br>" + frCnDef + "</br>" + frEnDef;

            Definition d = new Definition(expele, exportedHtml);

            ArrayList<Definition> defList = new ArrayList<>();
            defList.add(d);
            return defList;
        }
        catch(IOException ioe){
            //Log.d("time out", Log.getStackTraceString(ioe));
            Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout){
        return null;
    }


}
