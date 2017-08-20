package com.mmjang.ankihelper.data.dict.customdict;

import java.io.IOException;

/**
 * Created by liao on 2017/8/17.
 */

public class ParserException extends IOException {
    public ParserException(){

    }

    public ParserException(String msg){
        super(msg);
    }
}
