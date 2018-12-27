package com.mmjang.duckmemo.data.note;

import com.mmjang.duckmemo.data.news.NewsEntryPosition;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import com.mmjang.duckmemo.data.card.DaoSession;
import com.mmjang.duckmemo.data.news.NewsEntryPositionDao;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class Note implements Addable{
    @Id
    private long id;
    private long lastedEditTime;
    private String language;
    private String word;
    private String sentence;
    private String translation;
    private String definition;
    private String extra;
    private String tag; //space separated
    private String data; //for storing additional data;
    private long newsEntryPositionId;
    @ToOne(joinProperty = "newsEntryPositionId")
    private NewsEntryPosition newsEntryPosition;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 363862535)
    private transient NoteDao myDao;
    @Generated(hash = 990349762)
    public Note(long id, long lastedEditTime, String language, String word,
            String sentence, String translation, String definition, String extra,
            String tag, String data, long newsEntryPositionId) {
        this.id = id;
        this.lastedEditTime = lastedEditTime;
        this.language = language;
        this.word = word;
        this.sentence = sentence;
        this.translation = translation;
        this.definition = definition;
        this.extra = extra;
        this.tag = tag;
        this.data = data;
        this.newsEntryPositionId = newsEntryPositionId;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getLastedEditTime() {
        return this.lastedEditTime;
    }
    public void setLastedEditTime(long lastedEditTime) {
        this.lastedEditTime = lastedEditTime;
    }
    public String getLanguage() {
        return this.language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getWord() {
        return this.word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public String getSentence() {
        return this.sentence;
    }
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
    public String getTranslation() {
        return this.translation;
    }
    public void setTranslation(String translation) {
        this.translation = translation;
    }
    public String getDefinition() {
        return this.definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }
    public String getExtra() {
        return this.extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }
    public String getTag() {
        return this.tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public long getNewsEntryPositionId() {
        return this.newsEntryPositionId;
    }
    public void setNewsEntryPositionId(long newsEntryPositionId) {
        this.newsEntryPositionId = newsEntryPositionId;
    }
    @Generated(hash = 1132928200)
    private transient Long newsEntryPosition__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 853413323)
    public NewsEntryPosition getNewsEntryPosition() {
        long __key = this.newsEntryPositionId;
        if (newsEntryPosition__resolvedKey == null
                || !newsEntryPosition__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NewsEntryPositionDao targetDao = daoSession.getNewsEntryPositionDao();
            NewsEntryPosition newsEntryPositionNew = targetDao.load(__key);
            synchronized (this) {
                newsEntryPosition = newsEntryPositionNew;
                newsEntryPosition__resolvedKey = __key;
            }
        }
        return newsEntryPosition;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1518709860)
    public void setNewsEntryPosition(@NotNull NewsEntryPosition newsEntryPosition) {
        if (newsEntryPosition == null) {
            throw new DaoException(
                    "To-one property 'newsEntryPositionId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.newsEntryPosition = newsEntryPosition;
            newsEntryPositionId = newsEntryPosition.getId();
            newsEntryPosition__resolvedKey = newsEntryPositionId;
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
    @Generated(hash = 799086675)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoteDao() : null;
    }
}
