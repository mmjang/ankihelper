package com.mmjang.duckmemo.data.news;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.mmjang.duckmemo.data.card.DaoSession;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class NewsEntryPosition {
    @Id(autoincrement = true)
    private Long id;
    private int sentenceIndex;
    private long news_entry_id;
    @ToOne(joinProperty = "news_entry_id")
    private NewsEntry newsEntry;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 448789466)
    private transient NewsEntryPositionDao myDao;
    @Generated(hash = 1930363186)
    public NewsEntryPosition(Long id, int sentenceIndex, long news_entry_id) {
        this.id = id;
        this.sentenceIndex = sentenceIndex;
        this.news_entry_id = news_entry_id;
    }
    @Generated(hash = 867218744)
    public NewsEntryPosition() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getSentenceIndex() {
        return this.sentenceIndex;
    }
    public void setSentenceIndex(int sentenceIndex) {
        this.sentenceIndex = sentenceIndex;
    }
    public long getNews_entry_id() {
        return this.news_entry_id;
    }
    public void setNews_entry_id(long news_entry_id) {
        this.news_entry_id = news_entry_id;
    }
    @Generated(hash = 478925791)
    private transient Long newsEntry__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 486016107)
    public NewsEntry getNewsEntry() {
        long __key = this.news_entry_id;
        if (newsEntry__resolvedKey == null
                || !newsEntry__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NewsEntryDao targetDao = daoSession.getNewsEntryDao();
            NewsEntry newsEntryNew = targetDao.load(__key);
            synchronized (this) {
                newsEntry = newsEntryNew;
                newsEntry__resolvedKey = __key;
            }
        }
        return newsEntry;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2090085595)
    public void setNewsEntry(@NotNull NewsEntry newsEntry) {
        if (newsEntry == null) {
            throw new DaoException(
                    "To-one property 'news_entry_id' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.newsEntry = newsEntry;
            news_entry_id = newsEntry.getId();
            newsEntry__resolvedKey = news_entry_id;
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
    @Generated(hash = 503918182)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNewsEntryPositionDao() : null;
    }
}
