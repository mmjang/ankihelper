package com.mmjang.ankihelper.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.ReadPosition;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadPositionListener;
import com.ichi2.anki.FlashCardsContract;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.content.ContentEntity;
import com.mmjang.ankihelper.data.content.ExternalContent;
import com.mmjang.ankihelper.data.database.ExternalDatabaseHelper;
import com.mmjang.ankihelper.data.history.HistoryStat;
import com.mmjang.ankihelper.data.history.HistoryType;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

public class TestActivity extends AppCompatActivity  implements OnHighlightListener, ReadPositionListener, FolioReader.OnClosedListener{
    private FolioReader folioReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView.setWebContentsDebuggingEnabled(true);
        folioReader = FolioReader.get();
        folioReader.setOnClosedListener(this);
        folioReader.setOnHighlightListener(this);
        folioReader.setReadPositionListener(this);
        folioReader.openBook(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "god.epub");
    }

    @Override
    public void onFolioReaderClosed() {

    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {

    }

    @Override
    public void saveReadPosition(ReadPosition readPosition) {

    }
}
