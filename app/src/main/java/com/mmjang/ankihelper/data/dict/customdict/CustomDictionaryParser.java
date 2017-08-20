package com.mmjang.ankihelper.data.dict.customdict;

import com.mmjang.ankihelper.data.dict.DictionaryRegister;

/**
 * Created by liao on 2017/8/17.
 */

public interface CustomDictionaryParser {
    CustomDictionaryInformation getCustomDictionaryInformation();
    boolean hasNext();
    CustomDictionaryEntry getNextEntry();
}
