package com.mmjang.duckmemo.data.news;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import com.mmjang.duckmemo.data.card.DaoSession;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class NewsEntry {
    @Id(autoincrement = true)
    private Long id;
    private String source = ""; //npr or //ap ...
    private String url = "";
    private String title = "";
    private String titleImageUrl = "";
    private String description = "";
    private String date = "";
    private long contentId;
    @ToOne(joinProperty = "contentId")
    private NewsContent content;
    private long lastSeenTime;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 264573279)
    private transient NewsEntryDao myDao;
    @Generated(hash = 1453637620)
    public NewsEntry(Long id, String source, String url, String title,
            String titleImageUrl, String description, String date, long contentId,
            long lastSeenTime) {
        this.id = id;
        this.source = source;
        this.url = url;
        this.title = title;
        this.titleImageUrl = titleImageUrl;
        this.description = description;
        this.date = date;
        this.contentId = contentId;
        this.lastSeenTime = lastSeenTime;
    }
    @Generated(hash = 35408494)
    public NewsEntry() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSource() {
        return this.source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitleImageUrl() {
        return this.titleImageUrl;
    }
    public void setTitleImageUrl(String titleImageUrl) {
        this.titleImageUrl = titleImageUrl;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public long getContentId() {
        return this.contentId;
    }
    public void setContentId(long contentId) {
        this.contentId = contentId;
    }
    public long getLastSeenTime() {
        return this.lastSeenTime;
    }
    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }
    @Generated(hash = 791892265)
    private transient Long content__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 257651519)
    public NewsContent getContent() {
        long __key = this.contentId;
        if (content__resolvedKey == null || !content__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NewsContentDao targetDao = daoSession.getNewsContentDao();
            NewsContent contentNew = targetDao.load(__key);
            synchronized (this) {
                content = contentNew;
                content__resolvedKey = __key;
            }
        }
        return content;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 974476000)
    public void setContent(@NotNull NewsContent content) {
        if (content == null) {
            throw new DaoException(
                    "To-one property 'contentId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.content = content;
            contentId = content.getId();
            content__resolvedKey = contentId;
        }
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1274396526)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNewsEntryDao() : null;
    }
}
