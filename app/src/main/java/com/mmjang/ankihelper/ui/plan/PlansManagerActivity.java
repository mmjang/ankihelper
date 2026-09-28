package com.mmjang.ankihelper.ui.plan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.ui.plan.helper.SimpleItemTouchHelperCallback;
import com.mmjang.ankihelper.util.DialogUtil;
import com.mmjang.ankihelper.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class PlansManagerActivity extends AppCompatActivity {

    private List<OutputPlanPOJO> mPlanList;
    RecyclerView planListView;
    PlansAdapter mPlansAdapter;
    private static final String PLAN_SEP = "|||";
    private static final int ERROR_FORMAT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Settings.getInstance(this).getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_plan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.getAnkiDroid().isAnkiDroidRunning()) {
                    Intent intent = new Intent(PlansManagerActivity.this, PlanEditorActivity.class);
                    startActivity(intent);
                } else {
                    DialogUtil.showStartAnkiDialog(PlansManagerActivity.this);
                }
            }
        });
        initPlanList();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try{
                            MyApplication.getAnkiDroid().getApi().getDeckList();
                        }catch (Exception e){
                        }
                    }
                }
        ).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<OutputPlanPOJO> newList = ExternalDatabase.getInstance().getAllPlan();
        mPlanList.clear();
        mPlanList.addAll(newList);
        mPlansAdapter.notifyDataSetChanged();
    }

    private void initPlanList() {
        mPlanList = new ArrayList<>();
        //Log.d("PlansManager:", plans.size() + "ge");
        planListView = (RecyclerView) findViewById(R.id.plan_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        planListView.setLayoutManager(llm);
        mPlansAdapter = new PlansAdapter(PlansManagerActivity.this, mPlanList);
        //planList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        planListView.setAdapter(mPlansAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mPlansAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(planListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_plans_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_export_plan:
                exportPlans();
                break;
            case R.id.menu_item_import_plan:
                importPlans();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }

    private void importPlans() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(clipboard.hasPrimaryClip()){
            if(clipboard.getText()!=null){
                String plansString = clipboard.getText().toString();
                processPlanString(plansString);
            }
        }else{
            Toast.makeText(this, "剪贴板为空！", Toast.LENGTH_SHORT).show();
        }
    }

    private void processPlanString(String plansString) {
        String[] lines = plansString.split("\n");
        if(lines.length == 0){
            Toast.makeText(this, "格式错误！", Toast.LENGTH_SHORT).show();
            return ;
        }

        for(String line : lines){
            if(line.replace(" ","").replace("\t", "").equals("")){
                continue;//blank line
            }
            String[] items = line.split("\\|\\|\\|");
            if(items.length != 5){
                String errorMessage = line;
                errorMessage += "\n格式错误，每行项目数应为5";
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                continue;
            }
            try {
                String planName = items[0].trim();
                long deckId = Long.parseLong(items[1]);
                long modeld = Long.parseLong(items[2]);
                String dictKey = items[3].trim();
                String fieldMapString = items[4];
                for(OutputPlanPOJO outputPlan : mPlanList){
                    if(outputPlan.getPlanName().equals(planName)){
                        planName = planName + "_copy";
                        break;
                    }
                }
                OutputPlanPOJO outputPlan = new OutputPlanPOJO();
                outputPlan.setPlanName(planName);
                outputPlan.setOutputDeckId(deckId);
                outputPlan.setOutputModelId(modeld);
                outputPlan.setDictionaryKey(dictKey);
                outputPlan.setFieldsMap(Utils.fieldsStr2Map(fieldMapString));
                ExternalDatabase.getInstance().insertPlan(outputPlan);
            }
            catch (Exception e){
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        onResume();
    }

    private void exportPlans() {
        StringBuilder sb = new StringBuilder();
        for(OutputPlanPOJO plan : mPlanList){
            sb.append(plan.getPlanName());
            sb.append(PLAN_SEP);
            sb.append(plan.getOutputDeckId());
            sb.append(PLAN_SEP);
            sb.append(plan.getOutputModelId());
            sb.append(PLAN_SEP);
            sb.append(plan.getDictionaryKey());
            sb.append(PLAN_SEP);
            sb.append(plan.getFieldsMapString());
            sb.append("\n");
        }
        String exportedString = sb.toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("plans string", exportedString);
        clipboard.setPrimaryClip(clip);
    }

}
