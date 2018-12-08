package com.mmjang.ankihelper.ui.content;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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

        List<String> contentDBList = externalContent.getContentDBList();
        if(contentDBList.size() == 0){
            new AlertDialog.Builder(ContentActivity.this)
                    .setMessage("无内容可刷，请将 .db 数据库复制到 sdcard/ankihelper/content 文件夹下")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    }).show();
        }
        for(int i = 0; i < contentDBList.size(); i ++){
            final int index = i;
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.content_db_box, null);
            TextView tv = view.findViewById(R.id.textview_content_catagory);
            tv.setText(contentDBList.get(i));
            tv.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Thread thrad = new Thread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            ContentEntity contentEntity = externalContent.getRandomContentAt(index,
                                                    !settings.getShowContentAlreadyRead());
                                            Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            intent.putExtra(Intent.EXTRA_TEXT, contentEntity.getText());
                                            intent.putExtra(Constant.INTENT_ANKIHELPER_NOTE, contentEntity.getNote());
                                            startActivity(intent);
                                        }
                                    }
                            );

                            thrad.start();

                        }
                    }
            );

            contentCatagoryContainer.addView(view);
        }
    }
}
