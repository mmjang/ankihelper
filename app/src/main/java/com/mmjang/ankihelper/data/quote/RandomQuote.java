package com.mmjang.ankihelper.data.quote;

import com.mmjang.ankihelper.util.com.baidu.translate.demo.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RandomQuote {
    private static final String URL = "https://talaikis.com/api/quotes/random/";
    public static Quote fetch() throws JSONException {
        String doc = HttpGet.get(URL, null);
        JSONObject docJson = new JSONObject(doc);
        String quote = docJson.getString("quote");
        String author = docJson.getString("author");
        String cat = docJson.getString("cat");
        Quote q = new Quote();
        q.Quote = quote;
        q.Author = author;
        q.Caption = cat;
        return q;
    }
}
