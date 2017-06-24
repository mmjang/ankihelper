package com.mmjang.ankihelper.ui.plan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.plan.OutputPlan;

import java.util.List;

/**
 * Created by liao on 2017/4/27.
 */

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder> {
    private List<OutputPlan> mPlansList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView planName;
        Button btnDelete;
        Button btnEdit;

        public ViewHolder(View view) {
            super(view);
            planName = (TextView) view.findViewById(R.id.plans_name);
            btnDelete = (Button) view.findViewById(R.id.btn_delete);
            btnEdit = (Button) view.findViewById(R.id.btn_edit);
        }
    }

    public PlansAdapter(
            Activity activity,
            List<OutputPlan> planList) {
        mPlansList = planList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plans_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        OutputPlan plan = mPlansList.get(position);
        holder.planName.setText(plan.getPlanName());
        holder.btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.confirm_deletion)
                                //.setMessage("Do you really want to whatever?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        int pos = holder.getAdapterPosition();
                                        mPlansList.get(pos).delete();
                                        mPlansList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();

                    }
                }
        );
        holder.btnEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        String planName = mPlansList.get(pos).getPlanName();
                        Intent intent = new Intent(mActivity, PlanEditorActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, planName);
                        MyApplication.getContext().startActivity(intent);
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return mPlansList.size();
    }
}
