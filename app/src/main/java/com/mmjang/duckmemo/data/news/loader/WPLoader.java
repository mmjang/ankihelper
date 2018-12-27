package com.mmjang.duckmemo.data.news.loader;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.news.NewsContent;
import com.mmjang.duckmemo.data.news.NewsEntry;
import com.mmjang.duckmemo.data.news.NewsLoader;
import com.mmjang.duckmemo.util.com.baidu.translate.demo.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WPLoader implements NewsLoader {
    public String mSourceName;
    public String mSourceUrl;
    public static final String POLITICS = "http://feeds.washingtonpost.com/rss/politics";
    public static final String OPNIONS = "http://feeds.washingtonpost.com/rss/opinions";
    public static final String NATIONAL = "http://feeds.washingtonpost.com/rss/national";
    public static final String WORLD = "http://feeds.washingtonpost.com/rss/world";
    public static final String BUSINESS = "http://feeds.washingtonpost.com/rss/business";
    public static final String LIFESTYLE = "http://feeds.washingtonpost.com/rss/lifestyle";
    private static final String TEMPLATE =
            "<html>\n" +
            "<head>\n" +
            "    <script src='rangy-core.js'></script>\n" +
            "    <script src='rangy-classapplier.js'></script>\n" +
            "    <script src='rangy-highlighter.js'></script>\n" +
            "    <script src='rangy-serializer.js'></script>\n" +
            "    <script src='sentence.js'></script>\n" +
                    "    <script src='bridge.js'></script>\n" +
                    "    <link rel=\"stylesheet\" charset=\"uft-8\" href=\"style.css\"/>\n" +
                    "    <link rel=\"stylesheet\" charset=\"uft-8\" href=\"site/washington_post/custom.css\"/>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"article\">\n" +
            "        <div id=\"title\">\n" +
            "            {{title}}\n" +
            "        </div>\n" +
            "    \n" +
            "        <div id=\"content\">\n" +
            "            {{content}}\n" +
            "        </div>\n" +
            "    </div>\n" +
                    "    <script src='Gesite/washington_post/custom.js'></script>\n" +
                    "</body>\n" +
            "</html>";

    public WPLoader(String url, String name){
        mSourceName = name;
        mSourceUrl = url;
    }

    public String getSourceName(){
        return mSourceName;
    }

    @Override
    public List<NewsEntry> getNewsMeta() {
        try {
            return getSectionEntryList(mSourceUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void getContent(NewsEntry newsEntry) throws IOException {
        Document doc = Jsoup.connect(newsEntry.getUrl())
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(10000)
                .get();
        String content = "";

        for(Element element : doc.select("article > p, article > .inline-photo, article span.author-timestamp")){
            content += element.toString();
        }

        String html = "";
        html = TEMPLATE.replace("{{title}}", newsEntry.getTitle())
                .replace("{{content}}", content);

        NewsContent newsContent = new NewsContent();
        newsContent.setContentHtml(html);
        newsContent.setHighlights("");
        MyApplication.getDaoSession().getNewsContentDao().insert(newsContent);
        newsEntry.setContent(newsContent);
    }

    List<NewsEntry> getSectionEntryList(String url) throws JSONException{
        String xml = HttpGet.get(url, null);
        if(xml == null){
            return null;
        }
        xml = xml.replaceAll("media:","media");
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        List<NewsEntry> newsEntries = new ArrayList<>();
        for(Element item : doc.select("item")){
            NewsEntry newsEntry = new NewsEntry();
            newsEntry.setTitle(item.select("title").get(0).text());
            String imageUrl = "";
            Elements eles = item.select("mediathumbnail");
            if(eles.size() > 0){
                newsEntry.setTitleImageUrl(eles.get(0).attr("url"));
            }
            newsEntry.setDescription(item.select("description").get(0).text());
            newsEntry.setUrl(item.select("link").get(0).text());
            newsEntry.setDate(item.select("pubDate").get(0).text());
            newsEntry.setSource(mSourceName);
            newsEntries.add(newsEntry);
        }
        return newsEntries;
    }

}
