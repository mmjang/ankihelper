package com.mmjang.ankihelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.app.AnkiDroidHelper;
import com.mmjang.ankihelper.app.CBWatcherService;
import com.mmjang.ankihelper.app.MyApplication;

public class MainActivity extends Activity{

    AnkiDroidHelper mAnkiDroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initAnkiApi();
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
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

}
