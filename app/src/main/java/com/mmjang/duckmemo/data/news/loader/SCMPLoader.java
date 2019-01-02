package com.mmjang.duckmemo.data.news.loader;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.news.NewsContent;
import com.mmjang.duckmemo.data.news.NewsEntry;
import com.mmjang.duckmemo.data.news.NewsLoader;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class SCMPLoader implements NewsLoader {
    public String mSourceName;
    public String mSourceUrl;
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    public static final String SECTION_CHINA = "https://www.scmp.com/rss/4/feed";
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
                    "    <link rel=\"stylesheet\" charset=\"uft-8\" href=\"site/scmp/custom.css\"/>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"article\">\n" +
            "        <div id=\"title\">\n" +
            "            {{title}}\n" +
            "        </div>\n" +
            "    \n" +
            "        <div id=\"content\">\n" +
                         "<p>{{pubdate}}</p>" +
                         "<img src='{{imgurl}}'/>" +
            "            {{content}}\n" +
            "        </div>\n" +
            "    </div>\n" +
                    "    <script src='site/scmp/custom.js'></script>\n" +
                    "</body>\n" +
            "</html>";

    public SCMPLoader(String url, String name){
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
        Request request = new Request.Builder().url(newsEntry.getUrl())
                .header("User-Agent", UA)
                .build();
        String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
        Document doc = Jsoup.parse(rawhtml);
        String content = "";
        for(Element element : doc.select(".col-xs-9.col-lg-9.pfcng-col.pfcng-col-2 .pane-content > p")){
            content += element.toString();
        }

        String html = "";
        html = TEMPLATE.replace("{{title}}", newsEntry.getTitle())
                .replace("{{content}}", content)
                .replace("{{imgurl}}", newsEntry.getTitleImageUrl())
                .replace("{{pubdate}}", newsEntry.getDate());

        NewsContent newsContent = new NewsContent();
        newsContent.setContentHtml(html);
        newsContent.setHighlights("");
        MyApplication.getDaoSession().getNewsContentDao().insert(newsContent);
        newsEntry.setContent(newsContent);
    }

    List<NewsEntry> getSectionEntryList(String url) throws JSONException{
        Request request = new Request.Builder().url(url)
                .header("User-Agent", UA)
                .build();
        String xml = null;
        try {
            xml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        xml = xml.replaceAll("media:","media");
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        List<NewsEntry> newsEntries = new ArrayList<>();
        Elements items = doc.select("item");
        for(int i = items.size() - 1; i >= 0; i --){
            Element item = items.get(i);
            NewsEntry newsEntry = new NewsEntry();
            newsEntry.setTitle(item.select("title").get(0).text());
            String imageUrl = "";
            Elements eles = item.select("mediacontent");
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
