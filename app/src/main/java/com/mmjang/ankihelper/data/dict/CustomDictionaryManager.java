package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.litepal.crud.callback.FindCallback;

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
 * Created by liao on 2017/8/11.
 */

public class CustomDictionaryManager {
    private static final String DICTIONARY_FILE_EXTENSION = ".txt";
    private static final String DEFAULT_ENCODING = "UTF8";
    private static final String EQUAL = "=";
    private static final int MAX_ENTRIES_ONE_WRITE = 1000;
    private static final String META= "META";
    private static final String ENDMETA= "ENDMETA";
    private static final String META_VERSION = "VERSION";
    private static final String META_DICT_NAME = "DICT_NAME";
    private static final String META_DICT_INTRO = "DICT_INTRO";
    private static final String META_DICT_LANG = "DICT_LANG";
    private static final String META_DEF_TMPL = "DEF_TMPL";
    private static final String META_FIELDS = "FIELDS";


    public CustomDictionaryDbHelper dbHelper;
    String mDictionaryPath;
    Context mContext;

    List<File> tabDictionaryFiles = new ArrayList<>();

    private enum PARSE_STATE{
        START,
        META,
        DATA
    }

    public CustomDictionaryManager(Context context,@NonNull String dictionaryPath){
        dbHelper = new CustomDictionaryDbHelper(context);
        mDictionaryPath = dictionaryPath;
        mContext = context;
    }

    public boolean reFreshDB(){
        List<File> files = findDictionaryFiles(mDictionaryPath);
        if(files.size() == 0){
            return false;
        }
        dbHelper.clearDB();
        int i = 0;
        for(File file : files){
            if(processOneDictionaryFile(i, file)){
                i ++;
            }
        }

        if(i == 0)  //all .txt is illegal
        {
            return false;
        }else{
            return true;
        }
    }

    public List<File> findDictionaryFiles(String dictionaryPath){
        File directory = new File(dictionaryPath);
        File[] files = directory.listFiles();
        List<File> result = new ArrayList<>();
        if(files != null){
            for(File file : files){
                if(file.isFile() && file.getPath().endsWith(DICTIONARY_FILE_EXTENSION)) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    public void clearDictionaries(){
        dbHelper.clearDB();
    }

    public List<IDictionary> getDictionaryList(){
        List<IDictionary> dictionaries = new ArrayList<>();
        for(int id : dbHelper.getDictIdList()){
            dictionaries.add(new CustomDictionary(mContext, dbHelper, id));
        }
        return  dictionaries;
    }

    public boolean processOneDictionaryFile(int id, File dictFile){
        try {
            FileInputStream fstream = new FileInputStream(dictFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName(DEFAULT_ENCODING)));
            String line;
            String dictFileName = dictFile.getName().replace(DICTIONARY_FILE_EXTENSION, "");
            PARSE_STATE state = PARSE_STATE.START;
            int version = -1; //if -1 than no version number is found
            String dictName = dictFileName; //the default name is the file name
            String dictIntro = "";
            String dictLang = ""; //en jp fr etc
            String tmpl = ""; //if empty, just join all fields.
            String[] fields = new String[0];
            List<String[]> entries = new LinkedList<>();
            try {
                  while((line = br.readLine()) != null){

                      if(state == PARSE_STATE.START){
                          if(line.trim().equals(META)){
                              state = PARSE_STATE.META;
                              continue;
                          }
                      }

                      if(state == PARSE_STATE.META){
                          String trimmed = line.trim();
                          if(trimmed.startsWith(META_VERSION)){
                              String metaValueStr = getMetaValue(trimmed);
                              if(metaValueStr != null){
                                  try{
                                      int value = Integer.parseInt(metaValueStr);
                                      version = value;
                                  }catch (NumberFormatException e){
                                      ;
                                  }
                              }
                              continue;
                          }
                          if(trimmed.startsWith(META_DICT_NAME)){
                              String metaValueStr = getMetaValue(trimmed);
                              if(metaValueStr != null){
                                  dictName = metaValueStr;
                              }
                              continue;
                          }
                          if(trimmed.startsWith(META_DICT_INTRO)){
                              String metaValueStr = getMetaValue(trimmed);
                              if(metaValueStr != null){
                                  dictIntro = metaValueStr;
                              }
                              continue;
                          }
                          if(trimmed.startsWith(META_DICT_LANG)){
                              String metaValueStr = getMetaValue(trimmed);
                              if(metaValueStr != null){
                                  dictLang = metaValueStr;
                              }
                              continue;
                          }
                          if(trimmed.startsWith(META_DEF_TMPL)){
                              String metaValueStr = getMetaValue(trimmed);
                              if(metaValueStr != null){
                                  tmpl = metaValueStr;
                              }
                              continue;
                          }
                          if(trimmed.startsWith(META_FIELDS)){
                              String metaValueStr = getMetaValue(trimmed);
                              if(metaValueStr != null){
                                  fields = splitLineByTab(metaValueStr);
                              }
                              continue;
                          }
                          if(trimmed.equals(ENDMETA)) { //the meta section ends
                              state = PARSE_STATE.DATA;
                          }
                      }

                      if(state == PARSE_STATE.DATA){
                          if(version != 1 || fields.length < 2){
                              return false;
                          }
                          String[] splitted = splitLineByTab(line);
                          if(splitted.length == fields.length){ //ensure all rows are of same length
                              entries.add(splitted);
                              if(entries.size() > MAX_ENTRIES_ONE_WRITE){ //insert in batch to speed up
                                  dbHelper.addEntries(id, entries);
                                  entries.clear();
                              }
                          }else{

                          }
                      }
                  }
                  if(version < 0){
                      return false;
                  }
                  //insert remaining entries
                  dbHelper.addEntries(id, entries);
                  dbHelper.addDictionaryInformation(id, dictName, dictLang, fields, dictIntro, tmpl);

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        catch (FileNotFoundException e){
            return false;
        }
        return true;
    }

    private String[] splitLineByTab(String line){
        String[] results = line.split("\t");
        for(String s : results){
            s = s.trim();
        }
        return results;
    }

    @Nullable
    private String getMetaValue(String metaString){
        int index = metaString.indexOf(EQUAL);
        int len = metaString.length();
        if(index > 0){
            return metaString.substring(index + 1, len);
        }else{
            return null;
        }
    }
}
