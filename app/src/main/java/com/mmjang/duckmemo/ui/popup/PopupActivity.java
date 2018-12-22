package com.mmjang.duckmemo.ui.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.folioreader.model.dictionary.Dictionary;
import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.Settings;
import com.mmjang.duckmemo.data.database.ExternalDatabase;
import com.mmjang.duckmemo.data.dict.Definition;
import com.mmjang.duckmemo.data.dict.DictionaryRegister;
import com.mmjang.duckmemo.data.dict.IDictionary;
import com.mmjang.duckmemo.data.history.HistoryUtil;
import com.mmjang.duckmemo.data.model.UserTag;
import com.mmjang.duckmemo.data.note.Addable;
import com.mmjang.duckmemo.data.note.DBExporter;
import com.mmjang.duckmemo.data.note.Exporter;
import com.mmjang.duckmemo.data.note.Note;
import com.mmjang.duckmemo.data.plan.OutputPlan;
import com.mmjang.duckmemo.data.plan.OutputPlanPOJO;
import com.mmjang.duckmemo.data.tag.Tag;
import com.mmjang.duckmemo.data.tag.TagDao;
import com.mmjang.duckmemo.domain.CBWatcherService;
import com.mmjang.duckmemo.domain.PlayAudioManager;
import com.mmjang.duckmemo.domain.PronounceManager;
import com.mmjang.duckmemo.ui.LauncherActivity;
import com.mmjang.duckmemo.ui.plan.PlanEditorActivity;
import com.mmjang.duckmemo.ui.widget.BigBangLayout;
import com.mmjang.duckmemo.ui.widget.BigBangLayoutWrapper;
import com.mmjang.duckmemo.util.Constant;
import com.mmjang.duckmemo.util.FieldUtil;
import com.mmjang.duckmemo.util.RegexUtil;
import com.mmjang.duckmemo.util.TextSplitter;
import com.mmjang.duckmemo.util.Translator;
import com.mmjang.duckmemo.util.Utils;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.mmjang.duckmemo.util.FieldUtil.getBlankSentence;
import static com.mmjang.duckmemo.util.FieldUtil.getBoldSentence;
import static com.mmjang.duckmemo.util.FieldUtil.getNormalSentence;


public class PopupActivity extends Activity implements BigBangLayoutWrapper.ActionListener{

    CardView mActContainer;
    CardView mBigBangContainer;
    CardView mDictAndDefContainer;
    Button btnCancelBlankAboveCard;
    CardView mCardViewPopup;
    List<IDictionary> dictionaryList;
    LinearLayout mDictionaryTabGroup;
    IDictionary currentDicitonary;
    Settings settings;
    String mTextToProcess;
    String mPlanNameFromIntent;
    String mCurrentKeyWord;
    TextSplitter mTextSplitter;
    String mNoteEditedByUser = "";
    Set<String> mTagEditedByUser = new HashSet<>();
    //posiblle pre set target word
    String mTargetWord;
    //possible url from dedicated borwser
    String mUrl = "";
    //translation
    String mTranslatedResult = "";
    boolean needTranslation = false;
    //export
    Exporter mExporter = new DBExporter();
    //views
    AutoCompleteTextView act;
    Button btnSearch;
    ImageButton btnPronounce;
    Spinner planSpinner;
    //RecyclerView recyclerViewDefinitionList;
    ImageButton mBtnEditNote;
    ImageButton mBtnEditTag;
    ImageButton mBtnTranslation;
    ImageButton mBtnFooterAdd;
    ImageButton mBtnFooterScrollup;
    ProgressBar progressBar;

    CardView mCardViewTranslation;
    EditText mEditTextTranslation;
    //fab
    //FloatingActionButton mFab;
    ScrollView scrollView;
    //plan b
    LinearLayout viewDefinitionList;
    List<Definition> mDefinitionList;
    TextView lastSelectedDictTab;
    //async event
    private static final int PROCESS_DEFINITION_LIST = 1;
    private static final int ASYNC_SEARCH_FAILED = 2;
    private static final int TRANSLATION_DONE = 3;
    private static final int TRANSLATIOn_FAILED = 4;
    //dao
    private TagDao tagDao;

