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

public class ScientificAmericanLoader implements NewsLoader {
    public String mSourceName;
    public String mSourceUrl;
    public static final String SECTION_TECH = "https://www.scientificamerican.com/tech/";
    public static final String SECTION_HEALTH = "https://www.scientificamerican.com/health/";
    public static final String SECTION_MIND = "https://www.scientificamerican.com/mind/";
    public static final String SECTION_SCIENCES= "https://www.scientificamerican.com/the-sciences/";
    public static final String SECTION_SUSTAINABILITY = "https://www.scientificamerican.com/sustainability/";


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
                    "            <image class=\"img\" src=\"{{imageUrl}}\"></image>\n" +
                    "            <div class=\"caption\">{{imageCaption}}</div>\n" +
                    "        </div>\n" +
                    "    \n" +
                    "        <div id=\"content\">\n" +
                    "            {{content}}\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

    public ScientificAmericanLoader(String url, String name){
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
        catch (IOException e){
            e.printStackTrace();;
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
        Elements main_article = doc.select("#sa_body article div.mura-region-local");
        Elements image = doc.select("#image-1 picture img");
        if(image.size() > 0){
            imageUrl = image.attr("src");
        }
        Elements caption = doc.select("#image-1 figcaption");
        if(caption.size() > 0){
            imageCaption = caption.text();
        }

        for(Element element : main_article.select("p")) {
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

    List<NewsEntry> getSectionEntryList(String url) throws JSONException, IOException {
        Request request = new Request.Builder().url(mSourceUrl).build();
        String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
        Document doc = Jsoup.parse(rawhtml);
        Elements feeds2 = doc.select("#sa_body section.most-popular-outer div.most-popular__grid article");
        List<NewsEntry> newsEntries = new ArrayList<>();
        //start from the end, make the most important news on the top
        for(Element feed : feeds2){

            NewsEntry newsEntry = new NewsEntry();
            newsEntry.setUrl(feed.select("div.listing-wide__thumb a").attr("href"));
            newsEntry.setTitle(feed.select("div.listing-wide__inner h3 a").text());
            Elements imgElement = feed.select("div.listing-wide__thumb a picture");
            String imageUrl = "";
            if(imgElement.size()>0) {
                imageUrl = imgElement.select("img").attr("src");
            }
            imageUrl = imageUrl.replace(".jpg","-s400-c85.jpg");
            newsEntry.setTitleImageUrl(imageUrl);
            newsEntry.setDate(feed.select("div.listing-wide__inner div.t_meta").text());
            newsEntry.setDescription(feed.select("div.listing-wide__inner p").text());
            newsEntry.setSource(mSourceName);
            newsEntries.add(newsEntry);
        }

        return newsEntries;
    }

}
