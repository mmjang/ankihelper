package com.mmjang.ankihelper.ui.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.hotspot2.omadm.PpsMoParser;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.DragEvent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.folioreader.util.ProgressDialog;
import com.ichi2.anki.FlashCardsContract;
import com.ichi2.anki.api.NoteInfo;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.dict.BingImage;
import com.mmjang.ankihelper.data.dict.Definition;
import com.mmjang.ankihelper.data.dict.DictionaryDotCom;
import com.mmjang.ankihelper.data.dict.DictionaryRegister;
import com.mmjang.ankihelper.data.dict.Dub91Sentence;
import com.mmjang.ankihelper.data.dict.EudicSentence;
import com.mmjang.ankihelper.data.dict.IDictionary;
import com.mmjang.ankihelper.data.dict.RenRenCiDianSentence;
import com.mmjang.ankihelper.data.dict.UrbanAutoCompleteAdapter;
import com.mmjang.ankihelper.data.dict.VocabCom;
import com.mmjang.ankihelper.data.history.HistoryUtil;
import com.mmjang.ankihelper.data.model.UserTag;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.domain.CBWatcherService;
import com.mmjang.ankihelper.domain.PlayAudioManager;
import com.mmjang.ankihelper.domain.PronounceManager;
import com.mmjang.ankihelper.ui.LauncherActivity;
import com.mmjang.ankihelper.ui.plan.PlanEditorActivity;
import com.mmjang.ankihelper.ui.widget.BigBangLayout;
import com.mmjang.ankihelper.ui.widget.BigBangLayoutWrapper;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.FieldUtil;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.TextSplitter;
import com.mmjang.ankihelper.util.Translator;
import com.mmjang.ankihelper.util.Utils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Func;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.mmjang.ankihelper.util.FieldUtil.getBlankSentence;
import static com.mmjang.ankihelper.util.FieldUtil.getBoldSentence;
import static com.mmjang.ankihelper.util.FieldUtil.getNormalSentence;


public class PopupActivity extends Activity implements BigBangLayoutWrapper.ActionListener{

    List<IDictionary> dictionaryList;
    IDictionary currentDicitonary;
    List<OutputPlanPOJO> outputPlanList;
    List<String> languageList;
    OutputPlanPOJO currentOutputPlan;
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
    //possible specific note id to update
    Long mUpdateNoteId = 0L;
    //!!!!!!!!!!!important!!! boolean, if the plan spinner is during init, forbid asyncsearch;
    boolean isDuringPlanSpinnerInit = false;
    //update action   replace/append    append is the default action, to prevent data loss;
    String mUpdateAction;
    //possible bookmark id from fbreader
    String mFbReaderBookmarkId;
    //translation
    String mTranslatedResult = "";
    boolean needTranslation = false;
    //views
    AutoCompleteTextView act;
    Button btnSearch;
    ImageButton btnPronounce;
    Spinner planSpinner;
    Spinner pronounceLanguageSpinner;
    //RecyclerView recyclerViewDefinitionList;
    ImageButton mBtnEditNote;
    ImageButton mBtnEditTag;
    ImageButton mBtnTranslation;
    ImageButton mBtnFooterRotateLeft;
    ImageButton mBtnFooterRotateRight;
    ImageButton mBtnFooterScrollup;
    ProgressBar progressBar;
    ProgressBar mAudioProgress;

    CardView mCardViewTranslation;
    EditText mEditTextTranslation;
    //fab
    //FloatingActionButton mFab;
    ScrollView scrollView;
    //plan b
    LinearLayout viewDefinitionList;
    List<Definition> mDefinitionList;
    //media
    MediaPlayer mMediaPlayer;
    //downloader
    Fetch fetch;
    boolean isFetchDownloading = false;
    //async event
    private static final int PROCESS_DEFINITION_LIST = 1;
    private static final int ASYNC_SEARCH_FAILED = 2;
    private static final int TRANSLATION_DONE = 3;
    private static final int TRANSLATIOn_FAILED = 4;

