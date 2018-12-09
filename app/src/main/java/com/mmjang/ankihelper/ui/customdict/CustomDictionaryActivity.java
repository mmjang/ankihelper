package com.mmjang.ankihelper.ui.customdict;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.dict.CustomDictionary;
import com.mmjang.ankihelper.data.dict.DictionaryRegister;
import com.mmjang.ankihelper.data.dict.IDictionary;
import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryManager;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomDictionaryActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int FILE_CODE = 2;
    private List<IDictionary> mDictionaries;
    private List<CustomDictionary> mCustomDictionaries = new ArrayList<>();
    private TextView mBtnImportCustomDictionary;
    private TextView mBtnClearCustomDictionaries;
    private ProgressBar mProgressBarImportCustomDictionary;
        //async event
    private static final int DICT_ADDED = 3;
    private static final int DICT_ADD_FAILED = 4;
    private static final int DICTS_REMOVED = 5;

    private int mMaxId = -1;

    private boolean startProgressBarQ = false;
    //async
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DICT_ADDED:
                    Toast.makeText(CustomDictionaryActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                    reFreshData();
                    setProgressBar(false);
                    break;
                case DICT_ADD_FAILED:
                    Toast.makeText(CustomDictionaryActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                    setProgressBar(false);
                    break;
                case DICTS_REMOVED:
                    Toast.makeText(CustomDictionaryActivity.this, "自定义词典已清空！", Toast.LENGTH_SHORT).show();
                    reFreshData();
                    setProgressBar(false);
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Settings.getInstance(this).getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dictionary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //views
        mBtnImportCustomDictionary = (TextView) findViewById(R.id.btn_import_custom_dictionary);
        mBtnClearCustomDictionaries = (TextView) findViewById(R.id.btn_remove_all_custom_dictionaries);
        mProgressBarImportCustomDictionary = (ProgressBar) findViewById(R.id.progress_bar_import_custom_dictionary);

        mBtnClearCustomDictionaries.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProgressBar(true);
                   Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomDictionaryManager cm = new CustomDictionaryManager(MyApplication.getContext(), "");
                    cm.db.clearDB();
                    Message message = mHandler.obtainMessage();
                    message.what = DICTS_REMOVED;
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

                    }
                }
        );

    }

    void reFreshData(){
         mDictionaries = DictionaryRegister.getDictionaryObjectList();
        for(IDictionary dict : mDictionaries){
            if(dict instanceof CustomDictionary){
                mCustomDictionaries.add((CustomDictionary) dict);
            }
        }

        for(CustomDictionary cd : mCustomDictionaries){
            int id = cd.getId();
            if(id > mMaxId){
                mMaxId = id;
            }
        }

    }

    @Override
    protected void onStart() {
        setProgressBar(true);
        reFreshData();
        setProgressBar(false);
        super.onStart();
        permission();
    }

    private void permission(){
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {

            } else {
                mBtnImportCustomDictionary.setClickable(false);
                requestPermission(); // Code for permission
            }
        }
        //listener
        mBtnImportCustomDictionary.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectFile();
                    }
                }
        );

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(CustomDictionaryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(CustomDictionaryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            ActivityCompat.requestPermissions(CustomDictionaryActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mBtnImportCustomDictionary.setClickable(true);
                } else {
                    Log.e("value", "Permission Denied, You cannot load custom dictionaries.");
                }
                break;
        }
    }

    void selectFile(){
        Intent i = new Intent(this, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILE_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            List<Uri> files = Utils.getSelectedFilesFromResult(intent);
            if(files.size() > 0){
                startProgressBarQ = true;
                final File file = Utils.getFileForUri(files.get(0));
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                    try {
                        CustomDictionaryManager cm = new CustomDictionaryManager(MyApplication.getContext(), "");
                        if(cm.processOneDictionaryFile(mMaxId + 1, file)){
                            Message message = mHandler.obtainMessage();
                            message.what = DICT_ADDED;
                            mHandler.sendMessage(message);
                        }
                        else{
                            Message message = mHandler.obtainMessage();
                            message.what = DICT_ADD_FAILED;
                            mHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }
        });
        thread.start();
            }
            //long id = System.currentTimeMillis();
//            List<Uri> files = Utils.getSelectedFilesFromResult(intent);
//            CustomDictionaryManager cm = new CustomDictionaryManager(this, "");
//            cm.clearDictionaries();
//            int i = 0;
//            for (Uri uri: files) {
//                File file = Utils.getFileForUri(uri);
//                cm.processOneDictionaryFile(i, file);
//                CustomDictionaryInformation info = cm.db.getDictInfo(i);
//                List<String[]> result = cm.db.queryHeadword(i, "one");
//                Cursor cursor = cm.db.getFilterCursor(i, "t");
//                List<String> rrr = new ArrayList<>();
//                String[] forms = FormsUtil.getInstance(this).getForms("ones");
//                while(cursor.moveToNext()){
//                    rrr.add(cursor.getString(1));
//                }
//                int ccc = rrr.size();
//                ;
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(startProgressBarQ){
            setProgressBar(true);
            startProgressBarQ = false;
        }
    }

    private void setProgressBar(boolean b){
        if(b) {
            mProgressBarImportCustomDictionary.setVisibility(View.VISIBLE);
        }
        else{
            mProgressBarImportCustomDictionary.setVisibility(View.GONE);
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
