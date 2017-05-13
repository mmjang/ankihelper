package com.mmjang.ankihelperrefactor.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelperrefactor.R;
import com.mmjang.ankihelperrefactor.app.AnkiDroidHelper;
import com.mmjang.ankihelperrefactor.app.CBWatcherService;
import com.mmjang.ankihelperrefactor.app.Definition;
import com.mmjang.ankihelperrefactor.app.Esdict;
import com.mmjang.ankihelperrefactor.app.IDictionary;
import com.mmjang.ankihelperrefactor.app.MyApplication;

import java.util.List;

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