    //view tag
    private static final int TAG_NOTE_ID_LONG = 5;
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
        setContentView(R.layout.activity_popup);
//        getActionBar().hide();
        //set animation
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        //
        assignViews();
        initBigBangLayout();
        loadData(); //dictionaryList;
        populatePlanSpinner();
        populateLanguageSpinner();
        setEventListener();
        handleIntent();
        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

        bigBangLayout.post( new Runnable() {
            @Override
            public void run() {
                setTargetWord();
                if(Utils.containsTranslationField(currentOutputPlan)){
                    asyncTranslate(mTextToProcess);
                }
            }
        });

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
            if(mTextToProcess.matches("[a-zA-Z\\-]*")){
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
        act = (AutoCompleteTextView) findViewById(R.id.edit_text_hwd);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnPronounce = ((ImageButton) findViewById(R.id.btn_pronounce));
        planSpinner = (Spinner) findViewById(R.id.plan_spinner);
        pronounceLanguageSpinner = (Spinner) findViewById(R.id.language_spinner);
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
        mBtnFooterRotateLeft = (ImageButton) findViewById(R.id.footer_rotate_left);
        mBtnFooterRotateRight= (ImageButton) findViewById(R.id.footer_rotate_right);
        mBtnFooterScrollup = (ImageButton) findViewById(R.id.footer_scroll_up);
        mAudioProgress = findViewById(R.id.audio_progress);
    }

    private void loadData() {
        dictionaryList = DictionaryRegister.getDictionaryObjectList();
        outputPlanList = ExternalDatabase.getInstance().getAllPlan();
        settings = Settings.getInstance(this);
        //load tag
        boolean loadQ = settings.getSetAsDefaultTag();
        if (loadQ) {
            mTagEditedByUser = Utils.fromStringToTagSet(settings.getDefaulTag());
        }
        //check if outputPlanList is empty
        if(outputPlanList.size() == 0){
            //Toast.makeText(this, , Toast.LENGTH_LONG).show();
            Utils.showMessage(this, getResources().getString(R.string.toast_no_available_plan));
        }
    }

    private void populatePlanSpinner() {
        if(outputPlanList.size() == 0){
            return;
        }
        final String[] planNameArr = new String[outputPlanList.size()];
        for (int i = 0; i < outputPlanList.size(); i++) {
            planNameArr[i] = outputPlanList.get(i).getPlanName();
        }
        ArrayAdapter<String> planSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, planNameArr);
        planSpinner.setAdapter(planSpinnerAdapter);
        planSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set plan to last selected plan
        String lastSelectedPlan = settings.getLastSelectedPlan();
        if (lastSelectedPlan.equals("")) //first use, set default plan to first one if any
        {
            if (outputPlanList.size() > 0) {
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                if(currentDicitonary == null){
                    String message = String.format("方案\"%s\"所选词典\"%s\"不存在，请检查是否需要重新导入自定义词典",
                            currentOutputPlan.getPlanName(),
                            currentOutputPlan.getDictionaryKey());
                    Utils.showMessage(PopupActivity.this, message);
                }else {
                    setActAdapter(currentDicitonary);
                }
            } else {
                return ;
            }
        }

