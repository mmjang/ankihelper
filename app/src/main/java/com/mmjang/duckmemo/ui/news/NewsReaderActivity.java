package com.mmjang.duckmemo.ui.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.news.NewsEntry;
import com.mmjang.duckmemo.data.news.NewsEntryDao;
import com.mmjang.duckmemo.data.news.NewsEntryPosition;
import com.mmjang.duckmemo.data.news.NewsLoader;
import com.mmjang.duckmemo.data.news.NewsLoaderUtils;
import com.mmjang.duckmemo.data.news.NewsUtils;
import com.mmjang.duckmemo.ui.popup.PopupActivity;
import com.mmjang.duckmemo.util.Constant;

import java.io.IOException;
import java.util.List;

public class NewsReaderActivity extends AppCompatActivity {

    WebView mWebView;
    ProgressBar mProgressBar;
    NewsEntryDao newsEntryDao;
    NewsEntry mNewsEntry;
    int mIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        mWebView = findViewById(R.id.news_webview);
        mProgressBar = findViewById(R.id.news_webview_progress);
        newsEntryDao = MyApplication.getDaoSession().getNewsEntryDao();
        setUpWebView();
        getSupportActionBar().setTitle("Loading...");
        Intent intent = getIntent();
        //must have
        long id = intent.getLongExtra(Constant.INTENT_DUCKMEMO_NEWS_ID, 0);
        //if index >=0, scroll
        mIndex = intent.getIntExtra(Constant.INTENT_DUCKMEMO_NEWS_POSITION_INDEX, -1);
        loadNews(id);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //must have
        long id = intent.getLongExtra(Constant.INTENT_DUCKMEMO_NEWS_ID, 0);
        //if index >=0, scroll
        mIndex = intent.getIntExtra(Constant.INTENT_DUCKMEMO_NEWS_POSITION_INDEX, -1);
        loadNews(id);

        if(mNewsEntry != null && mNewsEntry.getId() == id && mIndex > 0)//the target article already opened
        {
            mWebView.evaluateJavascript(String.format("jumpToSentence(%s)", mIndex), null);
        }

        if(mNewsEntry == null || mNewsEntry.getId() != id){
            loadNews(id);
        }
    }

    private void setUpWebView() {

        WebView.setWebContentsDebuggingEnabled(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(
                new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        restoreHighlightAndPosition();
                        //setFont();
                    }
                }
        );
        mWebView.addJavascriptInterface(this, "reader");
    }

    private void restoreHighlightAndPosition() {
        if(mNewsEntry != null && mNewsEntry.getContent() != null){
            String hightLights = mNewsEntry.getContent().getHighlights();
            if(hightLights != null && !hightLights.isEmpty()) {
                mWebView.evaluateJavascript(
                        String.format("onRestoreHighlights('%s')", hightLights),
                        null);
                if(mIndex > 0) {
                    mWebView.evaluateJavascript(String.format("jumpToSentence(%s)", mIndex), null);
                }
                mIndex = -1;
            }
        }
    }

    void loadNews(long id){
        mProgressBar.setVisibility(View.VISIBLE);
        @SuppressLint("StaticFieldLeak") AsyncTask<Long, Void, NewsEntry> asyncTask = new AsyncTask<Long, Void, NewsEntry>() {
            @Override
            protected NewsEntry doInBackground(Long... ids) {
                long id = ids[0];
                List<NewsEntry> result = newsEntryDao.queryBuilder().where(NewsEntryDao.Properties.Id.eq(id)).list();
                if(result.size() == 0){
                    return null;
                }else{
                    mNewsEntry = result.get(0);
                    NewsLoader newsLoader = NewsLoaderUtils.getLoaderByName(mNewsEntry.getSource());
                    if(newsLoader == null){
                        return null;
                    }
                    if(mNewsEntry.getContent() == null){
                        try {
                            newsLoader.getContent(mNewsEntry);
                            newsEntryDao.update(mNewsEntry);
                            return mNewsEntry;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }else{
                        return mNewsEntry;
                    }
                }
            }

            @Override
            protected void onPostExecute(NewsEntry newsEntry) {
                mProgressBar.setVisibility(View.GONE);
                if(newsEntry == null){
                    Toast.makeText(NewsReaderActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }else{
                    mWebView.loadDataWithBaseURL("file:///android_asset/news_reader/",
                            newsEntry.getContent().getContentHtml(), "text/html", "utf-8", null);
                    //set read time
                    newsEntry.setLastSeenTime(System.currentTimeMillis());
                    newsEntryDao.update(newsEntry);
                    getSupportActionBar().setTitle(newsEntry.getSource());
                }
            }
        };
        asyncTask.execute(id);
    }

    @JavascriptInterface
    public void invokePopup(String sentence, String word, int sentenceIndex){
        NewsEntryPosition newsEntryPosition = new NewsEntryPosition();
        newsEntryPosition.setSentenceIndex(sentenceIndex);
        newsEntryPosition.setNewsEntry(mNewsEntry);
        long id = MyApplication.getDaoSession().getNewsEntryPositionDao().insert(newsEntryPosition);
        Intent intent = new Intent(this, PopupActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sentence);
        intent.putExtra(Constant.INTENT_DUCKMEMO_NEWS_ENTRY_POSITION_ID, id);
        if(!word.trim().isEmpty()) {
            intent.putExtra(Constant.INTENT_DUCKMEMO_TARGET_WORD, word);
        }
        startActivity(intent);
    }

    @JavascriptInterface
    public void onSaveHightlights(String serializedHighlights){
        Toast.makeText(this, "hightlights saved", Toast.LENGTH_SHORT).show();
        if(mNewsEntry!=null && mNewsEntry.getContent()!=null){
            mNewsEntry.getContent().setHighlights(serializedHighlights);
            MyApplication.getDaoSession().getNewsContentDao().update(mNewsEntry.getContent());
        }
    }
}
