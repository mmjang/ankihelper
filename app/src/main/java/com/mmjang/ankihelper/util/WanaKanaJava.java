package com.mmjang.ankihelper.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Java version of the Javascript WanaKana romaji-to-kana converter library (https://github.com/WaniKani/WanaKana)
 * Version 1.1.1
 */
public class WanaKanaJava
{

    //static final int LOWERCASE_START = 0x61;
    //static final int LOWERCASE_END = 0x7A;
    static final int UPPERCASE_START = 0x41;
    static final int UPPERCASE_END = 0x5A;
    static final int HIRAGANA_START = 0x3041;
    static final int HIRAGANA_END = 0x3096;
    static final int KATAKANA_START = 0x30A1;
    static final int KATAKANA_END = 0x30FA;

    HashMap<String, Boolean> mOptions = new HashMap<String, Boolean>();
    static final String OPTION_USE_OBSOLETE_KANA = "useObsoleteKana";
    static final String OPTION_IME_MODE = "IMEMode";

    HashMap<String, String> mRtoJ = new HashMap<String, String>();
    HashMap<String, String> mJtoR = new HashMap<String, String>();

    private interface Command
    {
        public boolean run(String str);
    }

    public WanaKanaJava(Boolean useObsoleteKana)
    {

        mOptions.put(OPTION_USE_OBSOLETE_KANA, useObsoleteKana);
        mOptions.put(OPTION_IME_MODE, false);

        prepareRtoJ();
        prepareJtoR();
    }

    // Pass every character of a string through a function and return TRUE if every character passes the function's check
    private boolean _allTrue(String checkStr, Command func)
    {
        for (int _i = 0; _i < checkStr.length(); _i++)
        {
            if (!func.run(String.valueOf(checkStr.charAt(_i))))
            {
                return false;
            }
        }
        return true;
    }

    // Check if a character is within a Unicode range
    private boolean _isCharInRange(char chr, int start, int end)
    {
        int code = (int) chr;
        return (start <= code && code <= end);
    }

    private boolean _isCharVowel(char chr, boolean includeY)
    {
        Pattern regexp = includeY ? Pattern.compile("[aeiouy]") : Pattern.compile("[aeiou]");
        Matcher matcher = regexp.matcher(String.valueOf(chr));

        return matcher.find();
    }

    private boolean _isCharConsonant(char chr, boolean includeY)
    {
        Pattern regexp = includeY ? Pattern.compile("[bcdfghjklmnpqrstvwxyz]") : Pattern.compile("[bcdfghjklmnpqrstvwxz]");
        Matcher matcher = regexp.matcher(String.valueOf(chr));

        return matcher.find();
    }

    private boolean _isCharKatakana(char chr)
    {
        return _isCharInRange(chr, KATAKANA_START, KATAKANA_END);
    }

    private boolean _isCharHiragana(char chr)
    {
        return _isCharInRange(chr, HIRAGANA_START, HIRAGANA_END);
    }

    private boolean _isCharKana(char chr)
    {
        return _isCharHiragana(chr) || _isCharKatakana(chr);
    }

    private String _katakanaToHiragana(String kata)
    {
        int code;
        String hira = "";

        for (int _i = 0; _i < kata.length(); _i++)
        {
            char kataChar = kata.charAt(_i);

            if (_isCharKatakana(kataChar))
            {
                code = (int) kataChar;
                code += HIRAGANA_START - KATAKANA_START;
                hira += String.valueOf(Character.toChars(code));
            }
            else
            {
                hira += kataChar;
            }
        }

        return hira;
    }

    private String _hiraganaToKatakana(String hira)
    {
        int code;
        String kata = "";

        for (int _i = 0; _i < hira.length(); _i++)
        {
            char hiraChar = hira.charAt(_i);

            if (_isCharHiragana(hiraChar))
            {
                code = (int) hiraChar;
                code += KATAKANA_START - HIRAGANA_START;
                kata += String.valueOf(Character.toChars(code));
            }
            else
            {
                kata += hiraChar;
            }
        }

        return kata;
    }

    public String _hiraganaToRomaji(String hira)
    {
        if(isRomaji(hira))
        {
            return hira;
        }

        String chunk = "";
        int chunkSize;
        int cursor = 0;
        int len = hira.length();
        int maxChunk = 2;
        boolean nextCharIsDoubleConsonant = false;
        String roma = "";
        String romaChar = null;

        while (cursor < len)
        {
            chunkSize = Math.min(maxChunk, len - cursor);
            while (chunkSize > 0)
            {
                chunk = hira.substring(cursor, (cursor+chunkSize));

                if (isKatakana(chunk))
                {
                    chunk = _katakanaToHiragana(chunk);
                }

                if (String.valueOf(chunk.charAt(0)).equals("っ") && chunkSize == 1 && cursor < (len - 1))
                {
                    nextCharIsDoubleConsonant = true;
                    romaChar = "";
                    break;
                }

                romaChar = mJtoR.get(chunk);

                if ((romaChar != null) && nextCharIsDoubleConsonant)
                {
                    romaChar = romaChar.charAt(0) + romaChar;
                    nextCharIsDoubleConsonant = false;
                }

                if (romaChar != null)
                {
                    break;
                }

                chunkSize--;
            }
            if (romaChar == null)
            {
                romaChar = chunk;
            }

            roma += romaChar;
            cursor += chunkSize > 0 ? chunkSize : 1;
        }
        return roma;
    }

    private String _romajiToHiragana(String roma)
    {
        return _romajiToKana(roma, true);
    }

    private String _romajiToKana(String roma, Boolean ignoreCase)
    {
        String chunk = "";
        String chunkLC = "";
        int chunkSize;
        int position = 0;
        int len = roma.length();
        int maxChunk = 3;
        String kana = "";
        String kanaChar = "";

        if (ignoreCase == null)
        {
            ignoreCase = false;
        }

        while (position < len)
        {
            chunkSize = Math.min(maxChunk, len - position);

            while (chunkSize > 0)
            {
                chunk = roma.substring(position, (position+chunkSize));
                chunkLC = chunk.toLowerCase();

                if ((chunkLC.equals("lts") || chunkLC.equals("xts")) && (len - position) >= 4)
                {
                    chunkSize++;
                    // The second parameter in substring() is an end point, not a length!
                    chunk = roma.substring(position, (position+chunkSize));
                    chunkLC = chunk.toLowerCase();
                }

                if (String.valueOf(chunkLC.charAt(0)).equals("n"))
                {
                    // Convert n' to ん
                    if(mOptions.get(OPTION_IME_MODE) && chunk.length() == 2 && String.valueOf(chunkLC.charAt(1)).equals("'"))
                    {
                        chunkSize = 2;
                        chunk = "nn";
                        chunkLC = chunk.toLowerCase();
                    }
                    // If the user types "nto", automatically convert "n" to "ん" first
                    // "y" is excluded from the list of consonants so we can still get にゃ, にゅ, and にょ
                    if(chunk.length() > 2 && _isCharConsonant(chunkLC.charAt(1), false) && _isCharVowel(chunkLC.charAt(2), true))
                    {
                        chunkSize = 1;
                        // I removed the "n"->"ん" mapping because the IME wouldn't let me type "na" for "な" without returning "んあ",
                        // so the chunk needs to be manually set to a value that will map to "ん"
                        chunk = "nn";
                        chunkLC = chunk.toLowerCase();
                    }
                }

                // Prepare to return a small-つ because we're looking at double-consonants.
                if (chunk.length() > 1 && !String.valueOf(chunkLC.charAt(0)).equals("n") && _isCharConsonant(chunkLC.charAt(0), true) && chunk.charAt(0) == chunk.charAt(1))
                {
                    chunkSize = 1;
                    // Return a small katakana ツ when typing in uppercase
                    if(_isCharInRange(chunk.charAt(0), UPPERCASE_START, UPPERCASE_END))
                    {
                        chunkLC = chunk = "ッ";
                    }
                    else
                    {
                        chunkLC = chunk = "っ";
                    }
                }

                kanaChar = mRtoJ.get(chunkLC);

                if (kanaChar != null)
                {
                    break;
                }

                chunkSize--;
            }

            if (kanaChar == null)
            {
                chunk = _convertPunctuation(String.valueOf(chunk.charAt(0)));
                kanaChar = chunk;
            }

            if (mOptions.get(OPTION_USE_OBSOLETE_KANA))
            {
                if (chunkLC.equals("wi"))
                {
                    kanaChar = "ゐ";
                }
                if (chunkLC.equals("we"))
                {
                    kanaChar = "ゑ";
                }
            }

            if (roma.length() > (position + 1) && mOptions.get(OPTION_IME_MODE) && String.valueOf(chunkLC.charAt(0)).equals("n"))
            {
                if ((String.valueOf(roma.charAt(position + 1)).toLowerCase().equals("y") && position == (len - 2)) || position == (len - 1))
                {
                    kanaChar = String.valueOf(chunk.charAt(0));
                }
            }
            if (!ignoreCase)
            {
                if (_isCharInRange(chunk.charAt(0), UPPERCASE_START, UPPERCASE_END))
                {
                    kanaChar = _hiraganaToKatakana(kanaChar);
                }
            }

            kana += kanaChar;

            position += chunkSize > 0 ? chunkSize : 1;
        }

        return kana;
    }

    private String _convertPunctuation(String input)
    {
        if (input.equals(String.valueOf(('　'))))
        {
            return String.valueOf(' ');
        }

        if (input.equals(String.valueOf('-')))
        {
            return String.valueOf('ー');
        }

        return input;
    }

    /**
     * Returns true if input is entirely hiragana.
     */

    public boolean isHiragana(String input)
    {
        return _allTrue(input, new Command()
        {
            @Override
            public boolean run(String str)
            {
                return _isCharHiragana(str.charAt(0));
            }
        });
    }

    public boolean isKatakana(String input)
    {
        return _allTrue(input, new Command()
        {
            @Override
            public boolean run(String str)
            {
                return _isCharKatakana(str.charAt(0));
            }
        });
    }

    public boolean isKana(String input)
    {
        return _allTrue(input, new Command()
        {
            @Override
            public boolean run(String str)
            {
                return (isHiragana(str)) || (isKatakana(str));
            }
        });
    }

    public boolean isRomaji(String input)
    {
        return _allTrue(input, new Command()
        {
            @Override
            public boolean run(String str)
            {
                return (!isHiragana(str)) && (!isKatakana(str));
            }
        });
    }

    public String toHiragana(String input)
    {
        if (isRomaji(input))
        {
            return _romajiToHiragana(input);
        }

        if (isKatakana(input))
        {
            return _katakanaToHiragana(input);
        }

        return input;
    }

    public String toKatakana(String input)
    {
        if (isHiragana(input))
        {
            return _hiraganaToKatakana(input);
        }

        if (isRomaji(input))
        {
            return _hiraganaToKatakana(_romajiToHiragana(input));
        }

        return input;
    }

    public String toKana(String input)
    {
        return _romajiToKana(input, false);
    }

    public String toRomaji(String input)
    {
        return _hiraganaToRomaji(input);
    }

    private void prepareRtoJ()
    {
        mRtoJ.put("a", "あ");
        mRtoJ.put("i", "い");
        mRtoJ.put("u", "う");
        mRtoJ.put("e", "え");
        mRtoJ.put("o", "お");
        mRtoJ.put("yi", "い");
        mRtoJ.put("wu", "う");
        mRtoJ.put("whu", "う");
        mRtoJ.put("xa", "ぁ");
        mRtoJ.put("xi", "ぃ");
        mRtoJ.put("xu", "ぅ");
        mRtoJ.put("xe", "ぇ");
        mRtoJ.put("xo", "ぉ");
        mRtoJ.put("xyi", "ぃ");
        mRtoJ.put("xye", "ぇ");
        mRtoJ.put("ye", "いぇ");
        mRtoJ.put("wha", "うぁ");
        mRtoJ.put("whi", "うぃ");
        mRtoJ.put("whe", "うぇ");
        mRtoJ.put("who", "うぉ");
        mRtoJ.put("wi", "うぃ");
        mRtoJ.put("we", "うぇ");
        mRtoJ.put("va", "ゔぁ");
        mRtoJ.put("vi", "ゔぃ");
        mRtoJ.put("vu", "ゔ");
        mRtoJ.put("ve", "ゔぇ");
        mRtoJ.put("vo", "ゔぉ");
        mRtoJ.put("vya", "ゔゃ");
        mRtoJ.put("vyi", "ゔぃ");
        mRtoJ.put("vyu", "ゔゅ");
        mRtoJ.put("vye", "ゔぇ");
        mRtoJ.put("vyo", "ゔょ");
        mRtoJ.put("ka", "か");
        mRtoJ.put("ki", "き");
        mRtoJ.put("ku", "く");
        mRtoJ.put("ke", "け");
        mRtoJ.put("ko", "こ");
        mRtoJ.put("lka", "ヵ");
        mRtoJ.put("lke", "ヶ");
        mRtoJ.put("xka", "ヵ");
        mRtoJ.put("xke", "ヶ");
        mRtoJ.put("kya", "きゃ");
        mRtoJ.put("kyi", "きぃ");
        mRtoJ.put("kyu", "きゅ");
        mRtoJ.put("kye", "きぇ");
        mRtoJ.put("kyo", "きょ");
        mRtoJ.put("qya", "くゃ");
        mRtoJ.put("qyu", "くゅ");
        mRtoJ.put("qyo", "くょ");
        mRtoJ.put("qwa", "くぁ");
        mRtoJ.put("qwi", "くぃ");
        mRtoJ.put("qwu", "くぅ");
        mRtoJ.put("qwe", "くぇ");
        mRtoJ.put("qwo", "くぉ");
        mRtoJ.put("qa", "くぁ");
        mRtoJ.put("qi", "くぃ");
        mRtoJ.put("qe", "くぇ");
        mRtoJ.put("qo", "くぉ");
        mRtoJ.put("kwa", "くぁ");
        mRtoJ.put("qyi", "くぃ");
        mRtoJ.put("qye", "くぇ");
        mRtoJ.put("ga", "が");
        mRtoJ.put("gi", "ぎ");
        mRtoJ.put("gu", "ぐ");
        mRtoJ.put("ge", "げ");
        mRtoJ.put("go", "ご");
        mRtoJ.put("gya", "ぎゃ");
        mRtoJ.put("gyi", "ぎぃ");
        mRtoJ.put("gyu", "ぎゅ");
        mRtoJ.put("gye", "ぎぇ");
        mRtoJ.put("gyo", "ぎょ");
        mRtoJ.put("gwa", "ぐぁ");
        mRtoJ.put("gwi", "ぐぃ");
        mRtoJ.put("gwu", "ぐぅ");
        mRtoJ.put("gwe", "ぐぇ");
        mRtoJ.put("gwo", "ぐぉ");
        mRtoJ.put("sa", "さ");
        mRtoJ.put("si", "し");
        mRtoJ.put("shi", "し");
        mRtoJ.put("su", "す");
        mRtoJ.put("se", "せ");
        mRtoJ.put("so", "そ");
        mRtoJ.put("za", "ざ");
        mRtoJ.put("zi", "じ");
        mRtoJ.put("zu", "ず");
        mRtoJ.put("ze", "ぜ");
        mRtoJ.put("zo", "ぞ");
        mRtoJ.put("ji", "じ");
        mRtoJ.put("sya", "しゃ");
        mRtoJ.put("syi", "しぃ");
        mRtoJ.put("syu", "しゅ");
        mRtoJ.put("sye", "しぇ");
        mRtoJ.put("syo", "しょ");
        mRtoJ.put("sha", "しゃ");
        mRtoJ.put("shu", "しゅ");
        mRtoJ.put("she", "しぇ");
        mRtoJ.put("sho", "しょ");
        mRtoJ.put("swa", "すぁ");
        mRtoJ.put("swi", "すぃ");
        mRtoJ.put("swu", "すぅ");
        mRtoJ.put("swe", "すぇ");
        mRtoJ.put("swo", "すぉ");
        mRtoJ.put("zya", "じゃ");
        mRtoJ.put("zyi", "じぃ");
        mRtoJ.put("zyu", "じゅ");
        mRtoJ.put("zye", "じぇ");
        mRtoJ.put("zyo", "じょ");
        mRtoJ.put("ja", "じゃ");
        mRtoJ.put("ju", "じゅ");
        mRtoJ.put("je", "じぇ");
        mRtoJ.put("jo", "じょ");
        mRtoJ.put("jya", "じゃ");
        mRtoJ.put("jyi", "じぃ");
        mRtoJ.put("jyu", "じゅ");
        mRtoJ.put("jye", "じぇ");
        mRtoJ.put("jyo", "じょ");
        mRtoJ.put("ta", "た");
        mRtoJ.put("ti", "ち");
        mRtoJ.put("tu", "つ");
        mRtoJ.put("te", "て");
        mRtoJ.put("to", "と");
        mRtoJ.put("chi", "ち");
        mRtoJ.put("tsu", "つ");
        mRtoJ.put("ltu", "っ");
        mRtoJ.put("xtu", "っ");
        mRtoJ.put("tya", "ちゃ");
        mRtoJ.put("tyi", "ちぃ");
        mRtoJ.put("tyu", "ちゅ");
        mRtoJ.put("tye", "ちぇ");
        mRtoJ.put("tyo", "ちょ");
        mRtoJ.put("cha", "ちゃ");
        mRtoJ.put("chu", "ちゅ");
        mRtoJ.put("che", "ちぇ");
        mRtoJ.put("cho", "ちょ");
        mRtoJ.put("cya", "ちゃ");
        mRtoJ.put("cyi", "ちぃ");
        mRtoJ.put("cyu", "ちゅ");
        mRtoJ.put("cye", "ちぇ");
        mRtoJ.put("cyo", "ちょ");
        mRtoJ.put("tsa", "つぁ");
        mRtoJ.put("tsi", "つぃ");
        mRtoJ.put("tse", "つぇ");
        mRtoJ.put("tso", "つぉ");
        mRtoJ.put("tha", "てゃ");
        mRtoJ.put("thi", "てぃ");
        mRtoJ.put("thu", "てゅ");
        mRtoJ.put("the", "てぇ");
        mRtoJ.put("tho", "てょ");
        mRtoJ.put("twa", "とぁ");
        mRtoJ.put("twi", "とぃ");
        mRtoJ.put("twu", "とぅ");
        mRtoJ.put("twe", "とぇ");
        mRtoJ.put("two", "とぉ");
        mRtoJ.put("da", "だ");
        mRtoJ.put("di", "ぢ");
        mRtoJ.put("du", "づ");
        mRtoJ.put("de", "で");
        mRtoJ.put("do", "ど");
        mRtoJ.put("dya", "ぢゃ");
        mRtoJ.put("dyi", "ぢぃ");
        mRtoJ.put("dyu", "ぢゅ");
        mRtoJ.put("dye", "ぢぇ");
        mRtoJ.put("dyo", "ぢょ");
        mRtoJ.put("dha", "でゃ");
        mRtoJ.put("dhi", "でぃ");
        mRtoJ.put("dhu", "でゅ");
        mRtoJ.put("dhe", "でぇ");
        mRtoJ.put("dho", "でょ");
        mRtoJ.put("dwa", "どぁ");
        mRtoJ.put("dwi", "どぃ");
        mRtoJ.put("dwu", "どぅ");
        mRtoJ.put("dwe", "どぇ");
        mRtoJ.put("dwo", "どぉ");
        mRtoJ.put("na", "な");
        mRtoJ.put("ni", "に");
        mRtoJ.put("nu", "ぬ");
        mRtoJ.put("ne", "ね");
        mRtoJ.put("no", "の");
        mRtoJ.put("nya", "にゃ");
        mRtoJ.put("nyi", "にぃ");
        mRtoJ.put("nyu", "にゅ");
        mRtoJ.put("nye", "にぇ");
        mRtoJ.put("nyo", "にょ");
        mRtoJ.put("ha", "は");
        mRtoJ.put("hi", "ひ");
        mRtoJ.put("hu", "ふ");
        mRtoJ.put("he", "へ");
        mRtoJ.put("ho", "ほ");
        mRtoJ.put("fu", "ふ");
        mRtoJ.put("hya", "ひゃ");
        mRtoJ.put("hyi", "ひぃ");
        mRtoJ.put("hyu", "ひゅ");
        mRtoJ.put("hye", "ひぇ");
        mRtoJ.put("hyo", "ひょ");
        mRtoJ.put("fya", "ふゃ");
        mRtoJ.put("fyu", "ふゅ");
        mRtoJ.put("fyo", "ふょ");
        mRtoJ.put("fwa", "ふぁ");
        mRtoJ.put("fwi", "ふぃ");
        mRtoJ.put("fwu", "ふぅ");
        mRtoJ.put("fwe", "ふぇ");
        mRtoJ.put("fwo", "ふぉ");
        mRtoJ.put("fa", "ふぁ");
        mRtoJ.put("fi", "ふぃ");
        mRtoJ.put("fe", "ふぇ");
        mRtoJ.put("fo", "ふぉ");
        mRtoJ.put("fyi", "ふぃ");
        mRtoJ.put("fye", "ふぇ");
        mRtoJ.put("ba", "ば");
        mRtoJ.put("bi", "び");
        mRtoJ.put("bu", "ぶ");
        mRtoJ.put("be", "べ");
        mRtoJ.put("bo", "ぼ");
        mRtoJ.put("bya", "びゃ");
        mRtoJ.put("byi", "びぃ");
        mRtoJ.put("byu", "びゅ");
        mRtoJ.put("bye", "びぇ");
        mRtoJ.put("byo", "びょ");
        mRtoJ.put("pa", "ぱ");
        mRtoJ.put("pi", "ぴ");
        mRtoJ.put("pu", "ぷ");
        mRtoJ.put("pe", "ぺ");
        mRtoJ.put("po", "ぽ");
        mRtoJ.put("pya", "ぴゃ");
        mRtoJ.put("pyi", "ぴぃ");
        mRtoJ.put("pyu", "ぴゅ");
        mRtoJ.put("pye", "ぴぇ");
        mRtoJ.put("pyo", "ぴょ");
        mRtoJ.put("ma", "ま");
        mRtoJ.put("mi", "み");
        mRtoJ.put("mu", "む");
        mRtoJ.put("me", "め");
        mRtoJ.put("mo", "も");
        mRtoJ.put("mya", "みゃ");
        mRtoJ.put("myi", "みぃ");
        mRtoJ.put("myu", "みゅ");
        mRtoJ.put("mye", "みぇ");
        mRtoJ.put("myo", "みょ");
        mRtoJ.put("ya", "や");
        mRtoJ.put("yu", "ゆ");
        mRtoJ.put("yo", "よ");
        mRtoJ.put("xya", "ゃ");
        mRtoJ.put("xyu", "ゅ");
        mRtoJ.put("xyo", "ょ");
        mRtoJ.put("ra", "ら");
        mRtoJ.put("ri", "り");
        mRtoJ.put("ru", "る");
        mRtoJ.put("re", "れ");
        mRtoJ.put("ro", "ろ");
        mRtoJ.put("rya", "りゃ");
        mRtoJ.put("ryi", "りぃ");
        mRtoJ.put("ryu", "りゅ");
        mRtoJ.put("rye", "りぇ");
        mRtoJ.put("ryo", "りょ");
        mRtoJ.put("la", "ら");
        mRtoJ.put("li", "り");
        mRtoJ.put("lu", "る");
        mRtoJ.put("le", "れ");
        mRtoJ.put("lo", "ろ");
        mRtoJ.put("lya", "りゃ");
        mRtoJ.put("lyi", "りぃ");
        mRtoJ.put("lyu", "りゅ");
        mRtoJ.put("lye", "りぇ");
        mRtoJ.put("lyo", "りょ");
        mRtoJ.put("wa", "わ");
        mRtoJ.put("wo", "を");
        mRtoJ.put("lwe", "ゎ");
        mRtoJ.put("xwa", "ゎ");
        mRtoJ.put("nn", "ん");
        mRtoJ.put("'n '", "ん");
        mRtoJ.put("xn", "ん");
        mRtoJ.put("ltsu", "っ");
        mRtoJ.put("xtsu", "っ");
    }

    private void prepareJtoR()
    {
        mJtoR.put("あ", "a");
        mJtoR.put("い", "i");
        mJtoR.put("う", "u");
        mJtoR.put("え", "e");
        mJtoR.put("お", "o");
        mJtoR.put("ゔぁ", "va");
        mJtoR.put("ゔぃ", "vi");
        mJtoR.put("ゔ", "vu");
        mJtoR.put("ゔぇ", "ve");
        mJtoR.put("ゔぉ", "vo");
        mJtoR.put("か", "ka");
        mJtoR.put("き", "ki");
        mJtoR.put("きゃ", "kya");
        mJtoR.put("きぃ", "kyi");
        mJtoR.put("きゅ", "kyu");
        mJtoR.put("く", "ku");
        mJtoR.put("け", "ke");
        mJtoR.put("こ", "ko");
        mJtoR.put("が", "ga");
        mJtoR.put("ぎ", "gi");
        mJtoR.put("ぐ", "gu");
        mJtoR.put("げ", "ge");
        mJtoR.put("ご", "go");
        mJtoR.put("ぎゃ", "gya");
        mJtoR.put("ぎぃ", "gyi");
        mJtoR.put("ぎゅ", "gyu");
        mJtoR.put("ぎぇ", "gye");
        mJtoR.put("ぎょ", "gyo");
        mJtoR.put("さ", "sa");
        mJtoR.put("す", "su");
        mJtoR.put("せ", "se");
        mJtoR.put("そ", "so");
        mJtoR.put("ざ", "za");
        mJtoR.put("ず", "zu");
        mJtoR.put("ぜ", "ze");
        mJtoR.put("ぞ", "zo");
        mJtoR.put("し", "shi");
        mJtoR.put("しゃ", "sha");
        mJtoR.put("しゅ", "shu");
        mJtoR.put("しょ", "sho");
        mJtoR.put("じ", "ji");
        mJtoR.put("じゃ", "ja");
        mJtoR.put("じゅ", "ju");
        mJtoR.put("じょ", "jo");
        mJtoR.put("た", "ta");
        mJtoR.put("ち", "chi");
        mJtoR.put("ちゃ", "cha");
        mJtoR.put("ちゅ", "chu");
        mJtoR.put("ちょ", "cho");
        mJtoR.put("つ", "tsu");
        mJtoR.put("て", "te");
        mJtoR.put("と", "to");
        mJtoR.put("だ", "da");
        mJtoR.put("ぢ", "di");
        mJtoR.put("づ", "du");
        mJtoR.put("で", "de");
        mJtoR.put("ど", "do");
        mJtoR.put("な", "na");
        mJtoR.put("に", "ni");
        mJtoR.put("にゃ", "nya");
        mJtoR.put("にゅ", "nyu");
        mJtoR.put("にょ", "nyo");
        mJtoR.put("ぬ", "nu");
        mJtoR.put("ね", "ne");
        mJtoR.put("の", "no");
        mJtoR.put("は", "ha");
        mJtoR.put("ひ", "hi");
        mJtoR.put("ふ", "fu");
        mJtoR.put("へ", "he");
        mJtoR.put("ほ", "ho");
        mJtoR.put("ひゃ", "hya");
        mJtoR.put("ひゅ", "hyu");
        mJtoR.put("ひょ", "hyo");
        mJtoR.put("ふぁ", "fa");
        mJtoR.put("ふぃ", "fi");
        mJtoR.put("ふぇ", "fe");
        mJtoR.put("ふぉ", "fo");
        mJtoR.put("ば", "ba");
        mJtoR.put("び", "bi");
        mJtoR.put("ぶ", "bu");
        mJtoR.put("べ", "be");
        mJtoR.put("ぼ", "bo");
        mJtoR.put("びゃ", "bya");
        mJtoR.put("びゅ", "byu");
        mJtoR.put("びょ", "byo");
        mJtoR.put("ぱ", "pa");
        mJtoR.put("ぴ", "pi");
        mJtoR.put("ぷ", "pu");
        mJtoR.put("ぺ", "pe");
        mJtoR.put("ぽ", "po");
        mJtoR.put("ぴゃ", "pya");
        mJtoR.put("ぴゅ", "pyu");
        mJtoR.put("ぴょ", "pyo");
        mJtoR.put("ま", "ma");
        mJtoR.put("み", "mi");
        mJtoR.put("む", "mu");
        mJtoR.put("め", "me");
        mJtoR.put("も", "mo");
        mJtoR.put("みゃ", "mya");
        mJtoR.put("みゅ", "myu");
        mJtoR.put("みょ", "myo");
        mJtoR.put("や", "ya");
        mJtoR.put("ゆ", "yu");
        mJtoR.put("よ", "yo");
        mJtoR.put("ら", "ra");
        mJtoR.put("り", "ri");
        mJtoR.put("る", "ru");
        mJtoR.put("れ", "re");
        mJtoR.put("ろ", "ro");
        mJtoR.put("りゃ", "rya");
        mJtoR.put("りゅ", "ryu");
        mJtoR.put("りょ", "ryo");
        mJtoR.put("わ", "wa");
        mJtoR.put("を", "wo");
        mJtoR.put("ん", "n");
        mJtoR.put("ゐ", "wi");
        mJtoR.put("ゑ", "we");
        mJtoR.put("きぇ", "kye");
        mJtoR.put("きょ", "kyo");
        mJtoR.put("じぃ", "jyi");
        mJtoR.put("じぇ", "jye");
        mJtoR.put("ちぃ", "cyi");
        mJtoR.put("ちぇ", "che");
        mJtoR.put("ひぃ", "hyi");
        mJtoR.put("ひぇ", "hye");
        mJtoR.put("びぃ", "byi");
        mJtoR.put("びぇ", "bye");
        mJtoR.put("ぴぃ", "pyi");
        mJtoR.put("ぴぇ", "pye");
        mJtoR.put("みぇ", "mye");
        mJtoR.put("みぃ", "myi");
        mJtoR.put("りぃ", "ryi");
        mJtoR.put("りぇ", "rye");
        mJtoR.put("にぃ", "nyi");
        mJtoR.put("にぇ", "nye");
        mJtoR.put("しぃ", "syi");
        mJtoR.put("しぇ", "she");
        mJtoR.put("いぇ", "ye");
        mJtoR.put("うぁ", "wha");
        mJtoR.put("うぉ", "who");
        mJtoR.put("うぃ", "wi");
        mJtoR.put("うぇ", "we");
        mJtoR.put("ゔゃ", "vya");
        mJtoR.put("ゔゅ", "vyu");
        mJtoR.put("ゔょ", "vyo");
        mJtoR.put("すぁ", "swa");
        mJtoR.put("すぃ", "swi");
        mJtoR.put("すぅ", "swu");
        mJtoR.put("すぇ", "swe");
        mJtoR.put("すぉ", "swo");
        mJtoR.put("くゃ", "qya");
        mJtoR.put("くゅ", "qyu");
        mJtoR.put("くょ", "qyo");
        mJtoR.put("くぁ", "qwa");
        mJtoR.put("くぃ", "qwi");
        mJtoR.put("くぅ", "qwu");
        mJtoR.put("くぇ", "qwe");
        mJtoR.put("くぉ", "qwo");
        mJtoR.put("ぐぁ", "gwa");
        mJtoR.put("ぐぃ", "gwi");
        mJtoR.put("ぐぅ", "gwu");
        mJtoR.put("ぐぇ", "gwe");
        mJtoR.put("ぐぉ", "gwo");
        mJtoR.put("つぁ", "tsa");
        mJtoR.put("つぃ", "tsi");
        mJtoR.put("つぇ", "tse");
        mJtoR.put("つぉ", "tso");
        mJtoR.put("てゃ", "tha");
        mJtoR.put("てぃ", "thi");
        mJtoR.put("てゅ", "thu");
        mJtoR.put("てぇ", "the");
        mJtoR.put("てょ", "tho");
        mJtoR.put("とぁ", "twa");
        mJtoR.put("とぃ", "twi");
        mJtoR.put("とぅ", "twu");
        mJtoR.put("とぇ", "twe");
        mJtoR.put("とぉ", "two");
        mJtoR.put("ぢゃ", "dya");
        mJtoR.put("ぢぃ", "dyi");
        mJtoR.put("ぢゅ", "dyu");
        mJtoR.put("ぢぇ", "dye");
        mJtoR.put("ぢょ", "dyo");
        mJtoR.put("でゃ", "dha");
        mJtoR.put("でぃ", "dhi");
        mJtoR.put("でゅ", "dhu");
        mJtoR.put("でぇ", "dhe");
        mJtoR.put("でょ", "dho");
        mJtoR.put("どぁ", "dwa");
        mJtoR.put("どぃ", "dwi");
        mJtoR.put("どぅ", "dwu");
        mJtoR.put("どぇ", "dwe");
        mJtoR.put("どぉ", "dwo");
        mJtoR.put("ふぅ", "fwu");
        mJtoR.put("ふゃ", "fya");
        mJtoR.put("ふゅ", "fyu");
        mJtoR.put("ふょ", "fyo");
        mJtoR.put("ぁ", "a");
        mJtoR.put("ぃ", "i");
        mJtoR.put("ぇ", "e");
        mJtoR.put("ぅ", "u");
        mJtoR.put("ぉ", "o");
        mJtoR.put("ゃ", "ya");
        mJtoR.put("ゅ", "yu");
        mJtoR.put("ょ", "yo");
        mJtoR.put("っ", "");
        mJtoR.put("ゕ", "ka");
        mJtoR.put("ゖ", "ka");
        mJtoR.put("ゎ", "wa");
        mJtoR.put("'　'", " ");
        mJtoR.put("んあ", "n'a");
        mJtoR.put("んい", "n'i");
        mJtoR.put("んう", "n'u");
        mJtoR.put("んえ", "n'e");
        mJtoR.put("んお", "n'o");
        mJtoR.put("んや", "n'ya");
        mJtoR.put("んゆ", "n'yu");
        mJtoR.put("んよ", "n'yo");
    }
}