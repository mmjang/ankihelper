package com.mmjang.ankihelper.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.database.MigrationUtil;
import com.mmjang.ankihelper.data.plan.DefaultPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.domain.CBWatcherService;
import com.mmjang.ankihelper.ui.content.ContentActivity;
import com.mmjang.ankihelper.ui.customdict.CustomDictionaryActivity;
import com.mmjang.ankihelper.ui.plan.PlansManagerActivity;
import com.mmjang.ankihelper.ui.stat.StatActivity;
import com.mmjang.ankihelper.ui.translation.CustomTranslationActivity;
import com.mmjang.ankihelper.util.Constant;

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
    TextView textViewHelp;
    TextView textViewAddDefaultPlan;
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
        textViewHelp = (TextView) findViewById(R.id.btn_help);
        textViewAddDefaultPlan = (TextView) findViewById(R.id.btn_add_default_plan);
        textViewRandomQuote = (TextView) findViewById(R.id.btn_show_random_content);
        textViewCustomTranslation = findViewById(R.id.btn_set_custom_fanyi);
        switchMoniteClipboard.setChecked(
                settings.getMoniteClipboardQ()
        );

        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

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
                (buttonView, isChecked) -> {
                    settings.setMoniteClipboardQ(isChecked);
                    if (isChecked) {
                        startCBService();
                    } else {
                        stopCBService();
                    }
                }
        );

        switchLeftHandMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settings.setLeftHandModeQ(isChecked)
        );

        switchCancelAfterAdd.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settings.setAutoCancelPopupQ(isChecked)
        );

        switchPinkTheme.setOnCheckedChangeListener(
                (compoundButton, b) -> {
                    settings.setPinkThemeQ(b);
                    recreate();
                }
        );

        textViewOpenPlanManager.setOnClickListener(
                v -> {
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
        );

        textViewCustomDictionary.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LauncherActivity.this, CustomDictionaryActivity.class);
                    startActivity(intent);
                }
        );

        textViewCustomTranslation.setOnClickListener(
                view -> {
                    Intent intent = new Intent(LauncherActivity.this, CustomTranslationActivity.class);
                    startActivity(intent);
                }
        );

        textViewHelp.setOnClickListener(
                v -> {
                    String url = "https://github.com/mmjang/ankihelper/blob/master/README.md";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
        );

        textViewAddDefaultPlan.setOnClickListener(
                v -> {
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
        );


        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

        textViewRandomQuote.setOnClickListener(
                view -> {
                    Intent intent = new Intent(LauncherActivity.this, ContentActivity.class);
                    startActivity(intent);
                }
        );
    }

    private void checkAndRequestPermissions() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
            Toast.makeText(this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
            initStoragePermission();
            return;
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
        if (item.getItemId() == R.id.menu_item_stat) {
            Intent intent2 = new Intent(this, StatActivity.class);
            startActivity(intent2);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            return;
        }

        if (requestCode == REQUEST_CODE_ANKI) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initStoragePermission();
            } else {
                new AlertDialog.Builder(LauncherActivity.this)
                        .setMessage(R.string.permission_denied)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> openSettingsPage())
                        .setOnDismissListener(dialog -> initStoragePermission())
                        .show();
            }
        }
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ensureExternalDbDirectoryAndMigrate();
                askIfAddDefaultPlan();
            } else {
                Toast.makeText(this, "storage permission denied, go to the settings and grant it manually!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void ensureExternalDbDirectoryAndMigrate() {
        File extFilesDir = getExternalFilesDir(null);
        if (extFilesDir != null) {
            File f = new File(extFilesDir, Constant.EXTERNAL_STORAGE_DIRECTORY);
            if (!f.exists()) {
                f.mkdirs();
            }
            File f2 = new File(f, Constant.EXTERNAL_STORAGE_CONTENT_SUBDIRECTORY);
            if (!f2.exists()) {
                f2.mkdirs();
            }
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
        ContextCompat.startForegroundService(this, intent);
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
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            return;
                        }).show();
                return;
            }
        }
        if (plans.size() == 0) {
            new AlertDialog.Builder(LauncherActivity.this)
                    .setTitle(R.string.confirm_add_default_plan)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        DefaultPlan plan = new DefaultPlan(LauncherActivity.this);
                        plan.addDefaultPlan();
                        Toast.makeText(LauncherActivity.this, R.string.default_plan_added, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            new AlertDialog.Builder(LauncherActivity.this)
                    .setMessage(R.string.confirm_add_default_plan_when_exists_already)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        try {
                            DefaultPlan plan = new DefaultPlan(LauncherActivity.this);
                            plan.addDefaultPlan();
                            Toast.makeText(LauncherActivity.this,
                                    R.string.default_plan_added, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(LauncherActivity.this,
                                    e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(LauncherActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LauncherActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        2);
            }
            ensureExternalDbDirectoryAndMigrate();
        } else if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(LauncherActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LauncherActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);
            } else {
                ensureExternalDbDirectoryAndMigrate();
            }
        } else {
            ensureExternalDbDirectoryAndMigrate();
        }
    }
}
