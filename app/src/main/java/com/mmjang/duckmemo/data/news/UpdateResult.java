package com.mmjang.duckmemo.data.news;

import java.util.List;

public class UpdateResult{
    boolean successful ;

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public List<NewsEntry> getNewsEntries() {
        return newsEntries;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    String message = "";
    List<NewsEntry> newsEntries;
    int updateCount;

    public UpdateResult(boolean successful, String message, List<NewsEntry> newsEntries, int updateCount) {
        this.successful = successful;
        this.message = message;
        this.newsEntries = newsEntries;
        this.updateCount = updateCount;
    }
}
