package com.mmjang.ankihelper.data.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by liao on 2017/4/20.
 */

public class UserTag extends DataSupport {
    @Column(unique = true)
    private String tag;

    public UserTag(String pTag) {
        tag = pTag;
    }

    public void setTag(String planName) {
        this.tag = planName;
    }

    public String getTag() {
        return tag;
    }
}
