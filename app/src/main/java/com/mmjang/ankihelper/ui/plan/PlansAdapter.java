package com.mmjang.ankihelper.ui.plan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.ui.plan.helper.ItemTouchHelperAdapter;
import com.mmjang.ankihelper.ui.plan.helper.ItemTouchHelperViewHolder;
import com.mmjang.ankihelper.util.DialogUtil;
import com.mmjang.ankihelper.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by liao on 2017/4/27.
 */

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder> implements ItemTouchHelperAdapter{
    private List<OutputPlanPOJO> mPlansList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
        RelativeLayout countainer;
        TextView planName;
        TextView dictName;
        LinearLayout layoutEdit;
        LinearLayout layoutDelete;

        public ViewHolder(View view) {
            super(view);
            countainer = view.findViewById(R.id.plan_item);
            planName = (TextView) view.findViewById(R.id.plans_name);
            dictName = (TextView) view.findViewById(R.id.plans_dictionary_name);
            layoutEdit = (LinearLayout) view.findViewById(R.id.layout_edit);
            layoutDelete = (LinearLayout) view.findViewById(R.id.layout_delete);
        }

        @Override
        public void onItemSelected() {
            countainer.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            countainer.setBackgroundColor(0);
        }
    }

    public PlansAdapter(
            Activity activity,
            List<OutputPlanPOJO> planList) {
        mPlansList = planList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plans, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        OutputPlanPOJO plan = mPlansList.get(position);
        holder.planName.setText(plan.getPlanName());
        holder.dictName.setText(plan.getDictionaryKey());
        holder.layoutDelete.setOnClickListener(
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
                                        //mPlansList.get(pos).delete();
                                        ExternalDatabase.getInstance().deletePlanByName(mPlansList.get(pos).getPlanName());
                                        mPlansList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                }
        );

        holder.layoutEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyApplication.getAnkiDroid().isAnkiDroidRunning()) {
                            int pos = holder.getAdapterPosition();
                            String planName = mPlansList.get(pos).getPlanName();
                            Intent intent = new Intent(mActivity, PlanEditorActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, planName);
                            MyApplication.getContext().startActivity(intent);
                        } else {
                            DialogUtil.showStartAnkiDialog(mActivity);
                        }
                    }
                }
        );

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        OutputPlanPOJO from = mPlansList.get(fromPosition);
        mPlansList.remove(fromPosition);
        mPlansList.add(toPosition, from);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onMoveFinished() {
        ExternalDatabase.getInstance().refreshPlanWith(mPlansList);
//        List<OutputPlan> plansInDatabase = DataSupport.findAll(OutputPlan.class);
//        for(int i = 0; i < plansInDatabase.size(); i ++){
//            OutputPlan oldPlan = plansInDatabase.get(i);
//            OutputPlan newPlan = mPlansList.get(i);
//            if(oldPlan.getPlanName() == newPlan.getPlanName()){
//                continue;
//            }else{
//                oldPlan.setPlanName(newPlan.getPlanName());
//                oldPlan.setDictionaryKey(newPlan.getDictionaryKey());
//                oldPlan.setOutputDeckId(newPlan.getOutputDeckId());
//                oldPlan.setOutputModelId(newPlan.getOutputModelId());
//                oldPlan.setFieldsMap(newPlan.getFieldsMap());
//                oldPlan.save();
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mPlansList.size();
    }
}
