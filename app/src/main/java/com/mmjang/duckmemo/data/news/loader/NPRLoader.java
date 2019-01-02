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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class NPRLoader implements NewsLoader {
    public String mSourceName;
    public String mSourceUrl;
    public static final String SECTION_NATIONAL = "https://www.npr.org/feeds/1003/feed.json";
    public static final String SECTION_WORLD = "https://www.npr.org/feeds/1004/feed.json";
    public static final String SECTION_SCIENCE = "https://www.npr.org/feeds/1007/feed.json";
    public static final String SECTION_BUSINESS = "https://www.npr.org/feeds/1006/feed.json";
    public static final String SECTION_ART = "https://www.npr.org/feeds/1008/feed.json";
    public static final String SECTION_TECH = "https://www.npr.org/feeds/1019/feed.json";
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
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"article\">\n" +
            "        <div id=\"title\">\n" +
            "            {{title}}\n" +
            "        </div>\n" +
            "\n" +
            "        <div id=\"image\">\n" +
            "            <img class=\"img\" src=\"{{imageUrl}}\"></img>\n" +
            "            <div class=\"caption\">{{imageCaption}}</div>\n" +
            "        </div>\n" +
            "    \n" +
            "        <div id=\"content\">\n" +
            "            {{content}}\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";

    public NPRLoader(String url, String name){
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
        Request request = new Request.Builder().url(newsEntry.getUrl()).build();
        String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
        Document doc = Jsoup.parse(rawhtml);
        String imageUrl = "";
        String imageCaption = "";
        String content = "";
        Elements image = doc.select("#storytext div.imagewrap img");
        if(image.size() > 0){
            imageUrl = image.attr("src");
        }
        Elements caption = doc.select("#storytext div.credit-caption");
        if(caption.size() > 0){
            imageCaption = caption.select("div.caption-wrap div.caption").get(0).text();
        }

        for(Element element : doc.select("#storytext > p")){
            content += element.toString();
        }

        String html = "";
        html = TEMPLATE.replace("{{title}}", newsEntry.getTitle())
                .replace("{{imageUrl}}", imageUrl)
                .replace("{{imageCaption}}", imageCaption)
                .replace("{{content}}", content);

        NewsContent newsContent = new NewsContent();
        newsContent.setContentHtml(html);
        newsContent.setHighlights("");
        MyApplication.getDaoSession().getNewsContentDao().insert(newsContent);
        newsEntry.setContent(newsContent);
    }

    List<NewsEntry> getSectionEntryList(String url) throws JSONException {
        Request request = new Request.Builder().url(url).build();
        String doc = null;
        try {
            doc = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if(doc == null){
            return null;
        }
        JSONObject docJson = new JSONObject(doc);
        JSONArray items = docJson.getJSONArray("items");

        List<NewsEntry> newsEntries = new ArrayList<>();
        //start from the end, make the most important news on the top
        for(int i = items.length() - 1; i >= 0; i --){
            JSONObject item = items.getJSONObject(i);
            NewsEntry newsEntry = new NewsEntry();
            newsEntry.setUrl(item.getString("url"));
            newsEntry.setTitle(item.getString("title"));
            String imageUrl = "";
            if(item.has("image")) {
                imageUrl = item.getString("image");
            }
            //imageUrl = imageUrl.replace(".jpg","-s400-c85.jpg");
            newsEntry.setTitleImageUrl(imageUrl);
            newsEntry.setDate(item.getString("date_published"));
            newsEntry.setDescription(item.getString("summary"));
            newsEntry.setSource(mSourceName);
            newsEntries.add(newsEntry);
        }

        return newsEntries;
    }

}
