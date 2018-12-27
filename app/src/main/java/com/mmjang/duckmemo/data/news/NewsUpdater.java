package com.mmjang.duckmemo.data.news;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.news.loader.NPRLoader;

import java.util.List;

public class NewsUpdater {

    private static final int MAX_ITEM = 100;
    NewsEntryDao newsEntryDao;
    NewsLoader mNewsLoader;

    public NewsUpdater(NewsLoader loader){
        mNewsLoader = loader;
        newsEntryDao = MyApplication.getDaoSession().getNewsEntryDao();
    }

    public List<NewsEntry> getDBCache(){
        return newsEntryDao.queryBuilder().where(NewsEntryDao.Properties.Source.eq(mNewsLoader.getSourceName()))
                .orderDesc(NewsEntryDao.Properties.Id).limit(MAX_ITEM).list();
    }

    public UpdateResult update(){
        List<NewsEntry> newsEntryList = mNewsLoader.getNewsMeta();
        int updated = 0;
        if(newsEntryList != null){
            for (NewsEntry newsEntry : newsEntryList) {
                List<NewsEntry> newsEntries = newsEntryDao.queryBuilder()
                        .where(NewsEntryDao.Properties.Url.eq(newsEntry.getUrl())).list();
                int count = newsEntries.size();
                if (count == 0) {
                    updated++;
                    newsEntryDao.insert(newsEntry);
                }
            }
        }

        if(newsEntryList == null || newsEntryList.size() == 0){
            return new UpdateResult(false, "更新失败",
                    null,0);
        }else{
            UpdateResult updateResult = new UpdateResult(
                    true,
                    "",
                    newsEntryDao.queryBuilder().where(NewsEntryDao.Properties.Source.eq(mNewsLoader.getSourceName()))
                            .orderDesc(NewsEntryDao.Properties.Id).limit(MAX_ITEM).list(),
                    updated
            );
            return updateResult;
        }
    }
}
