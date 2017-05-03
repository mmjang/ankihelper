package com.mmjang.ankihelperrefactor.ui;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mmjang.ankihelperrefactor.R;
import com.mmjang.ankihelperrefactor.app.Definition;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

/**
 * Created by liao on 2017/4/29.
 */

public class DefinitionAdapter extends RecyclerView.Adapter<DefinitionAdapter.ViewHolder>{
    private List<Definition> mDefinitionList;

    DefinitionAdapter(List<Definition> definitionList){
        mDefinitionList = definitionList;
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
        Definition def = mDefinitionList.get(position);
        holder.textVeiwDefinition.setText(Html.fromHtml(def.getDisplayHtml()));
    }

    @Override
    public int getItemCount(){
        return mDefinitionList.size();
    }
}
