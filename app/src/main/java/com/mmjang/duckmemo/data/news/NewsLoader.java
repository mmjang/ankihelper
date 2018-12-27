package com.mmjang.duckmemo.data.news;

import java.io.IOException;
import java.util.List;

public interface NewsLoader {
    String getSourceName();
    List<NewsEntry> getNewsMeta();
    void getContent(NewsEntry newsEntry) throws IOException;
}
