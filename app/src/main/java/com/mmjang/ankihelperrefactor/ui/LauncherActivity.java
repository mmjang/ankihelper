package com.mmjang.ankihelperrefactor.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelperrefactor.R;
import com.mmjang.ankihelperrefactor.app.AnkiDroidHelper;
import com.mmjang.ankihelperrefactor.app.CBWatcherService;
import com.mmjang.ankihelperrefactor.app.MyApplication;
import com.mmjang.ankihelperrefactor.app.Settings;

public class LauncherActivity extends AppCompatActivity{

    AnkiDroidHelper mAnkiDroid;
    Settings settings;
    //views
    Switch switchMoniteClipboard;
    Switch switchCancelAfterAdd;
    TextView textViewOpenPlanManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initAnkiApi();
        settings = Settings.getInstance(this);

        switchMoniteClipboard = (Switch) findViewById(R.id.switch_monite_clipboard);
        switchCancelAfterAdd = (Switch) findViewById(R.id.switch_cancel_after_add);
        textViewOpenPlanManager = (TextView) findViewById(R.id.btn_open_plan_manager);

        switchMoniteClipboard.setChecked(
                settings.getMoniteClipboardQ()
        );

        switchCancelAfterAdd.setChecked(
                settings.getAutoCancelPopupQ()
        );

        switchMoniteClipboard.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setMoniteClipboardQ(isChecked);
                        if(isChecked){
                            startCBService();
                        }else{
                            stopCBService();
                        }
                    }
                }
        );

        switchCancelAfterAdd.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setAutoCancelPopupQ(isChecked);
                    }
                }
        );

        textViewOpenPlanManager.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LauncherActivity.this, PlansManagerActivity.class);
                        startActivity(intent);
                    }
                }
        );
        if(settings.getMoniteClipboardQ()){
            startCBService();
        }
    }

    private void initAnkiApi(){
        if(mAnkiDroid == null){
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!mAnkiDroid.isApiAvailable(MyApplication.getContext())) {
            Toast.makeText(this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
        }

        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(this, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(MainActivity.this, R.string.permission_granted, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
        }
    }

    private void startCBService(){
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void stopCBService(){
        Intent intent = new Intent(this, CBWatcherService.class);
        stopService(intent);
    }

}
