package com.mmjang.duckmemo.ui.news;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.Settings;
import com.mmjang.duckmemo.data.news.NewsEntry;
import com.mmjang.duckmemo.data.news.NewsEntryDao;
import com.mmjang.duckmemo.data.news.NewsEntryPosition;
import com.mmjang.duckmemo.data.news.NewsLoader;
import com.mmjang.duckmemo.data.news.NewsLoaderUtils;
import com.mmjang.duckmemo.data.news.NewsUtils;
import com.mmjang.duckmemo.ui.popup.PopupActivity;
import com.mmjang.duckmemo.util.Constant;
import com.mmjang.duckmemo.util.Utils;

import org.jsoup.select.Evaluator;

import java.io.IOException;
import java.util.List;

public class NewsReaderActivity extends AppCompatActivity {

    WebView mWebView;
    ProgressBar mProgressBar;
    NewsEntryDao newsEntryDao;
    NewsEntry mNewsEntry;
    int mIndex = -1;
    Settings mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        mSettings = Settings.getInstance(this);
        mWebView = findViewById(R.id.news_webview);
        mProgressBar = findViewById(R.id.news_webview_progress);
        newsEntryDao = MyApplication.getDaoSession().getNewsEntryDao();
        setUpWebView();
        getSupportActionBar().setTitle("Loading...");
        setBackground(CSSUtils.getBackgroundColorList()[mSettings.getReaderBackgroundIndex()], false);
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
                        mWebView.evaluateJavascript(String.format("setArticleFont('%s')", mSettings.getReaderFontClass()), null);
                        setBackground(CSSUtils.getBackgroundColorList()[mSettings.getReaderBackgroundIndex()], true);
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
                    getSupportActionBar().setSubtitle(newsEntry.getTitle());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.activity_news_reader_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_font_settings:
                showFontMenu();
                break;
            case R.id.menu_open_with_browser:
                openLinkWithBrowser();
                break;
        }
        return true;
    }

    private void openLinkWithBrowser() {
        if(mNewsEntry != null){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mNewsEntry.getUrl()));
            startActivity(intent);
        }
    }

    private void showFontMenu() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_font_setting, null);
        dialogBuilder.setView(dialogView);
        final Spinner fontFamilySpinner = (Spinner) dialogView.findViewById(R.id.font_family_spinner);
        final Spinner fontSizeSpinner = (Spinner) dialogView.findViewById(R.id.font_size_spinner);
        final RadioGroup radioGroup = dialogView.findViewById(R.id.radiogroup_reader_background);
        fontFamilySpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[] {"Serif", "Sans Serif", "Georgia", "Caecilia", "FaricyNew", "Theinhardt"}));
        fontSizeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[] {"xSmall", "Small", "Medium", "Large", "xLarge"}
                ));
        int[] indexs = CSSUtils.getIndexFromClass(mSettings.getReaderFontClass());
        fontFamilySpinner.setSelection(indexs[0]);
        fontSizeSpinner.setSelection(indexs[1]);
        dialogBuilder.setTitle("Font Settings");
        fontFamilySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setFontByIndex(position, fontSizeSpinner.getSelectedItemPosition());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        fontSizeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setFontByIndex(fontFamilySpinner.getSelectedItemPosition(), position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        final int colorIndex = mSettings.getReaderBackgroundIndex();
        final String[] colorList = CSSUtils.getBackgroundColorList();
        for(int i = 0; i < radioGroup.getChildCount(); i ++){
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setBackgroundColor(Color.parseColor(colorList[i]));
            if(colorIndex == i){
                radioButton.performClick();
            }
        }

        radioGroup.post(
                new Runnable() {
                    @Override
                    public void run() {
                        radioGroup.setOnCheckedChangeListener(
                                new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        switch (checkedId){
                                            case R.id.reader_background_radio_1:
                                                setBackground(colorList[0], true);
                                                mSettings.setReaderBackgroundIndex(0);
                                                break;
                                            case R.id.reader_background_radio_2:
                                                setBackground(colorList[1], true);
                                                mSettings.setReaderBackgroundIndex(1);
                                                break;
                                            case R.id.reader_background_radio_3:
                                                setBackground(colorList[2], true);
                                                mSettings.setReaderBackgroundIndex(2);
                                                break;
                                        }
                                    }
                                }
                        );
                    }
                }
        );


        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setFontByIndex(int familyIndex, int sizeIndex) {
        String className = CSSUtils.getClassFromIndex(familyIndex, sizeIndex);
        mWebView.evaluateJavascript(String.format("setArticleFont('%s');", className), null);
        mSettings.setReaderFontClass(className);
    }

    private void setBackground(String hex, boolean setWebView){
        if(setWebView) {
            mWebView.evaluateJavascript("document.body.style.backgroundColor = '" + hex + "'", null);
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hex)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(hex));
        }
    }
}