    //async
    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESS_DEFINITION_LIST:
                    showSearchButton();
                    //scrollView.fullScroll(ScrollView.FOCUS_UP);
                    mDefinitionList = (List<Definition>) msg.obj;
                    processDefinitionList(mDefinitionList);
                    break;
                case ASYNC_SEARCH_FAILED:
                    showSearchButton();
                    Toast.makeText(PopupActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case TRANSLATION_DONE:
                    String result = (String) msg.obj;
                    String[] splitted = result.split("\n");
                    if(splitted.length > 0 && splitted[0].equals("error")){
                        Toast.makeText(PopupActivity.this, result, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    mEditTextTranslation.setText((result));
                    showTranslateDone();
                    showTranslationCardView(true);
                    break;
                default:
                    showTranslateNormal();
                    Toast.makeText(PopupActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private BigBangLayout bigBangLayout;
    private BigBangLayoutWrapper bigBangLayoutWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Settings.getInstance(this).getPinkThemeQ()){
            setTheme(R.style.TransparentPink);
        }
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_popup_new);
//        getActionBar().hide();
        //set animation
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        //
        assignViews();
        setHeight();
        initBigBangLayout();
        loadData(); //dictionaryList;
        initDictionaries();
        setEventListener();
        handleIntent();
        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

        bigBangLayout.post( new Runnable() {
            @Override
            public void run() {
                setTargetWord();
            }
        });

        tagDao = MyApplication.getDaoSession().getTagDao();
    }

    private void setHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        btnCancelBlankAboveCard.getLayoutParams().height = height / 2;
        mCardViewPopup.setMinimumHeight(height / 2);
    }

