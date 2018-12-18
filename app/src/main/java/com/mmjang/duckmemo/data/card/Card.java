package com.mmjang.duckmemo.data.card;

import com.mmjang.duckmemo.data.note.Note;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.mmjang.duckmemo.data.note.NoteDao;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class Card implements SM2Card{
    @Id
    private long id;
    private long noteId;
    @ToOne(joinProperty = "noteId")
    private Note note;
    private int cardType;
    private long nextReviewTime;
    private int interval; // < 0, minute. > 0 day
    private int repetitions;
    private float easinessFactor;
    private String initialSteps;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 599084715)
    private transient CardDao myDao;
    @Generated(hash = 2092778196)
    public Card(long id, long noteId, int cardType, long nextReviewTime,
            int interval, int repetitions, float easinessFactor,
            String initialSteps) {
        this.id = id;
        this.noteId = noteId;
        this.cardType = cardType;
        this.nextReviewTime = nextReviewTime;
        this.interval = interval;
        this.repetitions = repetitions;
        this.easinessFactor = easinessFactor;
        this.initialSteps = initialSteps;
    }
    @Generated(hash = 52700939)
    public Card() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getNoteId() {
        return this.noteId;
    }
    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }
    public int getCardType() {
        return this.cardType;
    }
    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
    public long getNextReviewTime() {
        return this.nextReviewTime;
    }
    public void setNextReviewTime(long nextReviewTime) {
        this.nextReviewTime = nextReviewTime;
    }
    public int getInterval() {
        return this.interval;
    }
    public void setInterval(int interval) {
        this.interval = interval;
    }
    public int getRepetitions() {
        return this.repetitions;
    }
    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }
    public float getEasinessFactor() {
        return this.easinessFactor;
    }
    public void setEasinessFactor(float easinessFactor) {
        this.easinessFactor = easinessFactor;
    }
    public String getInitialSteps() {
        return this.initialSteps;
    }
    public void setInitialSteps(String initialSteps) {
        this.initialSteps = initialSteps;
    }

    @Override
    public int[] getInitialStepsArray(){
        String[] splitted = this.initialSteps.split(" ");
        int[] result = new int[splitted.length];
        for(int i = 0; i < result.length; i ++){
            result[i] = Integer.parseInt(splitted[i]);
        }
        return result;
    }

    @Generated(hash = 1056330060)
    private transient Long note__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1544665371)
    public Note getNote() {
        long __key = this.noteId;
        if (note__resolvedKey == null || !note__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NoteDao targetDao = daoSession.getNoteDao();
            Note noteNew = targetDao.load(__key);
            synchronized (this) {
                note = noteNew;
                note__resolvedKey = __key;
            }
        }
        return note;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 372886723)
    public void setNote(@NotNull Note note) {
        if (note == null) {
            throw new DaoException(
                    "To-one property 'noteId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.note = note;
            noteId = note.getId();
            note__resolvedKey = noteId;
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
    @Generated(hash = 1693529984)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCardDao() : null;
    }
}