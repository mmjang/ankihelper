package com.mmjang.duckmemo.ui.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.Settings;
import com.mmjang.duckmemo.data.card.CardHtmlGenerator;
import com.mmjang.duckmemo.data.news.NewsEntry;
import com.mmjang.duckmemo.data.news.NewsUpdater;
import com.mmjang.duckmemo.data.note.Note;
import com.mmjang.duckmemo.filter.NoteFilter;
import com.mmjang.duckmemo.ui.editor.NoteEditorActivity;
import com.mmjang.duckmemo.ui.news.NewsAdapter;
import com.mmjang.duckmemo.ui.news.NewsListActivity;
import com.mmjang.duckmemo.util.Constant;

import org.greenrobot.greendao.annotation.OrderBy;

import java.util.ArrayList;
import java.util.List;

public class NoteBrowserActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    List<Note> mNoteList;
    NoteAdapter mNoteAdapter;
    NoteFilter noteFilter;
    MenuItem mMenuItemToggleVisibility;
    public boolean mVisibility;
    private static final int REQUEST_CODE_EDIT = 1;
    private int noteEditPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_browser);
        mRecyclerView = findViewById(R.id.note_list_recycler_view);
        mProgressBar = findViewById(R.id.note_list_progressbar);
        noteFilter = new NoteFilter();
        mVisibility = Settings.getInstance(this).getLastNoteBrowserVisibilityState();
        initNoteList();
        loadNoteList();
    }

    private void loadNoteList() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, List<Note>> asyncTaskDB = new AsyncTask<Integer, Void, List<Note>>() {
            @Override
            protected List<Note> doInBackground(Integer... integers) {
                mProgressBar.setVisibility(View.VISIBLE);
                return noteFilter.getNoteList(NoteFilter.ORDERBY.TIME, NoteFilter.ORDER.DESC, "", "");
            }

            @Override
            protected void onPostExecute(List<Note> result){
                //no need to disable refreshing
                mNoteList.clear();
                mNoteList.addAll(result);
                mNoteAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        };
        asyncTaskDB.execute(0);
    }

    private void initNoteList() {
        mNoteList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mNoteAdapter = new NoteAdapter(this, mNoteList);
        mRecyclerView.setAdapter(mNoteAdapter);
    }

    public void onEditNote(Note note, int notePosition){
        noteEditPosition = notePosition;
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra(Constant.INTENT_DUCKMEMO_NOTE_ID, note.getId());
        intent.setAction(Intent.ACTION_SEND);
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_EDIT){
            if(resultCode == Constant.RESULT_SAVED){
                if(noteEditPosition >= 0) {
                    Note note = mNoteList.get(noteEditPosition);
                    MyApplication.getDaoSession().getNoteDao().refresh(note);
                    mNoteAdapter.notifyItemChanged(noteEditPosition);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.activity_note_browser_menu_entry, menu);
        mMenuItemToggleVisibility = menu.findItem(R.id.menu_item_toggle_hide);
        if(mVisibility){
            mMenuItemToggleVisibility.setIcon(R.drawable.ic_visible);
        }else{
            mMenuItemToggleVisibility.setIcon(R.drawable.ic_invisible);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_toggle_hide:
                mVisibility = !mVisibility;
                if(mVisibility){
                    mMenuItemToggleVisibility.setIcon(R.drawable.ic_visible);
                }else{
                    mMenuItemToggleVisibility.setIcon(R.drawable.ic_invisible);
                }
                Settings.getInstance(this).setLastNoteBrowserVisibilityState(mVisibility);
                mNoteAdapter.notifyDataSetChanged();
        }
        return true;
    }


}
