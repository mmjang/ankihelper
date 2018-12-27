package com.mmjang.duckmemo.data.news;

import android.support.annotation.NonNull;

import com.mmjang.duckmemo.data.news.loader.AtlanticLoader;
import com.mmjang.duckmemo.data.news.loader.NPRLoader;
import com.mmjang.duckmemo.data.news.loader.ScientificAmericanLoader;
import com.mmjang.duckmemo.data.news.loader.WPLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NewsLoaderUtils {
    public static List<NewsLoader> getLoaderList(){
        List<NewsLoader> newsLoaderList = new ArrayList<>();
        newsLoaderList.add(new NPRLoader(NPRLoader.SECTION_NATIONAL, "NPR National"));
        newsLoaderList.add(new NPRLoader(NPRLoader.SECTION_WORLD, "NPR World"));
        newsLoaderList.add(new NPRLoader(NPRLoader.SECTION_SCIENCE, "NPR Science"));
        newsLoaderList.add(new NPRLoader(NPRLoader.SECTION_TECH, "NPR Tech"));
        newsLoaderList.add(new NPRLoader(NPRLoader.SECTION_BUSINESS, "NPR Business"));
        newsLoaderList.add(new NPRLoader(NPRLoader.SECTION_ART, "NPR Art&Life"));

        String wpPrefix = "WP ";
        newsLoaderList.add(new WPLoader(WPLoader.WORLD, wpPrefix + "World"));
        newsLoaderList.add(new WPLoader(WPLoader.POLITICS, wpPrefix + "Politics"));
        newsLoaderList.add(new WPLoader(WPLoader.NATIONAL, wpPrefix + "National"));
        newsLoaderList.add(new WPLoader(WPLoader.BUSINESS, wpPrefix + "Business"));
        newsLoaderList.add(new WPLoader(WPLoader.LIFESTYLE, wpPrefix + "Lifestyle"));
        newsLoaderList.add(new WPLoader(WPLoader.OPNIONS, wpPrefix + "Opinions"));

        String atPrefix= "Atlantic-";
        newsLoaderList.add(new AtlanticLoader(AtlanticLoader.SECTION_POLITICS, atPrefix +"Politics"));
        newsLoaderList.add(new AtlanticLoader(AtlanticLoader.SECTION_TECH, atPrefix +"Tech"));
        newsLoaderList.add(new AtlanticLoader(AtlanticLoader.SECTION_ENTERTAINMENT, atPrefix +"Entertainment"));
        newsLoaderList.add(new AtlanticLoader(AtlanticLoader.SECTION_IDEAS, atPrefix +"Ideas"));

        newsLoaderList.add(new ScientificAmericanLoader(ScientificAmericanLoader.SECTION_SCIENCES, "Scientific American - Sciences"));
        return newsLoaderList;
    }

    public static NewsLoader getLoaderByName(String name){
        for(NewsLoader loader : getLoaderList()){
            if(loader.getSourceName().equals(name)){
                return loader;
            }
        }
        return null;
    }
}
