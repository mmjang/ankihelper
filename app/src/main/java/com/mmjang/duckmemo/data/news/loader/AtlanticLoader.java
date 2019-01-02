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

public class AtlanticLoader implements NewsLoader {
    public String mSourceName;
    public String mSourceUrl;
    public static final String SECTION_POLITICS = "https://www.theatlantic.com/politics/";
    public static final String SECTION_ENTERTAINMENT = "https://www.theatlantic.com/entertainment/";
    public static final String SECTION_FAMILY = "https://www.theatlantic.com/family/";
    public static final String SECTION_BOOKS = "https://www.theatlantic.com/books/";
    public static final String SECTION_EDUCATION = "https://www.theatlantic.com/education/";
    public static final String SECTION_TECH = "https://www.theatlantic.com/technology/";
    public static final String SECTION_IDEAS = "https://www.theatlantic.com/ideas/";

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
                    "            <img class=\"img\" src=\"{{imageUrl}}\"></image>\n" +
                    "            <div class=\"caption\">{{imageCaption}}</div>\n" +
                    "        </div>\n" +
                    "    \n" +
                    "        <div id=\"content\">\n" +
                    "            {{content}}\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

    public AtlanticLoader(String url, String name){
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
        Elements main_article = doc.select("body > main > article .l-article__container");
        Elements image = main_article.select("div.blah > div.l-article__container__container >figure> picture > img");
        if(image.size() > 0){
            imageUrl = image.attr("src");
        }
        Elements caption = doc.select("div.blah > div.l-article__container__container >figure > figcaption");
        if(caption.size() > 0){
            imageCaption = caption.select("span").get(0).text();
        }

        for(Element container : main_article.select("div.blah")){
            for (Element element : container.select("div > section > p"))
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

    List<NewsEntry> getSectionEntryList(String url) throws JSONException, IOException{
        Request request = new Request.Builder().url(url).build();
        String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
        Document doc = Jsoup.parse(rawhtml);
        Elements feeds = doc.select("li.article.blog-article");
        List<NewsEntry> newsEntries = new ArrayList<>();
        //start from the end, make the most important news on the top
        for(Element feed:feeds ){

            NewsEntry newsEntry = new NewsEntry();
            newsEntry.setUrl("");
            for(Element a : feed.select("a[href]")){
                if(a.attr("href").contains("archive")){
                    newsEntry.setUrl("https://www.theatlantic.com" + a.attr("href"));
                    break;
                }
            }
            //newsEntry.setUrl( + feed.select(".hed a[href]").attr("href"));
            newsEntry.setTitle(feed.select("h2").text());
            String imageUrl = "";
            if(feed.select("a > figure >img").hasAttr("data-src")) {
                imageUrl = feed.select("a > figure >img").attr("data-src");
            }
            //imageUrl = imageUrl.replace(".jpg",".jpg");
            newsEntry.setTitleImageUrl(imageUrl);
            newsEntry.setDate(feed.select("li.date").text());
            newsEntry.setDescription(feed.select("li > p").text());
            newsEntry.setSource(mSourceName);
            newsEntries.add(newsEntry);
        }

        return newsEntries;
    }

}