    private void initDictionaries() {
        String lastSelectedDict = settings.getLastSelectedDict();
        LayoutInflater inflater = PopupActivity.this.getLayoutInflater();
        for(final IDictionary dictionary : dictionaryList){
            final TextView dictTab = (TextView) inflater.inflate(R.layout.dict_tab_textview, null);
            dictTab.setText(dictionary.getDictionaryName());
            mDictionaryTabGroup.addView(dictTab);
            dictTab.setBackgroundResource(R.drawable.dict_tab);

            dictTab.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dictTab.setBackgroundResource(R.drawable.dict_tab_selected);
                            dictTab.setClickable(false);
                            currentDicitonary = getDictionaryFromName(dictTab.getText().toString());
                            act.setAdapter((SimpleCursorAdapter) currentDicitonary.getAutoCompleteAdapter(PopupActivity.this, android.R.layout.simple_spinner_dropdown_item));
                            if(lastSelectedDictTab != null){
                                lastSelectedDictTab.setClickable(true);
                                lastSelectedDictTab.setBackgroundResource(R.drawable.dict_tab);
                            }
                            lastSelectedDictTab = dictTab;
                            settings.setLastSelectedDict(currentDicitonary.getDictionaryName());
                            asyncSearch(act.getText().toString());
                        }
                    }
            );
        }
        for(int i = 0; i < mDictionaryTabGroup.getChildCount(); i ++){
            TextView tv =(TextView) mDictionaryTabGroup.getChildAt(i);
            if(tv.getText().toString().equals(lastSelectedDict)){
                lastSelectedDictTab = tv;
            }
        }
        if(lastSelectedDictTab == null){
            lastSelectedDictTab = (TextView) mDictionaryTabGroup.getChildAt(0);
        }
        lastSelectedDictTab.setBackgroundResource(R.drawable.dict_tab_selected);
        lastSelectedDictTab.setClickable(false);
        currentDicitonary = getDictionaryFromName(lastSelectedDictTab.getText().toString());
        settings.setLastSelectedDict(currentDicitonary.getDictionaryName());
        act.setAdapter((SimpleCursorAdapter) currentDicitonary.getAutoCompleteAdapter(PopupActivity.this, android.R.layout.simple_spinner_dropdown_item));
    }

    private void setTargetWord(){
        if (!TextUtils.isEmpty(mTargetWord)) {
            for (BigBangLayout.Line line : bigBangLayout.getLines()) {
                List<BigBangLayout.Item> items = line.getItems();
                for (BigBangLayout.Item item : items) {
                    if (item.getText().equals(mTargetWord)) {
                        item.setSelected(true);
                    }
                }
            }
            act.setText(mTargetWord);
            asyncSearch(mTargetWord);
        }else{
            if(mTextToProcess.matches("[!-~]*")){
                mBigBangContainer.setVisibility(View.GONE);
                act.setText(mTextToProcess);
                asyncSearch(mTextToProcess);
            }
        }
    }

    private void setStatusBarColor() {
        int statusBarColor = 0;
        if (Build.VERSION.SDK_INT >= 21) {
            statusBarColor = getWindow().getStatusBarColor();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(statusBarColor);
        }
    }

    private void assignViews() {
        btnCancelBlankAboveCard = findViewById(R.id.btn_cancel_blank_above_card);
        act = (AutoCompleteTextView) findViewById(R.id.edit_text_hwd);
        btnSearch = (Button) findViewById(R.id.btn_search);
        mDictionaryTabGroup = findViewById(R.id.dictionary_tabs_countainer);
        //recyclerViewDefinitionList = (RecyclerView) findViewById(R.id.recycler_view_definition_list);
        viewDefinitionList = (LinearLayout) findViewById(R.id.view_definition_list);
        mBtnEditNote = (ImageButton) findViewById(R.id.footer_note);
        mBtnEditTag = (ImageButton) findViewById(R.id.footer_tag);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        bigBangLayout = (BigBangLayout) findViewById(R.id.bigbang);
        bigBangLayoutWrapper = (BigBangLayoutWrapper) findViewById(R.id.bigbang_wrapper);
        mCardViewTranslation = (CardView) findViewById(R.id.cardview_translation);
        mBtnTranslation = (ImageButton) findViewById(R.id.footer_translate);
        mEditTextTranslation = (EditText) findViewById(R.id.edittext_translation);
        //mFab = (FloatingActionButton) findViewById(R.id.fab);
//        mBtnFooterRotateLeft = (ImageButton) findViewById(R.id.footer_rotate_left);
        mBtnFooterAdd = findViewById(R.id.footer_add);
        mBtnFooterScrollup = (ImageButton) findViewById(R.id.footer_scroll_up);
        mCardViewPopup = findViewById(R.id.popup_card);
        mActContainer = findViewById(R.id.act_container);
        mBigBangContainer = findViewById(R.id.bigbang_container);
        mDictAndDefContainer = findViewById(R.id.dict_and_def_container);
    }

    private void loadData() {
        dictionaryList = DictionaryRegister.getDictionaryObjectList();
        settings = Settings.getInstance(this);
        //load tag
        boolean loadQ = settings.getSetAsDefaultTag();
        if (loadQ) {
            mTagEditedByUser = Utils.fromStringToTagSet(settings.getDefaulTag());
        }
    }

    private void initBigBangLayout(){
        bigBangLayout.setShowSymbol(true);
        bigBangLayout.setShowSpace(true);
        bigBangLayout.setShowSection(true);
        bigBangLayout.setItemSpace(0);
        bigBangLayout.setLineSpace(0);
        bigBangLayout.setTextPadding(5);
        bigBangLayout.setTextPaddingPort(5);
        bigBangLayoutWrapper.setStickHeader(true);
        bigBangLayoutWrapper.setActionListener(this);

    }

    private void setEventListener() {

        btnCancelBlankAboveCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        btnSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String word = act.getText().toString();
                        if (!word.isEmpty()) {
                            asyncSearch(word);
                            Utils.hideSoftKeyboard(PopupActivity.this);
                        }
                    }
                }
        );

        mBtnEditNote.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setupEditNoteDialog();
                    }
                }
        );

        mBtnEditTag.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setupEditTagDialog();
                    }
                }
        );

        act.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d("autocomplete", i + "");
                        act.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        btnSearch.callOnClick();
                                    }
                                }
                        );
                    }
                }
        );

        mBtnTranslation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mEditTextTranslation.getText().toString().equals("")){
                            asyncTranslate(mTextToProcess);
                        }
                    }
                }
        );

        mBtnFooterAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String word = "";
                        String defString = "";
                        List<Definition> checkedDefinition = new ArrayList<>();
                        boolean isNothingChecked = false;
                        //there's definition
                        if(mDefinitionList != null && mDefinitionList.size() > 0){
                            for(int i = 0; i < viewDefinitionList.getChildCount(); i ++){
                                View view = viewDefinitionList.getChildAt(i);
                                CheckBox checkBox = (CheckBox) view.findViewById(R.id.def_checkbox);
                                if(checkBox.isChecked()){
                                    checkedDefinition.add(mDefinitionList.get(i));
                                }
                            }
                            //there's more than one def, but nothing is checked
                            if(mDefinitionList.size() > 1 && checkedDefinition.size() == 0){
                                isNothingChecked = true;
                            }

                            //nothing checked, use all
                            if(checkedDefinition.size() == 0){
                                checkedDefinition = mDefinitionList;
                            }

                            for(Definition def : checkedDefinition){
                                defString += def.getCombinedDefinition();
                            }
                            //use the first check def as headword
                            word = checkedDefinition.get(0).getWord();
                        }

                        Addable addable = new Note();
                        addable.setWord(word);
                        addable.setTag(Utils.fromTagSetToString(mTagEditedByUser));
                        if(mBigBangContainer.getVisibility() == View.VISIBLE) {
                            //there's sentence, use it;
                            addable.setSentence(FieldUtil.getBoldSentence(bigBangLayout.getLines()));
                        }else{
                            addable.setSentence("");
                        }
                        addable.setExtra(mNoteEditedByUser);
                        addable.setDefinition(defString);
                        addable.setLanguage("en");
                        addable.setTranslation(mTranslatedResult);
                        mExporter.add(addable);
                        if(isNothingChecked){
                            Toast.makeText(PopupActivity.this, "未勾选释义，已保存全部释义", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PopupActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        mBtnFooterScrollup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(scrollView.getScrollY() > 0) {
                            scrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    }
                }
        );
    }

    private IDictionary getDictionaryFromName(String name) {
        for (IDictionary dict : dictionaryList) {
            if (dict.getDictionaryName().equals(name)) {
                return dict;
            }
        }
        return null;
    }

    private void processDefinitionList(List<Definition> definitionList) {
        if (definitionList.isEmpty()) {
            Toast.makeText(this, R.string.definition_not_found, Toast.LENGTH_SHORT).show();
        } else {
            viewDefinitionList.removeAllViewsInLayout();
            for (Definition def : definitionList) {
                viewDefinitionList.addView(getCardFromDefinition(def));
            }
        }
    }


    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (intent == null) {
            return;
        }
        if (type == null) {
            return;
        }
        //getStringExtra() may return null
        if (Intent.ACTION_SEND.equals(action) && type.equals("text/plain")) {
            String base64 = intent.getStringExtra(Constant.INTENT_DUCKMEMO_BASE64);
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_TEXT);
            if(base64 != null && !base64.equals("0")){
                mTextToProcess = new String(Base64.decode(mTextToProcess, Base64.DEFAULT));
            }
            mTargetWord = intent.getStringExtra(Constant.INTENT_DUCKMEMO_TARGET_WORD);
            mUrl = intent.getStringExtra(Constant.INTENT_DUCKMEMO_TARGET_URL);
            //mFbReaderBookmarkId = intent.getStringExtra(Constant.INTENT_ANKIHELPER_FBREADER_BOOKMARK_ID);
            String noteEditedByUser = intent.getStringExtra(Constant.INTENT_DUCKMEMO_NOTE);
            if(noteEditedByUser != null){
                mNoteEditedByUser = noteEditedByUser;
            }
            String updateId = intent.getStringExtra(Constant.INTENT_DUCKMEMO_NOTE_ID);

        }
        if (Intent.ACTION_PROCESS_TEXT.equals(action) && type.equals("text/plain")) {
                mTextToProcess = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);
                }
                if (mTextToProcess == null) {
            return;
        }
        populateWordSelectBox();

        HistoryUtil.savePopupOpen(mTextToProcess);
    }

    private void populateWordSelectBox() {
        List<String> localSegments = TextSplitter.getLocalSegments(mTextToProcess);
        for (String localSegment : localSegments) {
            bigBangLayout.addTextItem(localSegment);
        }
        ;
    }


    private void asyncSearch(final String word) {
        if (word.length() == 0) {
            showPronounce(false);
            return;
        }
        if(currentDicitonary == null){
            return;
        }
        mActContainer.setVisibility(View.VISIBLE);
        mDictAndDefContainer.setVisibility(View.VISIBLE);
        showProgressBar();
        showPronounce(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Your code goes here
                    Log.d("clicked", "yes");
                    List<Definition> d = currentDicitonary.wordLookup(word);
                    Message message = mHandler.obtainMessage();
                    message.obj = d;
                    message.what = PROCESS_DEFINITION_LIST;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    String error = e.getMessage();
                    Message message = mHandler.obtainMessage();
                    message.obj = error;
                    message.what = ASYNC_SEARCH_FAILED;
                    mHandler.sendMessage(message);
                }
            }
        });
        thread.start();
        HistoryUtil.saveWordlookup(mTextToProcess, word);
    }

    private void asyncTranslate(final String mTextToProcess){
        if(mTextToProcess == null) return;
        if(mTextToProcess.trim().equals("")) return;
        showTranslateLoading();
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String result;
                            if(RegexUtil.isChineseSentence(mTextToProcess)){
                                result = Translator.translate(mTextToProcess, "zh", "en");
                            }else {
                                result = Translator.translate(mTextToProcess, "auto", "zh");
                            }
                            Message message = mHandler.obtainMessage();
                            message.obj = result;
                            message.what = TRANSLATION_DONE;
                            mHandler.sendMessage(message);
                        }
                        catch(Exception e){
                            String error = e.getMessage();
                            Message message = mHandler.obtainMessage();
                            message.obj = error;
                            message.what = TRANSLATIOn_FAILED;
                            mHandler.sendMessage(message);
                        }
                    }
                }
        );
        thread.start();
    }

    private void setActAdapter(IDictionary dict) {
        SimpleCursorAdapter sca = (SimpleCursorAdapter) dict.getAutoCompleteAdapter(PopupActivity.this,
                android.R.layout.simple_spinner_dropdown_item);
        if (sca != null) {
            act.setAdapter(sca);
        }
    }

    //plan B
    private View getCardFromDefinition(final Definition def) {
        View view;
        if(settings.getLeftHandModeQ()){
            view = LayoutInflater.from(PopupActivity.this)
                    .inflate(R.layout.definition_item_left, null);
        }
        else{
            view = LayoutInflater.from(PopupActivity.this)
                    .inflate(R.layout.definition_item, null);
        }
        final TextView textVeiwDefinition = (TextView) view.findViewById(R.id.textview_definition);
        final CheckBox defCheckbox = (CheckBox) view.findViewById(R.id.def_checkbox);
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defCheckbox.toggle();
                    }
                }
        );
        //final Definition def = mDefinitionList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textVeiwDefinition.setText(Html.fromHtml(def.getDisplayHtml(), Html.FROM_HTML_MODE_COMPACT));
        }
        else{
            textVeiwDefinition.setText(Html.fromHtml(def.getDisplayHtml()));

        }
        if(mDefinitionList.size() == 1){
            defCheckbox.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private void setupEditNoteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PopupActivity.this);
        LayoutInflater inflater = PopupActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_note, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit_note);
        edt.setHorizontallyScrolling(false);
        edt.setMaxLines(4);
        edt.setText(mNoteEditedByUser);
        edt.setSelection(mNoteEditedByUser.length());
        dialogBuilder.setTitle(R.string.dialog_note);
        //dialogBuilder.setMessage("输入笔记");
        dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mNoteEditedByUser = edt.getText().toString();
            }
        });
