package com.mmjang.duckmemo.data.news;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class NewsContent {
    @Id(autoincrement = true)
    Long id;
    String contentHtml;
    String highlights;
    @Generated(hash = 1845639092)
    public NewsContent(Long id, String contentHtml, String highlights) {
        this.id = id;
        this.contentHtml = contentHtml;
        this.highlights = highlights;
    }
    @Generated(hash = 1577047943)
    public NewsContent() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContentHtml() {
        return this.contentHtml;
    }
    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }
    public String getHighlights() {
        return this.highlights;
    }
    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }
}
