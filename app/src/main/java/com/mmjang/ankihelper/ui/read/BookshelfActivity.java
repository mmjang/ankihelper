package com.mmjang.ankihelper.ui.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.folioreader.FolioReader;
import com.folioreader.model.BookMeta;
import com.folioreader.model.HighLight;
import com.folioreader.model.ReadPosition;
import com.folioreader.model.ReadPositionImpl;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadPositionListener;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.book.Book;
import com.mmjang.ankihelper.data.book.DefaultBook;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.ui.plan.PlansAdapter;
import com.mmjang.ankihelper.ui.plan.PlansManagerActivity;
import com.mmjang.ankihelper.util.FileUtils;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;
import java.util.List;

public class BookshelfActivity extends AppCompatActivity implements
        OnHighlightListener, ReadPositionListener, FolioReader.OnClosedListener{

    private static final int FILE_CODE = 0;
    private FolioReader folioReader;
    private List<Book> mBookList;
    private RecyclerView mRecyclerBookList;
    private BooksAdapter mBooksAdapter;

    private static final int BOOK_IMPORT_FAILED = 1;
    private static final int BOOK_IMPORT_SUCCESSFUL = 2;

    private Book mCurrentBook;

    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BOOK_IMPORT_FAILED:
                    Toast.makeText(BookshelfActivity.this, "书籍导入失败，文件可能损坏", Toast.LENGTH_SHORT).show();
                    break;
                case BOOK_IMPORT_SUCCESSFUL:
                    Book book = (Book) msg.obj;
                    ExternalDatabase.getInstance().insertBook(book);
                    mBookList.add(0, book);
                    mBooksAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance(this);
        if(settings.getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        mRecyclerBookList = findViewById(R.id.book_list);
        folioReader = FolioReader.get();
        folioReader.setOnClosedListener(this);
        folioReader.setOnHighlightListener(this);
        folioReader.setReadPositionListener(this);

        initBookList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Book> newList = ExternalDatabase.getInstance().getLastBooks();
        mBookList.clear();
        onLoadingDefaultBooks(mBookList);
        mBookList.addAll(newList);
        mBooksAdapter.notifyDataSetChanged();
    }

    private void onLoadingDefaultBooks(List<Book> mBookList) {
        if(Settings.getInstance(this).getFirstTimeRunningReader() && mBookList.size() == 0){
            for(Book book : DefaultBook.getDefaultBook()){
                mBookList.add(book);
                ExternalDatabase.getInstance().insertBook(book);
            }
            Settings.getInstance(this).setFirstTimeRunningReader(false);
        }
    }

    private void initBookList(){
        mBookList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerBookList.setLayoutManager(llm);
        mBooksAdapter = new BooksAdapter(this, mBookList);
        //planList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerBookList.setAdapter(mBooksAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_bookshelf_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_import_book:
                onImportBook();
        }
        return true;
    }

    private void onImportBook() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/epub+zip");
        startActivityForResult(intent, FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == FILE_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if(data != null){
                uri = data.getData();
                String bookPath = FileUtils.getPath(this, uri);
                if(bookPath.endsWith(".epub")){
                    onImportEpubFile(bookPath);
                }
            }
        }
    }

    private void onImportEpubFile(final String path) {
        Toast.makeText(this, "正在导入...\n" + path, Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        BookMeta bookMeta = folioReader.getBookMeta(path);
                        Message message = mHandler.obtainMessage();
                        if(bookMeta == null){
                            message.what = BOOK_IMPORT_FAILED;
                            mHandler.sendMessage(message);

                        }else{
                            Book book = new Book(
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis(),
                                    bookMeta.getTitle(),
                                    bookMeta.getAuthor(),
                                    path,
                                    ""
                            );
                            message.what = BOOK_IMPORT_SUCCESSFUL;
                            message.obj = book;
                            mHandler.sendMessage(message);
                        }
                    }
                }
        );
        thread.start();
    }

    void onOpenBook(final Book book){
        mCurrentBook = ExternalDatabase.getInstance().refreshBook(book);
        mCurrentBook.setLastOpenTime(System.currentTimeMillis());
        if(!book.getReadPosition().isEmpty()){
            ReadPosition readPosition = ReadPositionImpl.createInstance(mCurrentBook.getReadPosition());
            //Toast.makeText(this, readPosition.toJson(), Toast.LENGTH_SHORT).show();
            folioReader.setReadPosition(readPosition);
        }
        ExternalDatabase.getInstance().updateBook(mCurrentBook);
        folioReader.openBook(book.getBookPath());
    }

    @Override
    public void onFolioReaderClosed() {

    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {

    }

    @Override
    public void saveReadPosition(ReadPosition readPosition) {
        //Toast.makeText(this, readPosition.toJson(), Toast.LENGTH_SHORT).show();
        mCurrentBook.setReadPosition(readPosition.toJson());
        ExternalDatabase.getInstance().updateBook(mCurrentBook);
    }
}