        ///////////////if user add intent parameter to control which plan to use
        mPlanNameFromIntent = getIntent().getStringExtra(Constant.INTENT_ANKIHELPER_PLAN_NAME);
        if(mPlanNameFromIntent != null){
            lastSelectedPlan = mPlanNameFromIntent;
        }
        ///////////////
        int i = 0;
        boolean find = false;
        for (OutputPlanPOJO plan : outputPlanList) {
            if (plan.getPlanName().equals(lastSelectedPlan)) {
                isDuringPlanSpinnerInit = true;
                planSpinner.setSelection(i);
                currentOutputPlan = outputPlanList.get(i);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                if(currentDicitonary == null) {
                    String message = String.format("方案\"%s\"所选词典\"%s\"不存在，请检查是否需要重新导入自定义词典",
                            currentOutputPlan.getPlanName(),
                            currentOutputPlan.getDictionaryKey());
                    Utils.showMessage(PopupActivity.this, message);
                    break;
                }
                setActAdapter(currentDicitonary);
                find = true;
                break;
            }
            //if not equal, compare next
            i++;
        }
        if (!find) //if the saved last plan no longer in the plan list, reset to first one
        {
            if (outputPlanList.size() > 0) {
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                if(currentDicitonary == null) {
                    String message = String.format("方案\"%s\"所选词典\"%s\"不存在，请检查是否需要重新导入自定义词典",
                            currentOutputPlan.getPlanName(),
                            currentOutputPlan.getDictionaryKey());
                    Utils.showMessage(PopupActivity.this, message);
                }
                setActAdapter(currentDicitonary);
            }
        } else {
            //if find, then current plan and dictionary must have been set above.
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            scrollView.setOnScrollChangeListener(
//                    new View.OnScrollChangeListener() {
//                        @Override
//                        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                            if(i1 > i3){
//                                mFab.hide();
//                            }else{
//                                mFab.show();
//                                //mFab.setAlpha(Constant.FLOAT_ACTION_BUTTON_ALPHA);
//                            }
//                        }
//                    }
//            );
//        }
    }

    private void populateLanguageSpinner() {

        String[] languages = PronounceManager.getAvailablePronounceLanguage();
        ArrayAdapter<String> languagesSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, languages);
        languagesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pronounceLanguageSpinner.setAdapter(languagesSpinnerAdapter);
        int lastPronounceLanguageIndex = settings.getLastPronounceLanguage();
        pronounceLanguageSpinner.setSelection(lastPronounceLanguageIndex);

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

        //auto finish
        Button btnCancelBlank = (Button) findViewById(R.id.btn_cancel_blank);
        Button btnCancelBlankAboveCard = (Button) findViewById(R.id.btn_cancel_blank_above_card);
        btnCancelBlank.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
        btnCancelBlankAboveCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        planSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentOutputPlan = outputPlanList.get(position);
                        currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                        setActAdapter(currentDicitonary);
                        //memorise last selected plan
                        settings.setLastSelectedPlan(currentOutputPlan.getPlanName());
                        String actContent = act.getText().toString();

                        if(isDuringPlanSpinnerInit){
                            isDuringPlanSpinnerInit = false;
                        }else {
                            if(!actContent.trim().isEmpty()) {
                                asyncSearch(actContent);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        pronounceLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                settings.setLastPronounceLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        btnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String word = act.getText().toString();
                PlayAudioManager.playPronounceVoice(PopupActivity.this, word);
            }
        });

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
        mBtnFooterRotateRight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mPlanSize = outputPlanList.size();
                        int currentPos = planSpinner.getSelectedItemPosition();
                        if(mPlanSize > 1){
                            if(currentPos < mPlanSize - 1){
                                planSpinner.setSelection(currentPos + 1);
                            }
                            else if(currentPos == mPlanSize - 1){
                                planSpinner.setSelection(0);
                            }
                            //vibarate(Constant.VIBRATE_DURATION);
                            //scrollView.fullScroll(ScrollView.FOCUS_UP);
                        }else{
                            Toast.makeText(PopupActivity.this, R.string.str_only_one_plan_cant_switch, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        mBtnFooterRotateLeft.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mPlanSize = outputPlanList.size();
                        int currentPos = planSpinner.getSelectedItemPosition();
                        if(mPlanSize > 1){
                            if(currentPos > 0){
                                planSpinner.setSelection(currentPos - 1);
                            }
                            else if(currentPos == 0){
                                planSpinner.setSelection(mPlanSize - 1);
                            }
                        //    vibarate(Constant.VIBRATE_DURATION);
                            //scrollView.fullScroll(ScrollView.FOCUS_UP);
                        }else{
                            Toast.makeText(PopupActivity.this, R.string.str_only_one_plan_cant_switch, Toast.LENGTH_SHORT).show();
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

    private IDictionary getDictionaryFromOutputPlan(OutputPlanPOJO outputPlan) {
        String dictionaryName = outputPlan.getDictionaryKey();
        for (IDictionary dict : dictionaryList) {
            if (dict.getDictionaryName().equals(dictionaryName)) {
                return dict;
            }
        }
        return null;
    }

    private void processDefinitionList(List<Definition> definitionList) {
        if (definitionList.isEmpty()) {
            Toast.makeText(this, R.string.definition_not_found, Toast.LENGTH_SHORT).show();
        } else {
//            DefinitionAdapter defAdapter = new DefinitionAdapter(PopupActivity.this, definitionList, mTextSplitter, currentOutputPlan);
//            LinearLayoutManager llm = new LinearLayoutManager(this);
//            //llm.setAutoMeasureEnabled(true);
//            recyclerViewDefinitionList.setLayoutManager(llm);
//            //recyclerViewDefinitionList.getRecycledViewPool().setMaxRecycledViews(0,0);
//            //recyclerViewDefinitionList.setHasFixedSize(true);
//            //recyclerViewDefinitionList.setNestedScrollingEnabled(false);
//            recyclerViewDefinitionList.setAdapter(defAdapter);
            viewDefinitionList.removeAllViewsInLayout();
            for (Definition def : definitionList) {
                viewDefinitionList.addView(getCardFromDefinition(def));
            }
            viewDefinitionList.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            if(scrollView.getScrollY() > 10) {
                                //scrollView.fullScroll(ScrollView.FOCUS_UP);
                            }
                        }
                    }
            );
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
            String base64 = intent.getStringExtra(Constant.INTENT_ANKIHELPER_BASE64);
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_TEXT);
            if(base64 != null && !base64.equals("0")){
                mTextToProcess = new String(Base64.decode(mTextToProcess, Base64.DEFAULT));
            }
            mTargetWord = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD);
            mUrl = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_URL);
            //mFbReaderBookmarkId = intent.getStringExtra(Constant.INTENT_ANKIHELPER_FBREADER_BOOKMARK_ID);
            String noteEditedByUser = intent.getStringExtra(Constant.INTENT_ANKIHELPER_NOTE);
            if(noteEditedByUser != null){
                mNoteEditedByUser = noteEditedByUser;
            }
            String updateId = intent.getStringExtra(Constant.INTENT_ANKIHELPER_NOTE_ID);
            mUpdateAction = intent.getStringExtra(Constant.INTENT_ANKIHELPER_UPDATE_ACTION);
            if(updateId != null && !updateId.isEmpty())
            {
                    try{
                        mUpdateNoteId = Long.parseLong(updateId);
                        if(mUpdateNoteId > 0){
                            mTagEditedByUser =
                                    MyApplication.getAnkiDroid()
                                            .getApi().getNote(mUpdateNoteId)
                                            .getTags();
                        }
                    }
                    catch(Exception e){

                    }
        }
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
        if(currentDicitonary == null || currentOutputPlan == null){
            return;
        }
        showProgressBar();
        progressBar.invalidate();
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
        Object adapter = dict.getAutoCompleteAdapter(PopupActivity.this,
                android.R.layout.simple_spinner_dropdown_item);
        if(adapter != null){
            if(adapter instanceof SimpleCursorAdapter){
                act.setAdapter((SimpleCursorAdapter) adapter);
            }
            else if(adapter instanceof UrbanAutoCompleteAdapter){
                act.setAdapter((UrbanAutoCompleteAdapter) adapter);
            }
        }
        act.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus){
                            if(act.getText().toString().trim().isEmpty()){
                                return;
                            }
                            act.showDropDown();
                        }
                    }
                }
        );
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
        //toggle fab with clicks
