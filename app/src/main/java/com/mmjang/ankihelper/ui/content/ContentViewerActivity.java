package com.mmjang.ankihelper.ui.content;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.TextView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.content.ContentEntity;
import com.mmjang.ankihelper.data.content.ExternalContent;
import com.mmjang.ankihelper.ui.popup.PopupActivity;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;

public class ContentViewerActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    TextView contentTextView;
    ExternalContent externalContent;
    int mIndex;
    FloatingActionButton floatingActionButton;
    String currentContent = "";
    String note = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance(this);
        if(settings.getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_viewer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        contentTextView = findViewById(R.id.context_text);
        floatingActionButton = findViewById(R.id.add_content);
        externalContent = new ExternalContent(this);

        Intent intent = getIntent();
        mIndex = intent.getIntExtra(Constant.INTENT_ANKIHELPER_CONTENT_INDEX, 0);

        final SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent(mIndex);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(
                swipeRefreshListener
        );

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        swipeRefreshListener.onRefresh();
                    }
                }
        );

        floatingActionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, currentContent);
                        intent.putExtra(Constant.INTENT_ANKIHELPER_NOTE, note);
                        startActivity(intent);
                    }
                }
        );

    }

    private void refreshContent(int index) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, ContentEntity> asyncTask = new AsyncTask<Integer, Void, ContentEntity>() {
            @Override
            protected ContentEntity doInBackground(Integer... integers) {
                return externalContent.getRandomContentAt(integers[0],
                        Settings.getInstance(ContentViewerActivity.this).getShowContentAlreadyRead());
            }

            @Override
            protected void onPostExecute(ContentEntity contentEntity){
                swipeRefreshLayout.setRefreshing(false);
                if(contentEntity != null){
                    contentTextView.setText(
                            contentEntity.getText()
                    );
                    currentContent = contentEntity.getText();
                    note = contentEntity.getNote();
                }else{
                    Utils.showMessage(ContentViewerActivity.this, "刷新失败");
                }
            }
        };

        asyncTask.execute(index);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
