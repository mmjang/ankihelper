package com.mmjang.ankihelper.ui.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.dict.Definition;
import com.mmjang.ankihelper.data.dict.DictionaryRegister;
import com.mmjang.ankihelper.data.dict.IDictionary;
import com.mmjang.ankihelper.data.model.UserTag;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.domain.CBWatcherService;
import com.mmjang.ankihelper.domain.PlayAudioManager;
import com.mmjang.ankihelper.domain.PronounceManager;
import com.mmjang.ankihelper.ui.widget.BigBangLayout;
import com.mmjang.ankihelper.ui.widget.BigBangLayoutWrapper;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.FieldUtil;
import com.mmjang.ankihelper.util.TextSplitter;
import com.mmjang.ankihelper.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.mmjang.ankihelper.util.FieldUtil.getBlankSentence;
import static com.mmjang.ankihelper.util.FieldUtil.getBoldSentence;


public class PopupActivity extends Activity implements BigBangLayoutWrapper.ActionListener{

    List<IDictionary> dictionaryList;
    IDictionary currentDicitonary;
    List<OutputPlan> outputPlanList;
    List<String> languageList;
    OutputPlan currentOutputPlan;
    Settings settings;
    String mTextToProcess;
    String mPlanNameFromIntent;
    String mCurrentKeyWord;
    TextSplitter mTextSplitter;
    String mNoteEditedByUser = "";
    HashSet<String> mTagEditedByUser = new HashSet<>();
    //posiblle pre set target word
    String mTargetWord;
    //possible url from dedicated borwser
    String mUrl = "";
    //possible specific note id to update
    Long mUpdateNoteId = 0L;
    //update action   replace/append    append is the default action, to prevent data loss;
    String mUpdateAction;
    //possible bookmark id from fbreader
    String mFbReaderBookmarkId;
    //views
    AutoCompleteTextView act;
    Button btnSearch;
    Button btnPronounce;
    Spinner planSpinner;
    Spinner pronounceLanguageSpinner;
    //RecyclerView recyclerViewDefinitionList;
    ImageButton mBtnEditNote;
    ImageButton mBtnEditTag;
    ProgressBar progressBar;
    //plan b
    LinearLayout viewDefinitionList;
    //async event
    private static final int PROCESS_DEFINITION_LIST = 1;
    private static final int ASYNC_SEARCH_FAILED = 2;
    //async
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESS_DEFINITION_LIST:
                    showSearchButton();
                    List<Definition> definitionList = (List<Definition>) msg.obj;
                    processDefinitionList(definitionList);
                    break;
                case ASYNC_SEARCH_FAILED:
                    showSearchButton();
                    Toast.makeText(PopupActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    private BigBangLayout bigBangLayout;
    private BigBangLayoutWrapper bigBangLayoutWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_popup);
        //set animation
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
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
        btnPronounce = ((Button) findViewById(R.id.btn_pronounce));
        planSpinner = (Spinner) findViewById(R.id.plan_spinner);
        pronounceLanguageSpinner = (Spinner) findViewById(R.id.language_spinner);
        //recyclerViewDefinitionList = (RecyclerView) findViewById(R.id.recycler_view_definition_list);
        viewDefinitionList = (LinearLayout) findViewById(R.id.view_definition_list);
        mBtnEditNote = (ImageButton) findViewById(R.id.btn_edit_note);
        mBtnEditTag = (ImageButton) findViewById(R.id.btn_edit_tag);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        bigBangLayout = (BigBangLayout) findViewById(R.id.bigbang);
        bigBangLayoutWrapper = (BigBangLayoutWrapper) findViewById(R.id.bigbang_wrapper);
    }

    private void loadData() {
        dictionaryList = DictionaryRegister.getDictionaryObjectList();
        outputPlanList = DataSupport.findAll(OutputPlan.class);
        settings = Settings.getInstance(this);
        //load tag
        boolean loadQ = settings.getSetAsDefaultTag();
        if (loadQ) {
            mTagEditedByUser.add(settings.getDefaulTag());
        }
        //check if outputPlanList is empty
        if(outputPlanList.size() == 0){
            Toast.makeText(this, R.string.toast_no_available_plan, Toast.LENGTH_LONG).show();
        }
    }

    private void populatePlanSpinner() {
        String[] planNameArr = new String[outputPlanList.size()];
        for (int i = 0; i < outputPlanList.size(); i++) {
            planNameArr[i] = outputPlanList.get(i).getPlanName();
        }
        ArrayAdapter<String> planSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, planNameArr);
        planSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planSpinner.setAdapter(planSpinnerAdapter);
        //set plan to last selected plan
        String lastSelectedPlan = settings.getLastSelectedPlan();
        if (lastSelectedPlan.equals("")) //first use, set default plan to first one if any
        {
            if (outputPlanList.size() > 0) {
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                setActAdapter(currentDicitonary);
            } else {
                //no outputplan available!!!
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
        for (OutputPlan plan : outputPlanList) {
            if (plan.getPlanName().equals(lastSelectedPlan)) {
                planSpinner.setSelection(i);
                currentOutputPlan = outputPlanList.get(i);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
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
                setActAdapter(currentDicitonary);
            }
        } else {
            //if find, then current plan and dictionary must have been set above.
        }
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
                        if(!actContent.trim().isEmpty()) {
                            asyncSearch(actContent);
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
    }

    private IDictionary getDictionaryFromOutputPlan(OutputPlan outputPlan) {
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
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_TEXT);
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
        final ImageButton btnAddDefinition = (ImageButton) view.findViewById(R.id.btn_add_definition);
        final LinearLayout btnAddDefinitionLarge = (LinearLayout) view.findViewById(R.id.btn_add_definition_large);
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
        //holder.itemView.setAnimation(AnimationUtils.loadAnimation(mActivity, android.R.anim.fade_in));
        //holder.textVeiwDefinition.setTextColor(Color.BLACK);
        btnAddDefinition.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibarate(Constant.VIBRATE_DURATION);
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
                                exportFields[i] = getBoldSentence(bigBangLayout.getLines());
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[2])) {
                                exportFields[i] = getBlankSentence(bigBangLayout.getLines());
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[3])) {
                                exportFields[i] = mNoteEditedByUser;
                                i++;
                                continue;
                            }
                            if (exportedFieldKey.equals(sharedExportElements[4])) {
                                exportFields[i] = mUrl;
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
                        long deckId = currentOutputPlan.getOutputDeckId();
                        long modelId = currentOutputPlan.getOutputModelId();
                        if(mUpdateNoteId == 0){
                            Long result = mAnkiDroid.getApi().addNote(modelId, deckId, exportFields, mTagEditedByUser);
                            if (result != null) {
                                Toast.makeText(PopupActivity.this, R.string.str_added, Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                            PopupActivity.this, R.drawable.ic_add_grey));
                                }
                                btnAddDefinition.setEnabled(false);
                                if (settings.getAutoCancelPopupQ()) {
                                    finish();
                                }
                            } else {
                                Toast.makeText(PopupActivity.this, R.string.str_failed_add, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            String[] original = mAnkiDroid.getApi().getNote(mUpdateNoteId).getFields();
                            if(original == null || original.length != exportFields.length){
                                Toast.makeText(PopupActivity.this, "note failed to update. Note type not compatible", Toast.LENGTH_SHORT).show();
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
                                    for(int j = 0; j < original.length; j++){
                                        if(original[j].trim().isEmpty() || exportFields[j].trim().isEmpty()) {
                                            exportFields[j] = original[j] + exportFields[j];
                                        }else{
                                            exportFields[j] = original[j] + "<br/>" + exportFields[j];
                                        }
                                    }
                            }

                            boolean success = mAnkiDroid.getApi().updateNoteFields(mUpdateNoteId, exportFields);
                            boolean successTag = mAnkiDroid.getApi().updateNoteTags(mUpdateNoteId, mTagEditedByUser);
                            if (success && successTag) {
                                Toast.makeText(PopupActivity.this, "note updated!", Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                            PopupActivity.this, R.drawable.ic_add_grey));
                                }
                                btnAddDefinition.setEnabled(false);
                                if(settings.getAutoCancelPopupQ()) {
                                    finish();
                                }
                            } else {
                                Toast.makeText(PopupActivity.this, "note failed to update!", Toast.LENGTH_SHORT).show();
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
        editTag.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (mTagEditedByUser.size() == 1) {
            String text = (String) mTagEditedByUser.toArray()[0];
            editTag.setText(text);
            editTag.setSelection(text.length());
        }
        List<UserTag> userTags = DataSupport.findAll(UserTag.class);
        String[] arr = new String[userTags.size()];
        for (int i = 0; i < userTags.size(); i++) {
            arr[i] = userTags.get(i).getTag();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PopupActivity.this,
                R.layout.support_simple_spinner_dropdown_item, arr);
        editTag.setAdapter(arrayAdapter);
        editTag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (editTag.getText().toString().isEmpty()) {
                    editTag.showDropDown();
                }
                return false;
            }
        });
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
                    mTagEditedByUser.clear();
                    mTagEditedByUser.add(tag);
                    settings.setSetAsDefaultTag(checkBoxSetAsDefaultTag.isChecked());
                    settings.setDefaultTag(tag);
                    UserTag userTag = new UserTag(tag);
                    userTag.save();
                }

            }
        });
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
        btnPronounce.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
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
}
