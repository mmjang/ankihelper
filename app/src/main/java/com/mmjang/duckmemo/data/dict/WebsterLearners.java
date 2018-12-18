package com.mmjang.duckmemo.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mmjang.duckmemo.MyApplication;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liao on 2017/4/28.
 */

public class WebsterLearners extends SQLiteAssetHelper implements IDictionary {

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "韦氏学习词典";
    private static final String DICT_INTRO = "数据来自 learnersdictionary.com";
    private static final String[] EXP_ELE = new String[] {"单词", "音标", "发音", "释义"};

    private static final String WORD_URL_BASE = "http://learnersdictionary.com/definition/";
    private static final String MP3_URL_BASE = "http://media.merriam-webster.com/audio/prons/en/us/mp3/";

    private static final String DATABASE_NAME = "wb_headwords.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private Context mContext;
    public WebsterLearners(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
        mContext = context;
    }

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public String[] getExportElementsList() {
        return EXP_ELE;
    }

    public List<Definition> wordLookup(String key) {
        List<Definition> result = new ArrayList<>();
        if(isWordInDict(key)){
            result.addAll(queryDefinition(key));
        }
        else{
            for(String form : FormsUtil.getInstance(mContext).getForms(key)){
                if(isWordInDict(form)) {
                    result.addAll(queryDefinition(form));
                }
            }
        }

        if(result.size() == 0){
            try {
                result.add(toDefinition(YoudaoOnline.getDefinition(key)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

        public List<Definition> queryDefinition(String key) {
        try {
            Document doc = Jsoup.connect(WORD_URL_BASE + key)
                    .userAgent("Mozilla")
                    .timeout(5000)
                    .get();

            List<Definition> definitionList = new ArrayList<>();
            //remove redundant nodes
            for(Element  redundant: doc.select("sup, .vis_w, .def_labels, .dxs")){
                redundant.remove();
            }

            String mp3Url = ""; //some entry may not have mp3, then we must share it;
            for(Element entry : doc.select(".entry")){
                String headword = getSingleQueryResult(entry, ".hw_txt", false);
                String phonetic = getSingleQueryResult(entry, ".text_prons", false);
                String labels = "<font color=grey>" + getSingleQueryResult(entry, ".labels", false) + "</font>";
                String sense = getSingleQueryResult(entry,"div.fl", false);
                Elements mp3Soup = entry.select(".fa.fa-volume-up");
                if(mp3Soup.size() > 0){
                    Element mp3Node = mp3Soup.get(0);
                    String dir = mp3Node.attr("data-dir");
                    String file = mp3Node.attr("data-file");
                    mp3Url = MP3_URL_BASE + "/" + dir + "/" + file + ".mp3";
                }
                for(Element sense_node : entry.select(".sblock_entry > .sblock_c")){
                    String def_text = getSBlockHtml(sense_node);
                    HashMap<String, String> defMap = new HashMap<>();
                    defMap.put(EXP_ELE[0], headword);
                    defMap.put(EXP_ELE[1], phonetic);
                    defMap.put(EXP_ELE[2], getMp3Tag(mp3Url));
                    defMap.put(EXP_ELE[3], String.format("<i>%s</i>%s<br/>%s",
                            sense, labels, def_text
                            ));
                    String displayedHtml =
                            String.format("<span><b>%s</b> %s<font color='%s'><i>%s</i></font> %s</span><span>%s</span>",
                                    headword, phonetic, getSenseColor(sense), sense, labels, def_text
                            );
                    definitionList.add(new Definition(headword, defMap, displayedHtml, displayedHtml));
                }

                for(Element phrase : entry.select(".dros > .dro")) {
                    String phrase_headword = getSingleQueryResult(phrase, "h2.dre", false);
                    String phrase_gram = "<font color=grey>" + getSingleQueryResult(phrase, "span.gram", false) + "</font>";
                    for(Element sense_node : phrase.select(".sblock_c")){
                        String def_text = getSBlockHtml(sense_node);
                        HashMap<String, String> defMap = new HashMap<>();
                        defMap.put(EXP_ELE[0], phrase_headword);
                        defMap.put(EXP_ELE[1], phonetic);
                        defMap.put(EXP_ELE[2], getMp3Tag(mp3Url));
                        defMap.put(EXP_ELE[3],
                                String.format("%s<br/>%s",
                                phrase_gram, def_text
                                ));
                        String displayedHtml =
                                String.format("<span><font color=blue><b>%s</b></font> %s</span><span>%s</span>",
                                        phrase_headword, phrase_gram, def_text
                                );
                        definitionList.add(new Definition(phrase_headword, defMap, displayedHtml, displayedHtml));
                    }
                }
            }


            return definitionList;

        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(context, layout,
                        null,
                        new String[]{"hwd"},
                        new int[]{android.R.id.text1},
                        0
                );
        adapter.setFilterQueryProvider(
                new FilterQueryProvider() {
                    @Override
                    public Cursor runQuery(CharSequence constraint) {
                        return getFilterCursor(constraint.toString());
                    }
                }
        );
        adapter.setCursorToStringConverter(
                new SimpleCursorAdapter.CursorToStringConverter() {
                    @Override
                    public CharSequence convertToString(Cursor cursor) {
                        return cursor.getString(1);
                    }
                }
        );

        return adapter;
    }

    private static String getSingleQueryResult(Document soup, String query, boolean toString){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            if(toString){
                return re.get(0).toString();
            }else {
                return re.get(0).text().trim();
            }
        }else{
            return "";
        }
    }

    private Cursor getFilterCursor(String q) {
        Log.d("databse", "getFilterCursor" + q);
        Cursor cursor = db.query("hwds", new String[]{"rowid _id", "hwd"}, "hwd LIKE ?", new String[]{q + "%"}, null, null, null);
        return cursor;
    }

    private boolean isWordInDict(String word){
        Cursor cursor = db.query("hwds", new String[] {"hwd"},
                "hwd=? COLLATE NOCASE", new String[]{word}, null, null, null);
        if(cursor.moveToNext()){
            return true;
        }
        return false;
    }

    private static String getSingleQueryResult(Element soup, String query, boolean toString){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            if(toString) {
                return re.get(0).toString();
            }
            else{
                return re.get(0).text().trim();
            }
        }else{
            return "";
        }
    }

    private String getMp3Tag(String file){
        if(file == null || file.isEmpty()){
            return "";
        }
        return "[sound:" + file + "]";
    }

    private String getDefHtml(Element def){
        String sense = def.toString().replaceAll("<h3.+?>","<div>").replace("</h3>","</div>").replaceAll("<a.+?>","<b>").replace("</a>","</b>");
        //String defString = def.child(1).text().trim();
        return sense;
    }

    private String getSenseColor(String sense){
        if(sense.equals("verb")){
            return "#539007";
        }
        if(sense.equals("adjective")){
            return "#f8b002";
        }
        if(sense.equals("adverb")){
            return "#684b9d";
        }
        if(sense.equals("noun")){
            return "#e3412f";
        }
        if(sense.equals("conjunction")){
            return "#04B7C9";
        }
        return "#04B7C9";
    }

    private String getSBlockHtml(Element sense_node) {
        //String sn_block_num = "<font color=blue>" + getSingleQueryResult(sense_node, ".sn_block_num", false) + "</font>";
        String label = "";
        Elements blockSoup = sense_node.select(".sblock_labels");
        if(blockSoup.size() > 0){
            Element sblockLabelNode = blockSoup.get(0);
            for(Element pva : sblockLabelNode.select(".pva")){
                pva.tagName("b");
            }
            label = sblockLabelNode.html() + "<br/>";
        }
        String senseHtml = "";
        for(Element sense : sense_node.select(".sense")){
            for(Element sgram : sense.select(".sgram_internal")){
                sgram.tagName("font");
                sgram.attr("color", "grey");
            }

            for(Element sgram : sense.select(".ssla")){
                sgram.tagName("i");
            }

            for(Element sgram : sense.select(".pva")){
                sgram.tagName("b");
            }
            senseHtml += label + sense.html() + "<br/>";
        }
        int len = senseHtml.length();
        return senseHtml.substring(0, len - 5);
    }


    private Definition toDefinition(YoudaoResult youdaoResult){
        String notiString = "<font color='gray'>本地词典未查到，以下是有道在线释义</font><br/>";
        String definition = "<b>" + youdaoResult.returnPhrase + "</b><br/>";
        for(String def : youdaoResult.translation){
            definition += def + "<br/>";
        }

        definition += "<font color='gray'>网络释义</font><br/>";
        for(String key : youdaoResult.webTranslation.keySet()){
            String joined = "";
            for(String value : youdaoResult.webTranslation.get(key)){
                joined += value + "; ";
            }
            definition += "<b>" + key + "</b>: " + joined + "<br/>";
        }

        Map<String, String> exp = new HashMap<>();
        exp.put(EXP_ELE[0], youdaoResult.returnPhrase);
        exp.put(EXP_ELE[1], youdaoResult.phonetic);
        exp.put(EXP_ELE[3], definition);
        exp.put(EXP_ELE[2], getYoudaoAudioTag(youdaoResult.returnPhrase, 2));
        return new Definition(exp, notiString + definition);
    }

    String getYoudaoAudioTag(String word, int voiceType){
        return "[sound:http://dict.youdao.com/dictvoice?audio=" + word + "&type=" + voiceType +"]";
    }
}