//        view.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(mFab.getVisibility() == View.VISIBLE){
//                            mFab.hide();
//                        }else{
//                            mFab.show();
//                        }
//                    }
//                }
//        );
        final TextView textVeiwDefinition = (TextView) view.findViewById(R.id.textview_definition);
        final ImageButton btnAddDefinition = (ImageButton) view.findViewById(R.id.btn_add_definition);
        final LinearLayout btnAddDefinitionLarge = (LinearLayout) view.findViewById(R.id.btn_add_definition_large);
        final ImageView defImage = view.findViewById(R.id.def_img);
        btnAddDefinitionLarge.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnAddDefinition.callOnClick();
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

        if(def.getDisplayHtml().isEmpty()){
            textVeiwDefinition.setVisibility(View.GONE);
        }

        if(def.getImageUrl()!=null && !def.getImageUrl().isEmpty()){
            Glide.with(this).load(def.getImageUrl()).into(defImage);
            defImage.setVisibility(View.VISIBLE);
        }

        if((currentDicitonary instanceof EudicSentence ||
                currentDicitonary instanceof RenRenCiDianSentence) && def.getAudioUrl()!=null && !def.getAudioUrl().isEmpty()){
            textVeiwDefinition.setTextIsSelectable(false);
            textVeiwDefinition.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mMediaPlayer == null) {
                                mMediaPlayer = new MediaPlayer();
                                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mMediaPlayer.setOnPreparedListener(
                                        new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mp) {
                                                mMediaPlayer.start();
                                                mAudioProgress.setVisibility(View.GONE);
                                            }
                                        }
                                );
                            }
                            try {
                                if(mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.reset();
                                    //mMediaPlayer.release();
                                }
                            }catch(IllegalStateException e){

                            }
                            try {
                                mMediaPlayer.setDataSource(PopupActivity.this, Uri.parse(def.getAudioUrl()));
                                mAudioProgress.setVisibility(View.VISIBLE);
                                mMediaPlayer.prepareAsync();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(PopupActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                            } catch (IllegalStateException e){

                            }
//                            mMediaPlayer.setOnPreparedListener(
//                                    new MediaPlayer.OnPreparedListener() {
//                                        @Override
//                                        public void onPrepared(MediaPlayer mp) {
//                                        }
//                                    }
//                            );

                            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.reset();
                                    mAudioProgress.setVisibility(View.GONE);
                                }
                            });

                            mMediaPlayer.setOnErrorListener(
                                    new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mp, int what, int extra) {
                                            mp.reset();
                                            Toast.makeText(PopupActivity.this, "Failed to play audio, check your connection.", Toast.LENGTH_SHORT);
                                            mAudioProgress.setVisibility(View.GONE);
                                            return false;
                                        }
                                    }
                            );
