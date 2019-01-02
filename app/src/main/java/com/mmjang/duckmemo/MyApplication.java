package com.mmjang.duckmemo;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mmjang.duckmemo.data.card.DaoMaster;
import com.mmjang.duckmemo.data.card.DaoSession;
import com.mmjang.duckmemo.util.Constant;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.database.Database;
import org.litepal.LitePalApplication;

import java.io.File;

import okhttp3.OkHttpClient;

/**
 * Created by liao on 2017/4/27.
 */

public class MyApplication extends MultiDexApplication {
    private static Context context;
    private static Application application;
    private static DaoSession daoSession;
    private static OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;
        LitePalApplication.initialize(context);
        CrashReport.initCrashReport(getApplicationContext(), "398dc6145b", false);
        AndroidThreeTen.init(this);
    }

    public static DaoSession getDaoSession() {
        if(daoSession == null){
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(application,
                    Environment.getExternalStorageDirectory() + File.separator + Constant.EXTERNAL_STORAGE_DIRECTORY
                            + File.separator + "duckmemo_greendao.db");
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        }
        return daoSession;
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication(){
        return application;
    }

    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }
}
