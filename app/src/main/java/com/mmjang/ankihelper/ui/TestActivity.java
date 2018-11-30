package com.mmjang.ankihelper.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ichi2.anki.FlashCardsContract;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.plan.OutputPlan;

import junit.framework.Test;

import org.litepal.crud.DataSupport;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<OutputPlan> newList = DataSupport.findAll(OutputPlan.class);
        OutputPlan basicPlan = null;
        for (OutputPlan plan : newList) {
            if (plan.getPlanName().equals("basic")) {
                basicPlan = plan;
                break;
            }
        }
        long deckId = basicPlan.getOutputDeckId();
        long modelId = basicPlan.getOutputModelId();
        AnkiDroidHelper anki = new AnkiDroidHelper(TestActivity.this);
        long noteid = anki.getApi().addNote(modelId, deckId, new String[]{"正面", "反面"}, null);
        ContentResolver cr = getContentResolver();
        Uri noteUri = Uri.withAppendedPath(FlashCardsContract.Note.CONTENT_URI, Long.toString(noteid));
        //int i = cr.delete(noteUri, null, null);
        //Toast.makeText(this, Integer.toString(i), Toast.LENGTH_SHORT).show();
    }
}