//                        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                //pass
//                            }
//                        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setupEditTagDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PopupActivity.this);
        LayoutInflater inflater = PopupActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_tag, null);
        dialogBuilder.setView(dialogView);
        final AutoCompleteTextView editTag = (AutoCompleteTextView) dialogView.findViewById(R.id.edit_tag);
        final CheckBox checkBoxSetAsDefaultTag = (CheckBox) dialogView.findViewById(R.id.checkbox_as_default_tag);
        final ChipGroup tagChipGroup = (ChipGroup) dialogView.findViewById(R.id.tag_chip_list);
        editTag.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (mTagEditedByUser.size() > 0) {
            String text = Utils.fromTagSetToString(mTagEditedByUser);
            editTag.setText(text);
            editTag.setSelection(text.length());
        }
        tagChipGroup.setSingleSelection(false);
        //final List<UserTag> userTags = DataSupport.findAll(UserTag.class);
        final List<Tag> userTags = tagDao.queryBuilder().orderDesc(TagDao.Properties.LastUsedTime).list();
        for(Tag userTag : userTags){
            final Chip chip = (Chip) inflater.inflate(R.layout.tag_chip_item, null);
            chip.setText(userTag.getName());
            chip.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                mTagEditedByUser.add(chip.getText().toString());
                            }else{
                                mTagEditedByUser.remove(chip.getText().toString());
                            }
                            //tag1,tag2,tag3
                            String text = Utils.fromTagSetToString(mTagEditedByUser);
                            editTag.setText(text);
                            editTag.setSelection(text.length());
                        }
                    }
            );
            if(mTagEditedByUser.contains(chip.getText().toString())){
                chip.setChecked(true);
            }
            tagChipGroup.addView(chip);
        }

        boolean setDefaultQ = settings.getSetAsDefaultTag();
        checkBoxSetAsDefaultTag.setChecked(setDefaultQ);
        dialogBuilder.setTitle(R.string.dialog_tag);
        //dialogBuilder.setMessage("输入笔记");
        dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String tag = editTag.getText().toString().trim();
                if (tag.isEmpty()) {
                    if (checkBoxSetAsDefaultTag.isChecked()) {
                        mTagEditedByUser.clear();
                        Toast.makeText(PopupActivity.this, R.string.tag_cant_be_blank, Toast.LENGTH_LONG).show();
                    } else {
                        settings.setSetAsDefaultTag(false);
                        mTagEditedByUser.clear();
                    }
                    return;
                } else {
                    mTagEditedByUser = Utils.fromStringToTagSet(editTag.getText().toString());
                    settings.setSetAsDefaultTag(checkBoxSetAsDefaultTag.isChecked());
                    settings.setDefaultTag(editTag.getText().toString());
                    for (String t : mTagEditedByUser) {
                        boolean newOne = true;
                        for (Tag tagInDb : userTags) {
                            if (tagInDb.getName().equals(t)) {
                                newOne = false;
                                break;
                            }
                        }

                        if (newOne) {
                            Tag newTag = new Tag();
                            newTag.setName(t);
                            newTag.setId(System.currentTimeMillis());
                            newTag.setLastUsedTime(0);
                            tagDao.insert(newTag);
                        }
                    }
                }
            }
            }
        );
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    //cancel auto completetextview focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof AutoCompleteTextView) {
            View currentFocus = getCurrentFocus();
            int screenCoords[] = new int[2];
            currentFocus.getLocationOnScreen(screenCoords);
            float x = event.getRawX() + currentFocus.getLeft() - screenCoords[0];
            float y = event.getRawY() + currentFocus.getTop() - screenCoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < currentFocus.getLeft() ||
                    x >= currentFocus.getRight() ||
                    y < currentFocus.getTop() ||
                    y > currentFocus.getBottom())) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                v.clearFocus();
            }
        }
        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    private void startCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.GONE);
    }

    private void showSearchButton() {
        progressBar.setVisibility(View.GONE);
        btnSearch.setVisibility(View.VISIBLE);
    }

    private void showPronounce(boolean shouldShow) {
    }

    private void showTranslateNormal(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtnTranslation.setImageResource(Utils.getResIdFromAttribute(this, R.attr.icon_translate_normal));
        }
    }

    private void showTranslateLoading(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtnTranslation.setImageResource(Utils.getResIdFromAttribute(this, R.attr.icon_translate_wait));
        }
    }

    private void showTranslateDone(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtnTranslation.setImageResource(Utils.getResIdFromAttribute(this, R.attr.icon_translate_done));
        }
    }

    private void showTranslationCardView(boolean show){
        mCardViewTranslation.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSelected(String text) {
        String currentWord = FieldUtil.getSelectedText(bigBangLayout.getLines());
        if (!currentWord.equals(act.getText().toString())) {
            mCurrentKeyWord = currentWord;
            act.setText(currentWord);
            asyncSearch(currentWord);
        }
    }

    @Override
    public void onSearch(String text) {

    }

    @Override
    public void onShare(String text) {

    }

    @Override
    public void onCopy(String text) {

    }

    @Override
    public void onTrans(String text) {

    }

    @Override
    public void onDrag() {

    }

    @Override
    public void onSwitchType(boolean isLocal) {

    }

    @Override
    public void onSwitchSymbol(boolean isShow) {

    }

    @Override
    public void onSwitchSection(boolean isShow) {

    }

    @Override
    public void onDragSelection() {

    }

    @Override
    public void onCancel() {
        act.setText("");
        asyncSearch("");
    }

    void vibarate(int ms) {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(ms);
    }

    void clearBigbangSelection(){
        for (BigBangLayout.Line line : bigBangLayout.getLines()) {
            List<BigBangLayout.Item> items = line.getItems();
            for (BigBangLayout.Item item : items) {
                if (item.getText().equals(mTargetWord)) {
                    item.setSelected(false);
                }
            }
        }
    }

    private void makeTextViewSelectAndSearch(final TextView textView){
        textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "copy all" option
                menu.removeItem(android.R.id.cut);
                //menu.removeItem(android.R.id.copy);
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                menu.add(0, 1, 0, "Definition").setIcon(R.drawable.ic_ali_search);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Called when an action mode is about to be exited and
                // destroyed
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        int min = 0;
                        int max = textView.getText().length();
                        if (textView.isFocused()) {
                            final int selStart = textView.getSelectionStart();
                            final int selEnd = textView.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        // Perform your definition lookup with the selected text
                        final String selectedText = textView.getText().subSequence(min, max).toString();
                        // Finish and close the ActionMode
                        mode.finish();
                        act.setText(selectedText);
                        asyncSearch(selectedText);
                        return true;
                    default:
                        break;
                }
                return false;
            }

        });
    }
}
