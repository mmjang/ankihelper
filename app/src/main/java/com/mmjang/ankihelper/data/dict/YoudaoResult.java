package com.mmjang.ankihelper.data.dict;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by liao on 2017/7/30.
 */

public class YoudaoResult {
    public String returnPhrase = "";
    public String phonetic = "";
    public List<String> translation = new ArrayList<>();
    public Map<String, List<String>> webTranslation = new LinkedHashMap<>();

}
