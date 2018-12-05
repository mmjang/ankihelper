package com.mmjang.ankihelper.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ichi2.anki.FlashCardsContract;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.database.ExternalDatabaseHelper;
import com.mmjang.ankihelper.data.history.HistoryStat;
import com.mmjang.ankihelper.data.history.HistoryType;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import org.litepal.crud.DataSupport;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExternalDatabaseHelper helper = new ExternalDatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
//        HistoryStat historyStat = new HistoryStat(30);
//        int a = historyStat.getDayCount(HistoryType.POPUP_OPEN);
//        int b = historyStat.getDayCount(HistoryType.WORD_LOOK_UP);
//        int c = historyStat.getDayCount(HistoryType.NOTE_ADD);
//        int[][] result = historyStat.getHourStatistics();
//        int i = 1;
//        int j =2 ;
    }
}
