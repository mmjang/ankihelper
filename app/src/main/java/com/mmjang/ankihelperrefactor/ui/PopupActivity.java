package com.mmjang.ankihelperrefactor.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mmjang.ankihelperrefactor.R;
import com.mmjang.ankihelperrefactor.app.Definition;
import com.mmjang.ankihelperrefactor.app.DictionaryRegister;
import com.mmjang.ankihelperrefactor.app.Esdict;
import com.mmjang.ankihelperrefactor.app.IDictionary;
import com.mmjang.ankihelperrefactor.app.OutputPlan;
import com.mmjang.ankihelperrefactor.app.Settings;

import org.litepal.crud.DataSupport;

import java.util.List;


public class PopupActivity extends Activity {

    List<IDictionary> dictionaryList;
    IDictionary currentDicitonary;
    List<OutputPlan> outputPlanList;
    OutputPlan currentOutputPlan;
    Settings settings;
    //views
    AutoCompleteTextView act;
    Button btnSearch;
    Spinner planSpinner;
    RecyclerView recyclerViewDefinitionList;
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
        setContentView(R.layout.activity_popup);
        assignViews();
        loadData(); //dictionaryList;
        populatePlanSpinner();
        setEventListener();
    }

    private void assignViews(){
        act = (AutoCompleteTextView) findViewById(R.id.edit_text_hwd);
        btnSearch = (Button) findViewById(R.id.btn_search);
        planSpinner = (Spinner) findViewById(R.id.plan_spinner);
        recyclerViewDefinitionList = (RecyclerView) findViewById(R.id.recycler_view_definition_list);
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
        planSpinner.setAdapter(planSpinnerAdapter);
        //set plan to last selected plan
        String lastSelectedPlan = settings.getLastSelectedPlan();
        if(lastSelectedPlan.equals("")) //first use, set default plan to first one if any
        {
            if(outputPlanList.size() > 0){
                settings.setLastSelectedPlan(outputPlanList.get(0).getPlanName());
                currentOutputPlan = outputPlanList.get(0);
                currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
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
            }
        }else{
            //if find, then current plan and dictionary must have been set above.
        }
    }

    private void setEventListener(){

        planSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentOutputPlan = outputPlanList.get(position);
                        currentDicitonary = getDictionaryFromOutputPlan(currentOutputPlan);
                        //memorise last selected plan
                        settings.setLastSelectedPlan(currentOutputPlan.getPlanName());
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
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try  {
                                        //Your code goes here
                                        Log.d("clicked", "yes");
                                        IDictionary es = new Esdict(PopupActivity.this);
                                        List<Definition> d = currentDicitonary.wordLookup(word);
                                        //     Log.d("async_test", d.get(0).getDisplayHtml());
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
        DefinitionAdapter defAdapter = new DefinitionAdapter(definitionList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true);
        recyclerViewDefinitionList.setLayoutManager(llm);
        recyclerViewDefinitionList.setNestedScrollingEnabled(false);
        recyclerViewDefinitionList.setAdapter(defAdapter);
    }
}
