package com.mmjang.ankihelper.ui.content;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.content.ContentEntity;
import com.mmjang.ankihelper.data.content.ExternalContent;
import com.mmjang.ankihelper.data.quote.Quote;
import com.mmjang.ankihelper.data.quote.RandomQuote;
import com.mmjang.ankihelper.ui.LauncherActivity;
import com.mmjang.ankihelper.ui.popup.PopupActivity;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;

import java.util.List;

public class ContentActivity extends AppCompatActivity {

    Switch switchShowContentAlreadyRead;
    LinearLayout contentCatagoryContainer;
    ExternalContent externalContent;
    Settings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = Settings.getInstance(this);
        if(settings.getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switchShowContentAlreadyRead = findViewById(R.id.switch_show_already_read);
        contentCatagoryContainer = findViewById(R.id.content_catagory_container);
        switchShowContentAlreadyRead.setChecked(!settings.getShowContentAlreadyRead());
        switchShowContentAlreadyRead.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setShowContentAlreadyRead(!isChecked);
                    }
                }
        );
        externalContent = new ExternalContent(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> contentDBList = externalContent.getContentDBList();
        if (contentDBList.size() == 0) {
            new AlertDialog.Builder(ContentActivity.this)
                    .setMessage("无内容可刷，请将 .db 数据库复制到 sdcard/ankihelper/content 文件夹下")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    }).show();
        }

        contentCatagoryContainer.removeAllViews();
        for (int i = 0; i < contentDBList.size(); i++) {
            final int index = i;
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.content_db_box, null);
            TextView tv = view.findViewById(R.id.textview_content_catagory);
            List<Long> counts = externalContent.getCountAt(index);
            String countText = "";
            if (counts != null) {
                countText = counts.get(1) + "/" + counts.get(0);
            }
            tv.setText(contentDBList.get(i) + ": " + countText);
            tv.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Thread thread = new Thread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
//                                            ContentEntity contentEntity = externalContent.getRandomContentAt(index,
//                                                    !settings.getShowContentAlreadyRead());
//                                            if(contentEntity == null){
//                                                ContentActivity.this.runOnUiThread(
//                                                        new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                Toast.makeText(ContentActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                );
//                                                return ;
//                                            }
                                            Intent intent = new Intent(getApplicationContext(), ContentViewerActivity.class);
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Constant.INTENT_ANKIHELPER_CONTENT_INDEX, index);
                                            startActivity(intent);
                                        }
                                    }
                            );

                            thread.start();

                        }
                    }
            );

            contentCatagoryContainer.addView(view);
        }
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
