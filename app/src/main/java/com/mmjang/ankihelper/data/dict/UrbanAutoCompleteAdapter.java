package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.mmjang.ankihelper.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UrbanAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;
    private static final String BASE_URL = "https://api.urbandictionary.com/v0/autocomplete-extra?term=";

    public UrbanAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        if(resultList != null) {
            return resultList.size();
        }else{
            return 0;
        }
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private ArrayList<String> autocomplete(String key){
        if(key.trim().isEmpty()){
            return new ArrayList<>();
        }
        String url = BASE_URL + key;
        MyApplication.getOkHttpClient().dispatcher().cancelAll();
        Request request = new Request.Builder().url(url).build();
        try{
            String json = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            ArrayList<String> result = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray reItems = jsonObject.getJSONArray("results");
            for(int i = 0; i < reItems.length(); i ++){
                String term = reItems.getJSONObject(i).getString("term");
                result.add(term);
            }
            return result;
        }
        catch (IOException ioe){
            return new ArrayList<>();
        }
        catch (JSONException je){
            return new ArrayList<>();
        }
    }
}
