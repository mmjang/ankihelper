package com.mmjang.ankihelper.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by liao on 2017/4/27.
 */

public class MyApplication extends Application{
    private static Context context;
    private static AnkiDroidHelper mAnkiDroid;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        LitePalApplication.initialize(context);
    }

    public static Context getContext(){
        return context;
    }

    public static AnkiDroidHelper getAnkiDroid(Activity activity){
        if(mAnkiDroid == null){
            mAnkiDroid = new AnkiDroidHelper(activity);
        }
        return mAnkiDroid;
    }

    private static void getAnkiDroidPermission(Activity activity){

    }


}
