package com.mmjang.duckmemo.ui.news;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.news.NewsEntry;
import com.mmjang.duckmemo.util.Constant;

import java.util.List;


/**
 * Created by liao on 2017/4/27.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
    private List<NewsEntry> mNewsEntryList;
    private Activity mActivity;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View countainer;
        TextView newsName;
        TextView newsSummary;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            countainer = view;
            image = (ImageView) view.findViewById(R.id.news_title_image);
            newsName = (TextView) view.findViewById(R.id.tv_news_title);
            newsSummary = (TextView) view.findViewById(R.id.tv_news_summary);
        }
    }

    public NewsAdapter(
            Activity activity,
            List<NewsEntry> planList) {
        mNewsEntryList = planList;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        NewsEntry plan = mNewsEntryList.get(position);
        holder.newsName.setText(plan.getTitle());
        holder.newsSummary.setText(plan.getDescription());
        Glide.with(mActivity).load(mNewsEntryList.get(position).getTitleImageUrl())
                .into(holder.image);
        holder.countainer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, NewsReaderActivity.class);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Constant.INTENT_DUCKMEMO_NEWS_ID, mNewsEntryList.get(position).getId());
                        mActivity.startActivity(intent);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mNewsEntryList.size();
    }
}
