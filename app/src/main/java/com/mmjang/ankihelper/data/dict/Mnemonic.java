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

public class Mnemonic implements IDictionary {

    //private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "Mnemonic助记词典";
    private static final String DICT_INTRO = "全英文助记，慎入。";
    private static final String[] EXP_ELE = new String[] {"助记"};
    private static final String wordUrl = "http://www.mnemonicdictionary.com/?word=";
    //private static final String mp3Url = "https://audio.vocab.com/1.0/us/";

    public Mnemonic(Context context){
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
            List<Definition> definitionList = new ArrayList<>();
            for(Element memo : doc.select(".span9")){
                HashMap<String, String> eleMap = new HashMap<>();
                eleMap.put(EXP_ELE[0], memo.text());
                definitionList.add(new Definition(eleMap, memo.text()));
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

    private static String getSingleQueryResult(Document soup, String query, boolean toString){
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

    private static String getSingleQueryResult(Element soup, String query, boolean toString){
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

