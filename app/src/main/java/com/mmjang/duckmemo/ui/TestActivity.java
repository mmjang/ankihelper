package com.mmjang.duckmemo.ui;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.ReadPosition;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadPositionListener;
import com.mmjang.duckmemo.R;
import java.io.File;

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