//                            if(mMediaPlayer == null){
//                                mMediaPlayer = new MediaPlayer();
//                            }
//
//                            try {
//                                if(mMediaPlayer.isPlaying()) {
//                                    mMediaPlayer.reset();
//                                    //mMediaPlayer.release();
//                                }
//                            }catch(IllegalStateException e){
//
//                            }
//                            try {
//                                Toast.makeText(PopupActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
//                                mMediaPlayer.setDataSource(PopupActivity.this, Uri.parse(def.getAudioUrl()));
//                                mMediaPlayer.prepare();
//                                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                    @Override
//                                    public void onCompletion(MediaPlayer mp) {
//                                        mMediaPlayer.reset();
//                                        //mMediaPlayer.release();
//                                    }
//                                });
//                                mMediaPlayer.start();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Toast.makeText(PopupActivity.this, "Failed to play audio", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
            );
        }

        //set custom action for the textView
        makeTextViewSelectAndSearch(textVeiwDefinition);
        //holder.itemView.setAnimation(AnimationUtils.loadAnimation(mActivity, android.R.anim.fade_in));
        //holder.textVeiwDefinition.setTextColor(Color.BLACK);
        btnAddDefinition.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //vibarate(Constant.VIBRATE_DURATION);
                        //before add, check if this note is already added by check the attached tag
                        Long noteIdAdded = (Long) btnAddDefinition.getTag(R.id.TAG_NOTE_ID);
                        if(noteIdAdded != null){
                            if(mUpdateNoteId == 0) {
                                if(Utils.deleteNote(PopupActivity.this, noteIdAdded.longValue())){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                                PopupActivity.this,
                                                Utils.getResIdFromAttribute(PopupActivity.this, R.attr.icon_add)));
                                    }
                                    btnAddDefinition.setTag(R.id.TAG_NOTE_ID, null);
                                    Toast.makeText(PopupActivity.this, R.string.str_cancel_note_add, Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(PopupActivity.this, R.string.error_note_cancel, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(PopupActivity.this, R.string.str_not_cancelable_append_mode, Toast.LENGTH_SHORT).show();
                            }
                            return ;
                        }

                        //save image
                        if(def.getImageUrl()!=null && !def.getImageUrl().isEmpty()){
                            if(defImage.getDrawable()!=null &&
                                    (currentDicitonary instanceof BingImage ||
                                            currentDicitonary instanceof RenRenCiDianSentence ||
                                            currentDicitonary instanceof Dub91Sentence)){
                                BitmapDrawable drawable = (BitmapDrawable) defImage.getDrawable();
                                Bitmap bm = drawable.getBitmap();

                                OutputStream fOut = null;
                                //Uri outputFileUri;
                                try {
                                    File root = new File(Constant.IMAGE_MEDIA_DIRECTORY);
                                    if(!root.exists()) {
                                        root.mkdirs();
                                    }
                                    File sdImageMainDirectory = new File(root, def.getImageName());
                                    //outputFileUri = Uri.fromFile(sdImageMainDirectory);
                                    fOut = new FileOutputStream(sdImageMainDirectory);
                                } catch (Exception e) {

                                }
                                try {
                                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                    fOut.flush();
                                    fOut.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ///////////////////////////////////

                        AnkiDroidHelper mAnkiDroid = MyApplication.getAnkiDroid();
                        String[] sharedExportElements = Constant.getSharedExportElements();
                        String[] exportFields = new String[currentOutputPlan.getFieldsMap().size()];
                        int i = 0;
                        Map<String, String> map = currentOutputPlan.getFieldsMap();
                        for (String exportedFieldKey : currentOutputPlan.getFieldsMap().values()) {
                            if (exportedFieldKey.equals(sharedExportElements[0])) {
                                exportFields[i] = "";
                                i++;
                                continue;
                            }

                            if (exportedFieldKey.equals(sharedExportElements[1])) {
                                exportFields[i] = getNormalSentence(bigBangLayout.getLines());
                                i++;
                                continue;
                            }

                            if (exportedFieldKey.equals(sharedExportElements[2])) {
                                exportFields[i] = getBoldSentence(bigBangLayout.getLines());
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[3])) {
                                exportFields[i] = getBlankSentence(bigBangLayout.getLines());
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[4])) {
                                exportFields[i] = mNoteEditedByUser;
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[5])) {
                                exportFields[i] = mUrl;
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[6])){
                                exportFields[i] = Utils.getAllHtmlFromDefinitionList(mDefinitionList);
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[7])){
                                exportFields[i] = mEditTextTranslation.getText().toString().replace("\n", "<br/>");
                                i++;
                                continue;
                            }
