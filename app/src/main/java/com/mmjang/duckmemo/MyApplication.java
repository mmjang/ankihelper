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

/**
 * Created by liao on 2017/4/27.
 */

public class MyApplication extends MultiDexApplication {
    private static Context context;
    private static Application application;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;
        LitePalApplication.initialize(context);
        CrashReport.initCrashReport(getApplicationContext(), "398dc6145b", false);
        AndroidThreeTen.init(this);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                Environment.getExternalStorageDirectory() + File.separator + Constant.EXTERNAL_STORAGE_DIRECTORY
                + File.separator + "duckmemo_greendao.db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication(){
        return application;
    }
}
