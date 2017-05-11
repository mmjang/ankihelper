package com.mmjang.ankihelperrefactor.ui;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelperrefactor.R;
import com.mmjang.ankihelperrefactor.app.Definition;
import com.mmjang.ankihelperrefactor.app.DictionaryRegister;
import com.mmjang.ankihelperrefactor.app.Esdict;
import com.mmjang.ankihelperrefactor.app.IDictionary;
import com.mmjang.ankihelperrefactor.app.OutputPlan;
import com.mmjang.ankihelperrefactor.app.Settings;
import com.mmjang.ankihelperrefactor.app.TextSegment;
import com.mmjang.ankihelperrefactor.app.TextSplitter;
import com.mmjang.ankihelperrefactor.app.Utils;

import org.apmem.tools.layouts.FlowLayout;
import org.litepal.crud.DataSupport;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class PopupActivity extends Activity {

    //constant
    private static final int STATE_NON_WORD = 0;
    private static final int STATE_WORD = 1;
    private static final int STATE_SELECTED = 2;

    List<IDictionary> dictionaryList;
    IDictionary currentDicitonary;
    List<OutputPlan> outputPlanList;
    OutputPlan currentOutputPlan;
    Settings settings;
    String mTextToProcess;
    String mCurrentKeyWord;
    TextSplitter mTextSplitter;
    //views
    AutoCompleteTextView act;
    Button btnSearch;
    Spinner planSpinner;
    RecyclerView recyclerViewDefinitionList;
    FlowLayout wordSelectBox;
    //async event
    private static final int  PROCESS_DEFINITION_LIST = 1;
    //async
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROCESS_DEFINITION_LIST:
                    List<Definition> definitionList = (List<Definition>) msg.obj;
                    processDefinitionList(definitionList);
                    break;
                case 2:
                    // ...
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_popup);
        //set animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        //
        assignViews();
        loadData(); //dictionaryList;
        populatePlanSpinner();
        setEventListener();
        handleIntent();
    }

    private void setStatusBarColor(){
        int statusBarColor = 0;
        if(Build.VERSION.SDK_INT >= 21){
            statusBarColor = getWindow().getStatusBarColor();
        }
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(statusBarColor);
        }
    }

    private void assignViews(){
        act = (AutoCompleteTextView) findViewById(R.id.edit_text_hwd);
        btnSearch = (Button) findViewById(R.id.btn_search);
        planSpinner = (Spinner) findViewById(R.id.plan_spinner);
        recyclerViewDefinitionList = (RecyclerView) findViewById(R.id.recycler_view_definition_list);
        wordSelectBox = (FlowLayout) findViewById(R.id.words_select_box);
    }

    private void loadData(){
        dictionaryList = DictionaryRegister.getDictionaryObjectList();
        outputPlanList = DataSupport.findAll(OutputPlan.class);
        settings = Settings.getInstance(this);

    }

    private void populatePlanSpinner(){
        String[] planNameArr = new String[outputPlanList.size()];
        for(int i = 0; i < outputPlanList.size(); i ++){
            planNameArr[i] = outputPlanList.get(i).getPlanName();
        }
        ArrayAdapter<String> planSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, planNameArr);
        planSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planSpinner.setAdapter(planSpinnerAdapter);
        //set plan to last selected plan
        String lastSelectedPlan = settings.getLastSelectedPlan();
        if(lastSelectedPlan.equals("")) //first use, set default plan to first one if any
        {
            if(outputPlanList.size() > 0){
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                setActAdapter(currentDicitonary);
            }else{
                //no outputplan available!!!
            }
        }

        int i = 0;
        boolean find = false;
        for(OutputPlan plan : outputPlanList){
            if(plan.getPlanName().equals(lastSelectedPlan)){
                planSpinner.setSelection(i);
                currentOutputPlan = outputPlanList.get(i);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                setActAdapter(currentDicitonary);
                find = true;
                break;
            }
            //if not equal, compare next
            i ++;
        }
        if(!find) //if the saved last plan no longer in the plan list, reset to first one
        {
            if(outputPlanList.size() > 0){
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                setActAdapter(currentDicitonary);
            }
        }else{
            //if find, then current plan and dictionary must have been set above.
        }
    }

    private void setEventListener(){

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
                        if(mCurrentKeyWord != null){
                            asyncSearch(act.getText().toString());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        btnSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String word = act.getText().toString();
                        if(!word.isEmpty()){
                            asyncSearch(word);
                            Utils.hideSoftKeyboard(PopupActivity.this);
                        }
                    }
                }
        );
    }

    private IDictionary getDictionaryFromOutputPlan(OutputPlan outputPlan){
        String dictionaryName = outputPlan.getDictionaryKey();
        for(IDictionary dict : dictionaryList){
            if(dict.getDictionaryName().equals(dictionaryName)){
                return dict;
            }
        }
        return null;
    }

    private void processDefinitionList(List<Definition> definitionList){
        if(definitionList.isEmpty()){
            Snackbar.make(recyclerViewDefinitionList, "没查到", Snackbar.LENGTH_SHORT).setAction("action", null).show();
        }else {
            DefinitionAdapter defAdapter = new DefinitionAdapter(PopupActivity.this, definitionList, mTextSplitter, currentOutputPlan);
            LinearLayoutManager llm = new FullyLinearLayoutManager(this);
            //llm.setAutoMeasureEnabled(true);
            recyclerViewDefinitionList.setLayoutManager(llm);
            recyclerViewDefinitionList.setNestedScrollingEnabled(false);
            recyclerViewDefinitionList.setAdapter(defAdapter);
        }
    }


    private void handleIntent(){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (intent == null) {
            return ;
        }
        if (type == null) {
            return ;
        }
        //getStringExtra() may return null
        if (Intent.ACTION_SEND.equals(action) && type.equals("text/plain")) {
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (Intent.ACTION_PROCESS_TEXT.equals(action) && type.equals("text/plain")) {
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);
        }
        if(mTextToProcess == null){
            return ;
        }
        mTextSplitter = new TextSplitter(mTextToProcess, STATE_NON_WORD, STATE_WORD);
        populateWordSelectBox(mTextSplitter);
    }

    private void populateWordSelectBox(TextSplitter splitter){
        //todo: this is dirty, be sure to reimplement later.
        for(TextSegment ts : splitter.getSegmentList()){
            wordSelectBox.addView(getWordSelectBoxItem(ts));
        }
    }

    private TextView getWordSelectBoxItem(final TextSegment textSegment){
        int pad1 = Utils.getPX(PopupActivity.this, 1);
        final String text = textSegment.getText();
        int state = textSegment.getState();
        TextView tv = new TextView(this);
        tv.setText(text);
        FlowLayout.LayoutParams fllp = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        int pad = Utils.getPX(PopupActivity.this, 2);
        fllp.setMargins(0, 0, 0, 0);
        tv.setLayoutParams(fllp);
        switch(state) {
            case STATE_NON_WORD:
                tv.setTextColor(Color.BLACK);
                tv.setBackground(ContextCompat.getDrawable(
                        PopupActivity.this, R.drawable.word_select_box_item_trans));
                tv.setPadding(0,pad,0,pad);
                break;
            case STATE_WORD:
                tv.setBackground(ContextCompat.getDrawable(
                        PopupActivity.this, R.drawable.word_select_box_item));
                tv.setTextColor(Color.BLACK);
                tv.setPadding(pad,pad,pad,pad);
                break;
        }

        tv.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textView = (TextView) v;
                        if(textSegment.getState() == STATE_NON_WORD){
                            return;
                        }
                        if(textSegment.getState() == STATE_WORD){
                            textSegment.setState(STATE_SELECTED);
                            textView.setBackground(ContextCompat.getDrawable(
                                    PopupActivity.this, R.drawable.word_select_box_item_hl));
                            textView.setTextColor(Color.WHITE);
                            mCurrentKeyWord = mTextSplitter.getStringFromState(STATE_SELECTED);
                            act.setText(mCurrentKeyWord);
                            asyncSearch(mCurrentKeyWord);
                            return ;
                        }
                        if(textSegment.getState() == STATE_SELECTED){
                            textSegment.setState(STATE_WORD);
                            textView.setBackground(ContextCompat.getDrawable(
                                    PopupActivity.this, R.drawable.word_select_box_item));
                            textView.setTextColor(Color.BLACK);
                            mCurrentKeyWord = mTextSplitter.getStringFromState(STATE_SELECTED);
                            act.setText(mCurrentKeyWord);
                            asyncSearch(mCurrentKeyWord);
                            return ;
                        }
                    }
                }
        );

        return tv;
    }

    private void asyncSearch(final String word){
        if(word.length() == 0){
            return ;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    //Your code goes here
                    Log.d("clicked", "yes");
                    List<Definition> d = currentDicitonary.wordLookup(word);
                    Message message = mHandler.obtainMessage();
                    message.obj = d;
                    message.what = PROCESS_DEFINITION_LIST;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void setActAdapter(IDictionary dict){
        SimpleCursorAdapter sca = (SimpleCursorAdapter) dict.getAutoCompleteAdapter(PopupActivity.this,
                android.R.layout.simple_spinner_dropdown_item);
        if(sca != null){
            act.setAdapter(sca);
        }
    }
}
