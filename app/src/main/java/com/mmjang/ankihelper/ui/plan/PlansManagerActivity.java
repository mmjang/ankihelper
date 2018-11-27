package com.mmjang.ankihelper.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.ui.plan.helper.SimpleItemTouchHelperCallback;
import com.mmjang.ankihelper.util.DialogUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class PlansManagerActivity extends AppCompatActivity {

    private List<OutputPlan> mPlanList;
    RecyclerView planListView;
    PlansAdapter mPlansAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<OutputPlan> newList = DataSupport.findAll(OutputPlan.class);
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

}
