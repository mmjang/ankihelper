package com.mmjang.duckmemo.ui.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.Settings;
import com.mmjang.duckmemo.data.news.NewsEntry;
import com.mmjang.duckmemo.data.news.NewsLoader;
import com.mmjang.duckmemo.data.news.NewsLoaderUtils;
import com.mmjang.duckmemo.data.news.NewsUpdater;
import com.mmjang.duckmemo.data.news.UpdateResult;
import com.mmjang.duckmemo.ui.LauncherActivity;
import com.mmjang.duckmemo.ui.browser.NoteBrowserActivity;
import com.mmjang.duckmemo.ui.review.ReviewerActivity;
import com.mmjang.duckmemo.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends AppCompatActivity {

    SwipeRefreshLayout newsListSwipeRefresh;
    RecyclerView recyclerNewsList;
    Spinner mNewsSourceSpinner;
    List<NewsLoader> mNewsLoaderList;
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private List<NewsEntry> mNewsEntryList;
    private NewsAdapter mNewsAdapter;
    private int mCurrentNewsSourceIndex;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private static final int REQUEST_CODE_STORAGE = 1;
    private boolean hasPermission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        dl = (DrawerLayout)findViewById(R.id.news_activity_drawer);
        t = new ActionBarDrawerToggle(this, dl, android.R.string.ok, android.R.string.no);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.drawer_reviewer:
                        startActivity(new Intent(NewsListActivity.this, ReviewerActivity.class));
                        return true;

                    case R.id.drawer_note_list:
                        startActivity(new Intent(NewsListActivity.this, NoteBrowserActivity.class));
                        return true;
                    case R.id.drawer_settings:
                        startActivity(new Intent(NewsListActivity.this, LauncherActivity.class));
                        return true;
                        default:
                        return true;
                }
            }
        });




        recyclerNewsList = findViewById(R.id.newList);
        newsListSwipeRefresh = findViewById(R.id.news_list_swipe_refresh);

        swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent(mCurrentNewsSourceIndex);
            }
        };

        newsListSwipeRefresh.setOnRefreshListener(
                swipeRefreshListener
        );
        //after granted, load news
        initStoragePermission();
    }

    //called inside onOptionsMenuCreated
    private void setUpNewsSourceSpinner() {
        mNewsLoaderList = NewsLoaderUtils.getLoaderList();
        String[] sourceNameList = new String[mNewsLoaderList.size()];
        for(int i = 0; i < sourceNameList.length; i ++){
            sourceNameList[i] = mNewsLoaderList.get(i).getSourceName();
        }
        mNewsSourceSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.news_source_dropdown_item, sourceNameList));
        mNewsSourceSpinner.setSelection(Settings.getInstance(this).getLastNewsSourceIndex());
        mNewsSourceSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentNewsSourceIndex = position;
                        //wait for the refresh to init
                        newsListSwipeRefresh.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        newsListSwipeRefresh.setRefreshing(true);
                                        swipeRefreshListener.onRefresh();
                                    }
                                }
                        );
                        Settings.getInstance(NewsListActivity.this).setLastNewsSourceIndex(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    private void setOldContent(){

    }

    private void refreshContent(final int index) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, UpdateResult> asyncTaskWeb = new AsyncTask<Integer, Void, UpdateResult>() {
            @Override
            protected UpdateResult doInBackground(Integer... integers) {
                return (new NewsUpdater(mNewsLoaderList.get(index))).update();
            }

            @Override
            protected void onPostExecute(UpdateResult result){
                if(index == mCurrentNewsSourceIndex) {//if the user has not changed the news source during refreshing
                    newsListSwipeRefresh.setRefreshing(false);
                    if (result.isSuccessful()) {
                        mNewsEntryList.clear();
                        mNewsEntryList.addAll(result.getNewsEntries());
                        mNewsAdapter.notifyDataSetChanged();
                        Toast.makeText(NewsListActivity.this,
                                result.getUpdateCount() + " new(s) updated", Toast.LENGTH_SHORT).show();
                    } else {
//                    mNewsEntryList.clear();
//                    mNewsEntryList.addAll(result.getNewsEntries());
//                    mNewsAdapter.notifyDataSetChanged();
                        Utils.showMessage(NewsListActivity.this, "新闻更新失败");
                    }
                }
            }
        };

        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, List<NewsEntry>> asyncTaskDB = new AsyncTask<Integer, Void, List<NewsEntry>>() {
            @Override
            protected List<NewsEntry> doInBackground(Integer... integers) {
                return (new NewsUpdater(mNewsLoaderList.get(index))).getDBCache();
            }

            @Override
            protected void onPostExecute(List<NewsEntry> result){
                //no need to disable refreshing
                mNewsEntryList.clear();
                mNewsEntryList.addAll(result);
                mNewsAdapter.notifyDataSetChanged();
                Toast.makeText(NewsListActivity.this, "DB cache loaded", Toast.LENGTH_SHORT).show();
            }
        };

        asyncTaskDB.execute(0);
        asyncTaskWeb.execute(0);
    }

    void initNewsList(){
        mNewsEntryList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerNewsList.setLayoutManager(llm);
        mNewsAdapter = new NewsAdapter(this, mNewsEntryList);
        recyclerNewsList.setAdapter(mNewsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.news_list_menu_entry, menu);
        mNewsSourceSpinner = (Spinner) menu.findItem(R.id.news_source_spinner).getActionView();
        mNewsSourceSpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(hasPermission) {
            setUpNewsSourceSpinner();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void initStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(NewsListActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(NewsListActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(NewsListActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                }
            } else {
                hasPermission = true;
                initNewsList();
                //setUpNewsSourceSpinner();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }

        if (requestCode == REQUEST_CODE_STORAGE) {
            if (requestCode == REQUEST_CODE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
                initNewsList();
                setUpNewsSourceSpinner();
            } else {
                Toast.makeText(this, "storage permission denied, go to the settings and grant it manually!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
