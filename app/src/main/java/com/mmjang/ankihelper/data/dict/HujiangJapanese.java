package com.mmjang.ankihelper.data.dict;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.common.net.UrlEscapers;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static org.jsoup.helper.HttpConnection.DEFAULT_UA;

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
    private static final String COOKIE = "_UZT_USER_SET_106_0_DEFAULT=2%7C94203fe9fb690808b7ef29aff3834b76; HJ_UID=92482875-6c87-23dc-b654-a051128fe960; TRACKSITEMAP=3%2C11%2C22%2C63%2C; _REF=http%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3D4EF7BW5aw2AUMr14xKpyBxPDcH6Ah3LnigHy27aeIww8BuyV8j6m02hc7eUKObbxwDxtpIE4vcOUheux2UYif_%26wd%3D%26eqid%3Df6c074f90001a098000000065a3e25dd; HJ_CMATCH=1; HJ_SID=47f67f36-d438-9a1f-782b-79a0eb723d97; HJ_CST=0; HJ_SSID_3=072d9dcf-6ba0-55a0-a662-22a1d555afb2; HJ_CSST_3=0; _SREF_3=";
    public HujiangJapanese(Context context) {

    }

    public HujiangJapanese(){

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
//            Connection.Response res = Jsoup.connect("https://www.hjdict.com")
//                    .userAgent(DEFAULT_UA)
//                    .method(Connection.Method.GET)
//                    .execute();
//            Map<String, String> cookies = res.cookies();

//            Document doc = Jsoup.connect(wordUrl + key)
//                    .userAgent(DEFAULT_UA)
//                    .header("cookie", COOKIE)
//                    //.cookies(cookies)
//                    .timeout(5000)
//                    .get();
//
            Request request = new Request.Builder().url(wordUrl + key)
                    .addHeader("User-Agent", Constant.UA)
                    .addHeader("Cookie",COOKIE)
                    .build();
            String html = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(html);
            //String html = doc.toString();
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
            Log.d("time out", Log.getStackTraceString(ioe));
            //Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


    public static void main(String[] args){
        IDictionary dic = new HujiangJapanese();
        //dic.wordLookup(UrlEscapers.urlFragmentEscaper().escape("娘"));
        dic.wordLookup("娘");
    }
}

