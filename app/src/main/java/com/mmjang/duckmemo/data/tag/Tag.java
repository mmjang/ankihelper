package com.mmjang.duckmemo.data.tag;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Tag {
    @Id
    private long id;
    private long lastUsedTime;//epoch
    @Unique
    private String name;
    @Generated(hash = 1879068626)
    public Tag(long id, long lastUsedTime, String name) {
        this.id = id;
        this.lastUsedTime = lastUsedTime;
        this.name = name;
    }
    @Generated(hash = 1605720318)
    public Tag() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getLastUsedTime() {
        return this.lastUsedTime;
    }
    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
