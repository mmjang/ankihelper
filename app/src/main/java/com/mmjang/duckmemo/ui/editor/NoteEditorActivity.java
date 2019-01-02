package com.mmjang.duckmemo.ui.editor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.note.Note;
import com.mmjang.duckmemo.data.note.NoteDao;
import com.mmjang.duckmemo.util.Constant;

public class NoteEditorActivity extends AppCompatActivity {

    EditText mEditTextSentence;
    EditText mEditTextTranslation;
    EditText mEditTextWord;
    EditText mEditTextDefinition;
    EditText mEditTextTags;

    Note mNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        bindView();
        long id = getIntent().getLongExtra(Constant.INTENT_DUCKMEMO_NOTE_ID, 0l);
        mNote = MyApplication.getDaoSession().getNoteDao().queryBuilder().where(NoteDao.Properties.Id.eq(id)).unique();
        mEditTextSentence.setText(mNote.getSentence());
        mEditTextTranslation.setText(mNote.getTranslation());
        mEditTextWord.setText(mNote.getWord());
        mEditTextDefinition.setText(mNote.getDefinition());
        mEditTextTags.setText(mNote.getTag());
    }

    private void bindView() {
        mEditTextTags = findViewById(R.id.editor_tags);
        mEditTextDefinition = findViewById(R.id.editor_definition);
        mEditTextWord = findViewById(R.id.editor_word);
        mEditTextSentence = findViewById(R.id.editor_sentence);
        mEditTextTranslation = findViewById(R.id.editor_translation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_activity_note_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_note_editor_save:
                saveNote();
                setResult(Constant.RESULT_SAVED);
                finish();
                break;
        }

        return true;
    }

    private void saveNote() {
        mNote.setSentence(mEditTextSentence.getText().toString());
        mNote.setTranslation(mEditTextTranslation.getText().toString());
        mNote.setWord(mEditTextWord.getText().toString());
        mNote.setDefinition(mEditTextDefinition.getText().toString());
        MyApplication.getDaoSession().getNoteDao().update(mNote);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(Constant.RESULT_ABORTED);
        finish();
    }
}
