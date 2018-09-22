package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.jsoup.helper.HttpConnection.DEFAULT_UA;

/**
 * Created by liao on 2017/4/28.
 */

public class Handian implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "汉典";
    private static final String DICT_INTRO = "数据来自 www.zdic.net";
    private static final String[] EXP_ELE = new String[] {"字词", "释义"};

    private static final String wordUrl = "http://www.zdic.net/search/?c=3&q=";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";
    private static final String COOKIE = "_UZT_USER_SET_106_0_DEFAULT=2%7C94203fe9fb690808b7ef29aff3834b76; HJ_UID=92482875-6c87-23dc-b654-a051128fe960; TRACKSITEMAP=3%2C11%2C22%2C63%2C; _REF=http%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3D4EF7BW5aw2AUMr14xKpyBxPDcH6Ah3LnigHy27aeIww8BuyV8j6m02hc7eUKObbxwDxtpIE4vcOUheux2UYif_%26wd%3D%26eqid%3Df6c074f90001a098000000065a3e25dd; HJ_CMATCH=1; HJ_SID=47f67f36-d438-9a1f-782b-79a0eb723d97; HJ_CST=0; HJ_SSID_3=072d9dcf-6ba0-55a0-a662-22a1d555afb2; HJ_CSST_3=0; _SREF_3=";
    public Handian(Context context) {

    }

    public Handian(){

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
                    .userAgent(DEFAULT_UA)
                    .timeout(5000)
                    .get();
            String html = doc.toString();
            Elements entrys = doc.select("div.cdnr, div.tagContent");
            ArrayList<Definition> defList = new ArrayList<>();
            if (entrys.size() > 0) {
                    Element ele = entrys.get(0);
                    String word = key;
                    String meaning = ele.toString();
                    meaning = meaning.replaceAll("<img src=\"/", "<img src=\"http://www.zdic.net/");
                    meaning = meaning.replaceAll("&amp;","&");
                    HashMap<String, String> defMap = new HashMap<>();
                    String definition = meaning;
                    defMap.put(EXP_ELE[0], word);
                    defMap.put(EXP_ELE[1], definition);
                    defList.add(new Definition(defMap, definition));
            }
            return defList;
        } catch (IOException ioe) {
            Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


    public static void main(String[] args){
        IDictionary dic = new Handian();
        //dic.wordLookup(UrlEscapers.urlFragmentEscaper().escape("娘"));
        dic.wordLookup("娘");
    }
}

