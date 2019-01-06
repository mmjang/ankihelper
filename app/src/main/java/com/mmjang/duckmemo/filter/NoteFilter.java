package com.mmjang.duckmemo.filter;

import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.note.Note;
import com.mmjang.duckmemo.data.note.NoteDao;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class NoteFilter {

    NoteDao noteDao;
    public enum ORDERBY {
        TIME, WORD
    }

    public enum ORDER{
        DESC, ASC
    }

    public NoteFilter(){
        noteDao = MyApplication.getDaoSession().getNoteDao();
    }

    public List<Note> getNoteList(
            ORDERBY orderby,
            ORDER order,
            @NonNull String tag,
            @NonNull String keyWord
    ){

        tag = tag.trim();
        keyWord = keyWord.trim();
        QueryBuilder<Note> builder = noteDao.queryBuilder();
        List<WhereCondition> keyWordConditions = new ArrayList<>();
        WhereCondition tagCondition = NoteDao.Properties.Tag.like("%" + tag + "%");
        if(!keyWord.isEmpty()){
            keyWord = keyWord.trim();
            keyWordConditions.add(NoteDao.Properties.Word.like("%" + keyWord + "%"));
            keyWordConditions.add(NoteDao.Properties.Sentence.like("%" + keyWord + "%"));
            keyWordConditions.add(NoteDao.Properties.Definition.like("%" + keyWord + "%"));
            keyWordConditions.add(NoteDao.Properties.Translation.like("%" + keyWord + "%"));
        }

        if(!tag.isEmpty() && !keyWord.isEmpty()){
            builder = builder.where(
                    builder.and(
                            builder.or(keyWordConditions.get(0),keyWordConditions.get(1),keyWordConditions.get(2),keyWordConditions.get(3)),
                            tagCondition
                    )
            );
        }
        else if(!tag.isEmpty()){
            builder = builder.where(tagCondition);
        }
        else if(!keyWord.isEmpty()){
            builder = builder.where(builder.or(
                    keyWordConditions.get(0),keyWordConditions.get(1),keyWordConditions.get(2),keyWordConditions.get(3)));
        }
        if(order == ORDER.ASC){
            switch (orderby){
                case TIME:
                    builder.orderAsc(NoteDao.Properties.Id);
                    break;
                case WORD:
                    builder.orderAsc(NoteDao.Properties.Word);
            }
        }
        else if(order == ORDER.DESC){
            switch (orderby){
                case TIME:
                    builder.orderDesc(NoteDao.Properties.Id);
                    break;
                case WORD:
                    builder.orderDesc(NoteDao.Properties.Word);
            }
        }
        return builder.list();
    }
}
