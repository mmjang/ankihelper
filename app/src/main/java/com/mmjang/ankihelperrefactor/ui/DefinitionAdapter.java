package com.mmjang.ankihelperrefactor.ui;

import android.app.Activity;
import android.content.Context;
import android.icu.util.Output;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelperrefactor.R;
import com.mmjang.ankihelperrefactor.app.AnkiDroidHelper;
import com.mmjang.ankihelperrefactor.app.Constant;
import com.mmjang.ankihelperrefactor.app.Definition;
import com.mmjang.ankihelperrefactor.app.MyApplication;
import com.mmjang.ankihelperrefactor.app.OutputPlan;
import com.mmjang.ankihelperrefactor.app.TextSplitter;

import org.apmem.tools.layouts.FlowLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by liao on 2017/4/29.
 */

public class DefinitionAdapter extends RecyclerView.Adapter<DefinitionAdapter.ViewHolder>{
    private List<Definition> mDefinitionList;
    private TextSplitter mTextSplitter;
    private OutputPlan mOutputPlan;
    private Activity mActivity;

    DefinitionAdapter(Activity activity, List<Definition> definitionList, TextSplitter textSplitter, OutputPlan outputPlan){
        mDefinitionList = definitionList;
        mTextSplitter = textSplitter;
        mOutputPlan = outputPlan;
        mActivity = activity;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textVeiwDefinition;
        ImageButton btnAddDefinition;

        public ViewHolder(View view){
            super(view);
            textVeiwDefinition = (TextView) view.findViewById(R.id.textview_definition);
            btnAddDefinition = (ImageButton) view.findViewById(R.id.btn_add_definition);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.definition_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        final Definition def = mDefinitionList.get(position);
        holder.textVeiwDefinition.setText(Html.fromHtml(def.getDisplayHtml()));
        holder.btnAddDefinition.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnkiDroidHelper mAnkiDroid = MyApplication.getAnkiDroid(mActivity);
                        String[] sharedExportElements = Constant.getSharedExportElements();
                        String[] flds = new String[mOutputPlan.getFieldsMap().size()];
                        int i = 0;
                        Map<String, String> map = mOutputPlan.getFieldsMap();
                        for(String key : mOutputPlan.getFieldsMap().values()){
                            if(key.equals(sharedExportElements[0])){
                                flds[i] = "";
                                i ++;
                                continue;
                            }
                            if(key.equals(sharedExportElements[1])){
                                flds[i] = mTextSplitter.getBoldSentence(2);
                                i ++;
                                continue;
                            }
                            if(key.equals(sharedExportElements[2])){
                                flds[i] = mTextSplitter.getBlankSentence(2);
                                i ++;
                                continue;
                            }
                            if(def.hasElement(key)){
                                flds[i] = def.getExportElement(key);
                                i ++;
                                continue;
                            }
                            flds[i] = "";
                            i ++;
                        }
                        long deckId = mOutputPlan.getOutputDeckId();
                        long modelId = mOutputPlan.getOutputModelId();
                        long result = mAnkiDroid.getApi().addNote(modelId, deckId, flds, new HashSet<String>());
                        if(result > 0){
                            Toast.makeText(mActivity, "添加成功", Toast.LENGTH_SHORT).show();
                            holder.btnAddDefinition.setBackground(ContextCompat.getDrawable(
                                    mActivity, R.drawable.ic_add_grey));
                        }else{
                            Toast.makeText(mActivity, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public int getItemCount(){
        return mDefinitionList.size();
    }
}
