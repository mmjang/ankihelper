package com.mmjang.ankihelper.data.dict;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by liao on 2017/7/30.
 */

public class YoudaoOnline {
    private static final String BASE_URL = "http://dict.youdao.com/fsearch?client=deskdict&keyfrom=chrome.extension&pos=-1&doctype=xml&dogVersion=1.0&vendor=unknown&appVer=3.1.17.4208&le=eng&q=%s";
    static public YoudaoResult getDefinition(String key) throws IOException{
            Document doc = Jsoup.connect(String.format(BASE_URL, key.trim()))
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(2000)
                    .parser(Parser.xmlParser())
                    .get();
            //doc.toString();
            String phonetic = getSingleQueryResult(doc, "phonetic-symbol");
            String returnPhrase = getSingleQueryResult(doc, "return-phrase");
            List<String> translation = new ArrayList<String>();
            for(Element e : doc.select("translation > content")){
                translation.add(e.text());
            }

            Map<String, List<String>> webTranslation = new LinkedHashMap<>();
            for(Element web : doc.select("web-translation")){
                String keyString = getSingleQueryResult(web, "key");
                List<String> values = new ArrayList<>();
                for(Element value : web.select("trans > value")){
                    String valueString = value.text().trim();
                    values.add(valueString);
                }
                webTranslation.put(keyString, values);
            }
        YoudaoResult youdaoResult = new YoudaoResult();
        youdaoResult.phonetic = phonetic;
        youdaoResult.returnPhrase = returnPhrase;
        youdaoResult.translation = translation;
        youdaoResult.webTranslation = webTranslation;
        return  youdaoResult;
    }

    private static String getSingleQueryResult(Document soup, String query){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            return re.get(0).text();
        }else{
            return "";
        }
    }

    private static String getSingleQueryResult(Element soup, String query){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            return re.get(0).text();
        }else{
            return "";
        }
    }
}