//                            if(exportedFieldKey.equals(sharedExportElements[5])){
//                                if(mFbReaderBookmarkId != null){
//                                    exportFields[i] = String.format(Constant.FBREADER_URL_TMPL, mFbReaderBookmarkId);
//                                }else{
//                                    exportFields[i]="";
//                                }
//                                i++;
//                                continue;
//                            }
                            if (def.hasElement(exportedFieldKey)) {
                                exportFields[i] = def.getExportElement(exportedFieldKey);
                                i++;
                                continue;
                            }

                            exportFields[i] = "";
                            i++;
                        }
                        //handle download; audio or image
                        if(currentDicitonary instanceof EudicSentence || currentDicitonary instanceof RenRenCiDianSentence){
                            if(fetch == null){
                                initFetch();
                            }
                            if(map.containsValue("原声例句")){
                                final Request request = new Request(def.getAudioUrl(), Constant.AUDIO_MEDIA_DIRECTORY + def.getAudioName());
                                request.setPriority(Priority.HIGH);
                                request.setNetworkType(NetworkType.ALL);
                                isFetchDownloading = true;
                                fetch.enqueue(request,
                                        new Func<Request>() {
                                            @Override
                                            public void call(@NotNull Request result) {
                                                mAudioProgress.setVisibility(View.VISIBLE);
                //                                isFetchDownloading = true;
                                            }
                                        }
                                        ,
                                        new Func<Error>() {
                                            @Override
                                            public void call(@NotNull Error result) {
                                                isFetchDownloading = false;
                                            }
                                        }
                                );
                            }
                        }

                        if(currentDicitonary instanceof VocabCom){
                            if(fetch == null){
                                initFetch();
                            }
                            if(map.containsValue("离线发音")){
                                final Request request = new Request(def.getAudioUrl(), Constant.AUDIO_MEDIA_DIRECTORY + def.getAudioName());
                                request.setPriority(Priority.HIGH);
                                request.setNetworkType(NetworkType.ALL);
                                isFetchDownloading = true;
                                fetch.enqueue(request,
                                        new Func<Request>() {
                                            @Override
                                            public void call(@NotNull Request result) {
                                                mAudioProgress.setVisibility(View.VISIBLE);
                                                isFetchDownloading = true;
                                            }
                                        }
                                        ,
                                        new Func<Error>() {
                                            @Override
                                            public void call(@NotNull Error result) {
                                                isFetchDownloading = false;
                                            }
                                        }
                                );
                            }
                        }
                        /////////////////
                        long deckId = currentOutputPlan.getOutputDeckId();
                        long modelId = currentOutputPlan.getOutputModelId();
                        if(mUpdateNoteId == 0){
                            Long result = mAnkiDroid.getApi().addNote(modelId, deckId, exportFields, mTagEditedByUser);
                            if (result != null) {
                                Toast.makeText(PopupActivity.this, R.string.str_added, Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                            PopupActivity.this, Utils.getResIdFromAttribute(PopupActivity.this, R.attr.icon_add_done)));
                                }
                                clearBigbangSelection();
                                mNoteEditedByUser = "";
                                //attach the noteid to the button
                                btnAddDefinition.setTag(R.id.TAG_NOTE_ID, result);
                                //if there is a note id field in the model, update the note
                                int count = 0;
                                for(String field : currentOutputPlan.getFieldsMap().keySet()){
                                    if(field.replace(" ","").toLowerCase().equals("noteid")){
                                        exportFields[count] = result.toString();
                                        boolean success = mAnkiDroid.getApi().updateNoteFields(
                                                result.longValue(),
                                                exportFields
                                        );
                                        if(!success){
                                            Toast.makeText(PopupActivity.this, R.string.str_error_noteid, Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    }
                                    count ++;
                                }
                                //save note add
                                HistoryUtil.saveNoteAdd("", getBoldSentence(bigBangLayout.getLines()),
                                    currentDicitonary.getDictionaryName(),
                                    textVeiwDefinition.getText().toString(),
                                    mTranslatedResult,
                                    mNoteEditedByUser,
                                        mTagEditedByUser.toString()
                                );
                            } else {
                                Toast.makeText(PopupActivity.this, R.string.str_failed_add, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{//there's note id, so we need to retrieve note first
                            NoteInfo note = mAnkiDroid.getApi().getNote(mUpdateNoteId);
                            String[] original = note.getFields();
                            Set<String> tags = note.getTags();
                            if(original == null || original.length != exportFields.length){
                                Toast.makeText(PopupActivity.this, R.string.str_error_notetype_noncompatible, Toast.LENGTH_SHORT).show();
                                return ;
                            }

                            if(mUpdateAction != null && mUpdateAction.equals("replace")) {
                                //replace
                                for (int j = 0; j < original.length; j++) {
                                    if (exportFields[j].isEmpty()) {
                                        exportFields[j] = original[j];
                                    }
                                }

                            }
                            else {
                                //append
                                for (int j = 0; j < original.length; j++) {
                                    if (original[j].trim().isEmpty() || exportFields[j].trim().isEmpty()) {
                                        exportFields[j] = original[j] + exportFields[j];
                                    } else {
                                        exportFields[j] = original[j] + "<br/>" + exportFields[j];
                                    }
                                }
                            }
                            //we need to check the tag used by user is already in the tags, if not, add it
                            tags.addAll(mTagEditedByUser);
                            boolean success = mAnkiDroid.getApi().updateNoteFields(mUpdateNoteId, exportFields);
                            boolean successTag = mAnkiDroid.getApi().updateNoteTags(mUpdateNoteId, tags);
                            if (success && successTag) {
                                Toast.makeText(PopupActivity.this, R.string.str_note_updated, Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                            PopupActivity.this, Utils.getResIdFromAttribute(PopupActivity.this, R.attr.icon_add_done)));
                                }
                                //btnAddDefinition.setEnabled(false);
                            } else {
                                Toast.makeText(PopupActivity.this, R.string.str_error_note_update, Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(settings.getAutoCancelPopupQ()) {
                            if(fetch == null)
                            {
                                finish();
                            }else{
                                if(!isFetchDownloading){
                                    finish();
                                }
                            }
                        }
                    }
                });
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
        final List<UserTag> userTags = DataSupport.findAll(UserTag.class);
        for(UserTag userTag : userTags){
            final Chip chip = (Chip) inflater.inflate(R.layout.tag_chip_item, null);
            chip.setText(userTag.getTag());
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
//        String[] arr = new String[userTags.size()];
//        for (int i = 0; i < userTags.size(); i++) {
//            arr[i] = userTags.get(i).getTag();
//        }
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PopupActivity.this,
//                R.layout.support_simple_spinner_dropdown_item, arr);
//        editTag.setAdapter(arrayAdapter);
//        editTag.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (editTag.getText().toString().isEmpty()) {
//                    editTag.showDropDown();
//                }
//                return false;
//            }
//        });
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
                    for(String t : mTagEditedByUser){
                        if(!userTags.contains(t)){ //add new tag
                            UserTag userTag = new UserTag(t);
                            userTag.save();
                        }
                    }
                }

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void initFetch(){
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch.addListener(
                new FetchListener() {
                    @Override
                    public void onAdded(@NotNull Download download) {

                    }

                    @Override
                    public void onQueued(@NotNull Download download, boolean b) {

                    }

                    @Override
                    public void onWaitingNetwork(@NotNull Download download) {

                    }

                    @Override
                    public void onCompleted(@NotNull Download download) {
                        Toast.makeText(PopupActivity.this, "Download Completed!", Toast.LENGTH_SHORT).show();
                        mAudioProgress.setVisibility(View.GONE);
                        isFetchDownloading = false;
                        if(settings.getAutoCancelPopupQ()) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
                        Toast.makeText(PopupActivity.this, "Download Failed!", Toast.LENGTH_SHORT).show();
                        mAudioProgress.setVisibility(View.GONE);
                        isFetchDownloading = false;
                        if(settings.getAutoCancelPopupQ()) {
                            finish();
                        }
                    }

                    @Override
                    public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

                    }

                    @Override
                    public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

                    }

                    @Override
                    public void onProgress(@NotNull Download download, long l, long l1) {

                    }

                    @Override
                    public void onPaused(@NotNull Download download) {

                    }

                    @Override
                    public void onResumed(@NotNull Download download) {

                    }

                    @Override
                    public void onCancelled(@NotNull Download download) {

                    }

                    @Override
                    public void onRemoved(@NotNull Download download) {

                    }

                    @Override
                    public void onDeleted(@NotNull Download download) {

                    }
                }
        );
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
        btnPronounce.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
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
