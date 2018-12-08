package com.mmjang.ankihelper.data.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mmjang.ankihelper.data.database.DBContract;
import com.mmjang.ankihelper.data.database.ExternalDatabaseContext;
import com.mmjang.ankihelper.util.Constant;

import java.io.File;

public class ExternalContentDatabaseHelper extends SQLiteOpenHelper {

    //private static final String DB_NAME = "ankihelper.db";
    private static final int VERSION = 1;
    private Context mContext;

    public ExternalContentDatabaseHelper(Context context, String dbName) {
        super(new ExternalDatabaseContext(context),
                Constant.EXTERNAL_STORAGE_CONTENT_SUBDIRECTORY + File.separator + dbName, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
