package com.mmjang.duckmemo.data.dict.customdict;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mmjang.duckmemo.data.dict.CustomDictionary;
import com.mmjang.duckmemo.data.dict.IDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liao on 2017/8/17.
 */

public class TabDictionaryParser implements CustomDictionaryParser {



    @Override
    public CustomDictionaryInformation getCustomDictionaryInformation() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public CustomDictionaryEntry getNextEntry() {
        return null;
    }

}
