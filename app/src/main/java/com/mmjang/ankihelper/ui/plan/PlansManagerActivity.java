package com.mmjang.ankihelper.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.util.DialogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class PlansManagerActivity extends AppCompatActivity {

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshPlanList();
    }

    private void refreshPlanList() {
        List<OutputPlan> plans = DataSupport.findAll(OutputPlan.class);
        //Log.d("PlansManager:", plans.size() + "ge");
        RecyclerView planList = (RecyclerView) findViewById(R.id.plan_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        planList.setLayoutManager(llm);
        PlansAdapter pa = new PlansAdapter(PlansManagerActivity.this, plans);
        //planList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        planList.setAdapter(pa);
    }

}
