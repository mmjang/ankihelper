package com.mmjang.ankihelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.database.MigrationUtil;
import com.mmjang.ankihelper.data.plan.DefaultPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.data.quote.Quote;
import com.mmjang.ankihelper.data.quote.RandomQuote;
import com.mmjang.ankihelper.domain.CBWatcherService;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.about.AboutActivity;
import com.mmjang.ankihelper.ui.content.ContentActivity;
import com.mmjang.ankihelper.ui.customdict.CustomDictionaryActivity;
import com.mmjang.ankihelper.ui.plan.PlansManagerActivity;
import com.mmjang.ankihelper.ui.popup.PopupActivity;
import com.mmjang.ankihelper.ui.read.BookshelfActivity;
import com.mmjang.ankihelper.ui.stat.StatActivity;
import com.mmjang.ankihelper.ui.translation.CustomTranslationActivity;
import com.mmjang.ankihelper.util.Constant;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {

    AnkiDroidHelper mAnkiDroid;
    Settings settings;
    //views
    Switch switchMoniteClipboard;
    Switch switchCancelAfterAdd;
    Switch switchLeftHandMode;
    Switch switchPinkTheme;
    TextView textViewOpenPlanManager;
    TextView textViewCustomDictionary;
    TextView textViewAbout;
    TextView textViewHelp;
    TextView textViewAddDefaultPlan;
    TextView textViewAddQQGroup;
    TextView textViewRandomQuote;
    TextView textViewCustomTranslation;

    private static final int REQUEST_CODE_ANKI = 0;
    private static final int REQUEST_CODE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = Settings.getInstance(this);
        if (settings.getPinkThemeQ()) {
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setVersion();
        checkAndRequestPermissions();
        switchMoniteClipboard = (Switch) findViewById(R.id.switch_monite_clipboard);
        switchCancelAfterAdd = (Switch) findViewById(R.id.switch_cancel_after_add);
        switchLeftHandMode = (Switch) findViewById(R.id.left_hand_mode);
        switchPinkTheme = (Switch) findViewById(R.id.pink_theme_switch);
        textViewOpenPlanManager = (TextView) findViewById(R.id.btn_open_plan_manager);
        textViewCustomDictionary = (TextView) findViewById(R.id.btn_open_custom_dictionary);
        textViewAbout = (TextView) findViewById(R.id.btn_about_and_support);
        textViewHelp = (TextView) findViewById(R.id.btn_help);
        textViewAddDefaultPlan = (TextView) findViewById(R.id.btn_add_default_plan);
        textViewAddQQGroup = (TextView) findViewById(R.id.btn_qq_group);
        textViewRandomQuote = (TextView) findViewById(R.id.btn_show_random_content);
        textViewCustomTranslation = findViewById(R.id.btn_set_custom_fanyi);

        textViewAbout.setText(Html.fromHtml("<font color='red'>❤</font>" + getResources().getString(R.string.btn_about_and_support_str)));
        switchMoniteClipboard.setChecked(
                settings.getMoniteClipboardQ()
        );

        switchCancelAfterAdd.setChecked(
                settings.getAutoCancelPopupQ()
        );

        switchLeftHandMode.setChecked(
                settings.getLeftHandModeQ()
        );

        switchPinkTheme.setChecked(
                settings.getPinkThemeQ()
        );

        switchMoniteClipboard.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setMoniteClipboardQ(isChecked);
                        if (isChecked) {
                            startCBService();
                        } else {
                            stopCBService();
                        }
                    }
                }
        );

        switchLeftHandMode.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setLeftHandModeQ(isChecked);
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

        switchPinkTheme.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        settings.setPinkThemeQ(b);
                        recreate();
                    }
                }
        );

        textViewOpenPlanManager.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAnkiDroid == null) {
                            mAnkiDroid = new AnkiDroidHelper(LauncherActivity.this);
                        }
                        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
                            Toast.makeText(LauncherActivity.this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (mAnkiDroid.shouldRequestPermission()) {
                            mAnkiDroid.requestPermission(LauncherActivity.this, 0);
                            return;
                        } else {

                        }
                        Intent intent = new Intent(LauncherActivity.this, PlansManagerActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewCustomDictionary.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LauncherActivity.this, CustomDictionaryActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewAbout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LauncherActivity.this, AboutActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewCustomTranslation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LauncherActivity.this, CustomTranslationActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewHelp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://github.com/mmjang/ankihelper/blob/master/README.md";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );

        textViewAddDefaultPlan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAnkiDroid == null) {
                            mAnkiDroid = new AnkiDroidHelper(LauncherActivity.this);
                        }
                        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
                            Toast.makeText(LauncherActivity.this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (mAnkiDroid.shouldRequestPermission()) {
                            mAnkiDroid.requestPermission(LauncherActivity.this, 0);
                            return;
                        } else {

                        }
                        askIfAddDefaultPlan();
                    }
                }
        );

        textViewAddQQGroup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        joinQQGroup("-1JxtFYckXpYUMpZKRbrMWuceCgM23R7");
                    }
                }
        );
        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

        textViewRandomQuote.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LauncherActivity.this, ContentActivity.class);
                        startActivity(intent);
                    }
                }
        );

        //debug new feature
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    YoudaoOnline.getDefinition("dedicate");
//                }
//                catch (IOException e){
//
//                }
//            }
//        });
//        thread.start();
    }

    private void checkAndRequestPermissions() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
            Toast.makeText(this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
        }

        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(this, REQUEST_CODE_ANKI);
        } else {
            initStoragePermission();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_about_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_book_shelf:
                Intent intent = new Intent(this, BookshelfActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_stat:
                Intent intent2 = new Intent(this, StatActivity.class);
                startActivity(intent2);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }

        if (requestCode == REQUEST_CODE_ANKI) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initStoragePermission();
            } else {
                //Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(LauncherActivity.this)
                        .setMessage(R.string.permission_denied)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                openSettingsPage();
                            }
                        }).show();
            }
        }
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (requestCode == REQUEST_CODE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ensureExternalDbDirectoryAndMigrate();
                askIfAddDefaultPlan();
            } else {
                Toast.makeText(this, "storage permission denied, go to the settings and grant it manually!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void ensureExternalDbDirectoryAndMigrate() {
        File f = new File(Environment.getExternalStorageDirectory(), Constant.EXTERNAL_STORAGE_DIRECTORY);
        if (!f.exists()) {
            f.mkdirs();
        }
        //the content folder
        File f2 = new File(f, Constant.EXTERNAL_STORAGE_CONTENT_SUBDIRECTORY);
        if (!f2.exists()) {
            f2.mkdir();
        }

        if (!settings.getOldDataMigrated() && MigrationUtil.needMigration()) {
            Toast.makeText(this, "正在迁移旧版数据请稍等...", Toast.LENGTH_LONG).show();
            MigrationUtil.migrate();
            Toast.makeText(this, "旧版数据迁移完成！", Toast.LENGTH_SHORT).show();
            settings.setOldDataMigrated(true);
        }
    }

    private void startCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void stopCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        stopService(intent);
    }

    void askIfAddDefaultPlan() {
        List<OutputPlanPOJO> plans = ExternalDatabase.getInstance().getAllPlan();
        for (OutputPlanPOJO plan : plans) {
            if (plan.getPlanName().equals(DefaultPlan.DEFAULT_PLAN_NAME)) {
                new AlertDialog.Builder(LauncherActivity.this)
                        .setMessage(R.string.duplicate_plan_name_complain)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                return;
                            }
                        }).show();
                return;
            }
        }
        if (plans.size() == 0) {
            new AlertDialog.Builder(LauncherActivity.this)
                    .setTitle(R.string.confirm_add_default_plan)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DefaultPlan plan = new DefaultPlan(LauncherActivity.this);
                            plan.addDefaultPlan();
                            Toast.makeText(LauncherActivity.this, R.string.default_plan_added, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            new AlertDialog.Builder(LauncherActivity.this)
                    .setMessage(R.string.confirm_add_default_plan_when_exists_already)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                DefaultPlan plan = new DefaultPlan(LauncherActivity.this);
                                plan.addDefaultPlan();
                                Toast.makeText(LauncherActivity.this, R.string.default_plan_added, Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(LauncherActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void openSettingsPage() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /****************
     *
     * 发起添加群流程。群号：安卓划词助手用户群(871406754) 的 key 为： -1JxtFYckXpYUMpZKRbrMWuceCgM23R7
     * 调用 joinQQGroup(-1JxtFYckXpYUMpZKRbrMWuceCgM23R7) 即可发起手Q客户端申请加群 安卓划词助手用户群(871406754)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public void setVersion() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView versionTextView = (TextView) findViewById(R.id.textview_version);
            versionTextView.setText(
                    "Ver: " + versionName
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(LauncherActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(LauncherActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(LauncherActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                }
            } else {
                ensureExternalDbDirectoryAndMigrate();
            }
        }
    }
}
