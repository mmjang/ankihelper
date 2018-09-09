package com.mmjang.ankihelper.data.dict.JPDeinflector;

import java.util.*;

import static com.mmjang.ankihelper.data.dict.JPDeinflector.Inflections.Form.*;

/**
 * This class contains constants and utility methods for
 * removing inflections from Japanese words.
 *
 * @author James Kirk
 */
public final class Inflections {

//TODO ① Separate the inflections for くれる・ある・いらっしゃる
//TODO ② Add inflections: −とく, −ちまう, −かっ[た], −られ[る]、させ[る], -さす（聞かすなど）

    /**
     * Groups of related inflections.
     */
    static final List<Inflection> GODAN_INFLECTIONS;
    static final List<Inflection> ICHIDAN_INFLECTIONS;
    static final List<Inflection> ADJECTIVE_INFLECTIONS;
    static final List<Inflection> SURU_INFLECTIONS;
    static final List<Inflection> KURU_INFLECTIONS;
    static final List<Inflection> IKU_INFLECTIONS;
    static final List<Inflection> SPECIAL_INFLECTIONS;

    /**
     * Contains non-existent verb endings. Checked against JMDict.
     */
    static final List<String> BOGUS_INFLECTIONS;

    /**
     * Matches い/え-column kana, e.g. いきシチ, えけセテ
     */
    private static final String ICHIDAN_REGEX;

    /**
     * Classifications for the inflection type of a word.
     */
    public static final class InflectionType {
        private InflectionType() {}

        public static final int UNINFLECTABLE = 0;
        public static final int GODAN = 1;
        public static final int ICHIDAN = 2;
        public static final int IKU = 3;
        public static final int KURU = 4;
        public static final int SURU = 5;
        public static final int ADJECTIVE = 6;
        public static final int KURERU = 7;
        public static final int SPECIAL_RU = 8;
        public static final int SPECIAL_ARU = 9;
        public static final int SPECIAL = 10;
    }

    /**
     * Values to identify forms of inflections.
     */
    public static final class Form {
        private Form() {}

        public static final int POLITE_PAST_NEGATIVE = 0;
        public static final int POLITE_NEGATIVE = 1;
        public static final int POLITE_VOLITIONAL = 2;
        public static final int POLITE_PAST = 3;
        public static final int CHAU = 4;
        public static final int SUGIRU = 5;
        public static final int NASAI = 6;
        public static final int TARA = 7;
        public static final int TARI = 8;
        public static final int CAUSATIVE = 9;
        public static final int POTENTIAL_OR_PASSIVE = 10;
        public static final int SOU = 11;
        public static final int TAI = 12;
        public static final int POLITE = 13;
        public static final int PAST = 14;
        public static final int NEGATIVE = 15;
        public static final int PASSIVE = 16;
        public static final int BA = 17;
        public static final int VOLITIONAL = 18;
        public static final int POTENTIAL = 19;
        public static final int PASSIVE_OR_CAUSATIVE = 20;
        public static final int TE = 21;
        public static final int ZU = 22;
        public static final int IMPERATIVE = 23;
        public static final int MASU_STEM = 24;
        public static final int ADV = 25;
        public static final int NOUN = 26;
        public static final int IMPERATIVE_NEGATIVE = 27;
        public static final int CHIMAU = 28;
    }

    /**
     * Ensures non-instantiability.
     */
    private Inflections() {
    }

    /**
     * @param word word that may be an ichidan verb
     * @return true if penultimate char is い/え-column kana
     */
    public static boolean hasIchidanEnding(String word) {
        if (word.length() < 2)
            return false;

        String s = word.substring(word.length() - 2, word.length() - 1);
        return s.matches(ICHIDAN_REGEX);
    }

    /**
     * @param form the form to be checked
     * @return true if form matches one of the auxiliary adjective forms
     */
    public static boolean isAuxAdjective(int form) {
        return form == Form.TAI || form == Form.SOU || form == Form.NEGATIVE;
    }

    static {
        ICHIDAN_REGEX = "[い|き|ぎ|し|じ|ち|ぢ|に|ひ|び|ぴ|み|り|" +
                "イ|キ|ギ|シ|ジ|チ|ヂ|ニ|ヒ|ビ|ピ|ミ|リ|" +
                "え|け|げ|せ|ぜ|て|で|ね|へ|べ|ぺ|め|れ|" +
                "エ|ケ|ゲ|セ|ゼ|テ|デ|ネ|ヘ|ベ|ペ|メ|レ]";

        BOGUS_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                "あつ", "あぐ", "あす", "あず", "あぬ", "あふ", "あぶ",
                "いつ",
                "えつ",
                "きう", "くなかう", "くなかる",
                "させられる",
                "しつ", "しう",
                "じう", "じつ",
                "たいる", "たかつ", "たくなう",
                "ちゃつ",
                "てう", "てつ",
                "っつ", "ってる",
                "です",
                "ないる", "なかつ", "なさう", "さいる",
                "ましる",
                "っる",
                "べう", "べす", "べつ",
                "やつ",
                "らる",
                "んでしる"
        ));

        SURU_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                new Inflection("しませんでした", "する", POLITE_PAST_NEGATIVE),
                new Inflection("しましょう", "する", POLITE_VOLITIONAL),
                new Inflection("した", "する", PAST),
                new Inflection("して", "する", TE),
                new Inflection("しろ", "する", IMPERATIVE),
                new Inflection("せず", "する", ZU),
                new Inflection("せよ", "する", IMPERATIVE),
                new Inflection("しすぎる", "する", SUGIRU),
                new Inflection("しちゃう", "する", CHAU),
                new Inflection("しなさい", "する", NASAI),
                new Inflection("しました", "する", POLITE_PAST),
                new Inflection("しません", "する", POLITE_NEGATIVE),
                new Inflection("させる", "する", CAUSATIVE),
                new Inflection("される", "する", PASSIVE),
                new Inflection("しそう", "する", SOU),
                new Inflection("したい", "する", TAI),
                new Inflection("したら", "する", TARA),
                new Inflection("したり", "する", TARI),
                new Inflection("しない", "する", NEGATIVE),
                new Inflection("します", "する", POLITE),
                new Inflection("しよう", "する", VOLITIONAL)
        ));

        KURU_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                new Inflection(".*(来すぎる)", "来る", SUGIRU),
                new Inflection(".*(来ませんでした)", "来る", POLITE_PAST_NEGATIVE),
                new Inflection(".*(来ましょう)", "来る", POLITE_VOLITIONAL),
                new Inflection(".*(来ちゃう)", "来る", CHAU),
                new Inflection(".*(来なさい)", "来る", NASAI),
                new Inflection(".*(来ました)", "来る", POLITE_PAST),
                new Inflection(".*(来ません)", "来る", POLITE_NEGATIVE),
                new Inflection(".*(来させる)", "来る", CAUSATIVE),
                new Inflection(".*(来られる)", "来る", POTENTIAL_OR_PASSIVE),
                new Inflection(".*(来そう)", "来る", SOU),
                new Inflection(".*(来たい)", "来る", TAI),
                new Inflection(".*(来たら)", "来る", TARA),
                new Inflection(".*(来たり)", "来る", TARI),
                new Inflection(".*(来ます)", "来る", POLITE),
                new Inflection(".*(来ない)", "来る", NEGATIVE),
                new Inflection(".*(来よう)", "来る", VOLITIONAL),
                new Inflection(".*(来れる)", "来る", POTENTIAL),
                new Inflection(".*(来た)", "来る", PAST),
                new Inflection(".*(来て)", "来る", TE),
                new Inflection(".*(来い)", "来る", IMPERATIVE),
                new Inflection(".*(来ず)", "来る", ZU),

                new Inflection(".*(きすぎる)", "くる", SUGIRU),
                new Inflection(".*(きませんでした)", "くる", POLITE_PAST_NEGATIVE),
                new Inflection(".*(きましょう)", "くる", POLITE_VOLITIONAL),
                new Inflection(".*(きちゃう)", "くる", CHAU),
                new Inflection(".*(きなさい)", "くる", NASAI),
                new Inflection(".*(きました)", "くる", POLITE_PAST),
                new Inflection(".*(きません)", "くる", POLITE_NEGATIVE),
                new Inflection(".*(こさせる)", "くる", CAUSATIVE),
                new Inflection(".*(こられる)", "くる", POTENTIAL_OR_PASSIVE),
                new Inflection(".*(きそう)", "くる", SOU),
                new Inflection(".*(きたい)", "くる", TAI),
                new Inflection(".*(きたら)", "くる", TARA),
                new Inflection(".*(きたり)", "くる", TARI),
                new Inflection(".*(きます)", "くる", POLITE),
                new Inflection(".*(こない)", "くる", NEGATIVE),
                new Inflection(".*(こよう)", "くる", VOLITIONAL),
                new Inflection(".*(これる)", "くる", POTENTIAL),
                new Inflection(".*(きた)", "くる", PAST),
                new Inflection(".*(きて)", "くる", TE),
                new Inflection(".*(こい)", "くる", IMPERATIVE),
                new Inflection(".*(こず)", "くる", ZU)

        ));

        IKU_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                new Inflection(".*[い|ゆ|行|征|逝|往](って)", "く", TE),
                new Inflection(".*[い|ゆ|行|征|逝|往](った)", "く", PAST),
                new Inflection(".*[い|ゆ|行|征|逝|往](っちゃう)", "く", CHAU),
                new Inflection(".*[い|ゆ|行|征|逝|往](ったら)", "く", TARA),
                new Inflection(".*[い|ゆ|行|征|逝|往](ったり)", "く", TARI),
                new Inflection(".*[い|ゆ|行|征|逝|往](きませんでした)", "く", POLITE_PAST_NEGATIVE),
                new Inflection(".*[い|ゆ|行|征|逝|往](きましょう)", "く", POLITE_VOLITIONAL),
                new Inflection(".*[い|ゆ|行|征|逝|往](け)", "く", IMPERATIVE),
                new Inflection(".*[い|ゆ|行|征|逝|往](かず)", "く", ZU),
                new Inflection(".*[い|ゆ|行|征|逝|往](きすぎる)", "く", SUGIRU),
                new Inflection(".*[い|ゆ|行|征|逝|往](きなさい)", "く", NASAI),
                new Inflection(".*[い|ゆ|行|征|逝|往](きました)", "く", POLITE_PAST),
                new Inflection(".*[い|ゆ|行|征|逝|往](きません)", "く", POLITE_NEGATIVE),
                new Inflection(".*[い|ゆ|行|征|逝|往](かせる)", "く", CAUSATIVE),
                new Inflection(".*[い|ゆ|行|征|逝|往](かれる)", "く", PASSIVE),
                new Inflection(".*[い|ゆ|行|征|逝|往](きそう)", "く", SOU),
                new Inflection(".*[い|ゆ|行|征|逝|往](きたい)", "く", TAI),
                new Inflection(".*[い|ゆ|行|征|逝|往](かない)", "く", NEGATIVE),
                new Inflection(".*[い|ゆ|行|征|逝|往](きます)", "く", POLITE),
                new Inflection(".*[い|ゆ|行|征|逝|往](こう)", "く", VOLITIONAL)
        ));

        SPECIAL_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                new Inflection(".*[訪|問|と](うて)", "う", TE),
                new Inflection(".*[訪|問|と](うた)", "う", PAST),
                new Inflection(".*[乞|恋|請|こ](うて)", "う", TE),
                new Inflection(".*[乞|恋|請|こ](うた)", "う", PAST)
        ));

        ICHIDAN_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                new Inflection("ません", "る", POLITE_NEGATIVE),
                new Inflection("ました", "る", POLITE_PAST),
                new Inflection("らせる", "る", CAUSATIVE),
                new Inflection("らない", "る", NEGATIVE),
                new Inflection("られる", "る", POTENTIAL_OR_PASSIVE),
                new Inflection("られ", "る", POTENTIAL_OR_PASSIVE),
                new Inflection("たら", "る", TARA),
                new Inflection("たり", "る", TARI),
                new Inflection("そう", "る", SOU),
                new Inflection("たい", "る", TAI),
                new Inflection("ない", "る", NEGATIVE),
                new Inflection("ます", "る", POLITE),
                new Inflection("よう", "る", VOLITIONAL),
                new Inflection("れば", "る", BA),
                new Inflection("ず", "る", ZU),
                new Inflection("て", "る", TE),
                new Inflection("ろ", "る", IMPERATIVE),
                new Inflection("た", "る", PAST),
                new Inflection("ちゃう", "る", CHAU),
                new Inflection("ませんでした", "る", POLITE_PAST_NEGATIVE),
                new Inflection("ましょう", "る", POLITE_VOLITIONAL),
                new Inflection("させる", "る", CAUSATIVE),
                new Inflection("すぎる", "る", SUGIRU),
                new Inflection("なさい", "る", NASAI)
        ));

        ADJECTIVE_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                new Inflection("くありませんでした", "い", POLITE_PAST_NEGATIVE),
                new Inflection("くありません", "い", POLITE_NEGATIVE),
                new Inflection("かったら", "い", TARA),
                new Inflection("かったり", "い", TARI),
                new Inflection("かった", "い", PAST),
                new Inflection("くない", "い", NEGATIVE),
                new Inflection("すぎる", "い", SUGIRU),
                new Inflection("ければ", "い", BA),
                new Inflection("くて", "い", TE),
                new Inflection("そう", "い", SOU),
                new Inflection("く", "い", ADV),
                new Inflection("さ", "い", NOUN)
        ));

        GODAN_INFLECTIONS = Collections.unmodifiableList(Arrays.asList(
                new Inflection("いませんでした", "う", POLITE_PAST_NEGATIVE),
                new Inflection("きませんでした", "く", POLITE_PAST_NEGATIVE),
                new Inflection("ぎませんでした", "ぐ", POLITE_PAST_NEGATIVE),
                new Inflection("しませんでした", "す", POLITE_PAST_NEGATIVE),
                new Inflection("ちませんでした", "つ", POLITE_PAST_NEGATIVE),
                new Inflection("にませんでした", "ぬ", POLITE_PAST_NEGATIVE),
                new Inflection("びませんでした", "ぶ", POLITE_PAST_NEGATIVE),
                new Inflection("みませんでした", "む", POLITE_PAST_NEGATIVE),
                new Inflection("りませんでした", "る", POLITE_PAST_NEGATIVE),
                new Inflection("いましょう", "う", POLITE_VOLITIONAL),
                new Inflection("きましょう", "く", POLITE_VOLITIONAL),
                new Inflection("ぎましょう", "ぐ", POLITE_VOLITIONAL),
                new Inflection("しましょう", "す", POLITE_VOLITIONAL),
                new Inflection("ちましょう", "つ", POLITE_VOLITIONAL),
                new Inflection("にましょう", "ぬ", POLITE_VOLITIONAL),
                new Inflection("びましょう", "ぶ", POLITE_VOLITIONAL),
                new Inflection("みましょう", "む", POLITE_VOLITIONAL),
                new Inflection("りましょう", "る", POLITE_VOLITIONAL),
                new Inflection("いじゃう", "ぐ", CHAU),
                new Inflection("いすぎる", "う", SUGIRU),
                new Inflection("いちゃう", "く", CHAU),
                new Inflection("いなさい", "う", NASAI),
                new Inflection("いました", "う", POLITE_PAST),
                new Inflection("いません", "う", POLITE_NEGATIVE),
                new Inflection("きすぎる", "く", SUGIRU),
                new Inflection("ぎすぎる", "ぐ", SUGIRU),
                new Inflection("きなさい", "く", NASAI),
                new Inflection("ぎなさい", "ぐ", NASAI),
                new Inflection("きました", "く", POLITE_PAST),
                new Inflection("ぎました", "ぐ", POLITE_PAST),
                new Inflection("きません", "く", POLITE_NEGATIVE),
                new Inflection("ぎません", "ぐ", POLITE_NEGATIVE),
                new Inflection("しすぎる", "す", SUGIRU),
                new Inflection("しちゃう", "す", CHAU),
                new Inflection("しなさい", "す", NASAI),
                new Inflection("しました", "す", POLITE_PAST),
                new Inflection("しません", "す", POLITE_NEGATIVE),
                new Inflection("ちすぎる", "つ", SUGIRU),
                new Inflection("ちなさい", "つ", NASAI),
                new Inflection("ちました", "つ", POLITE_PAST),
                new Inflection("ちません", "つ", POLITE_NEGATIVE),
                new Inflection("っちゃう", "う", CHAU),
                new Inflection("っちゃう", "つ", CHAU),
                new Inflection("っちゃう", "る", CHAU),
                new Inflection("にすぎる", "ぬ", SUGIRU),
                new Inflection("になさい", "ぬ", NASAI),
                new Inflection("にました", "ぬ", POLITE_PAST),
                new Inflection("にません", "ぬ", POLITE_NEGATIVE),
                new Inflection("びすぎる", "ぶ", SUGIRU),
                new Inflection("びなさい", "ぶ", NASAI),
                new Inflection("びました", "ぶ", POLITE_PAST),
                new Inflection("びません", "ぶ", POLITE_NEGATIVE),
                new Inflection("みすぎる", "む", SUGIRU),
                new Inflection("みなさい", "む", NASAI),
                new Inflection("みました", "む", POLITE_PAST),
                new Inflection("みません", "む", POLITE_NEGATIVE),
                new Inflection("りすぎる", "る", SUGIRU),
                new Inflection("りなさい", "る", NASAI),
                new Inflection("りました", "る", POLITE_PAST),
                new Inflection("りません", "る", POLITE_NEGATIVE),
                new Inflection("んじゃう", "ぬ", CHAU),
                new Inflection("んじゃう", "ぶ", CHAU),
                new Inflection("んじゃう", "む", CHAU),
                new Inflection("いそう", "う", SOU),
                new Inflection("いたい", "う", TAI),
                new Inflection("いたら", "く", TARA),
                new Inflection("いだら", "ぐ", TARA),
                new Inflection("いたり", "く", TARI),
                new Inflection("いだり", "ぐ", TARI),
                new Inflection("います", "う", POLITE),
                new Inflection("かせる", "く", CAUSATIVE),
                new Inflection("がせる", "ぐ", CAUSATIVE),
                new Inflection("かない", "く", NEGATIVE),
                new Inflection("がない", "ぐ", NEGATIVE),
                new Inflection("かれる", "く", PASSIVE),
                new Inflection("がれる", "ぐ", PASSIVE),
                new Inflection("きそう", "く", SOU),
                new Inflection("ぎそう", "ぐ", SOU),
                new Inflection("きたい", "く", TAI),
                new Inflection("ぎたい", "ぐ", TAI),
                new Inflection("きます", "く", POLITE),
                new Inflection("ぎます", "ぐ", POLITE),
                new Inflection("させる", "す", CAUSATIVE),
                new Inflection("さない", "す", NEGATIVE),
                new Inflection("される", "す", PASSIVE_OR_CAUSATIVE),
                new Inflection("しそう", "す", SOU),
                new Inflection("したい", "す", TAI),
                new Inflection("したら", "す", TARA),
                new Inflection("したり", "す", TARI),
                new Inflection("します", "す", POLITE),
                new Inflection("たせる", "つ", CAUSATIVE),
                new Inflection("たない", "つ", NEGATIVE),
                new Inflection("たれる", "つ", PASSIVE),
                new Inflection("ちそう", "つ", SOU),
                new Inflection("ちたい", "つ", TAI),
                new Inflection("ちます", "つ", POLITE),
                new Inflection("ったら", "う", TARA),
                new Inflection("ったら", "つ", TARA),
                new Inflection("ったら", "る", TARA),
                new Inflection("ったり", "う", TARI),
                new Inflection("ったり", "つ", TARI),
                new Inflection("ったり", "る", TARI),
                new Inflection("なせる", "ぬ", CAUSATIVE),
                new Inflection("なない", "ぬ", NEGATIVE),
                new Inflection("なれる", "ぬ", PASSIVE),
                new Inflection("にそう", "ぬ", SOU),
                new Inflection("にたい", "ぬ", TAI),
                new Inflection("にます", "ぬ", POLITE),
                new Inflection("ばせる", "ぶ", CAUSATIVE),
                new Inflection("ばない", "ぶ", NEGATIVE),
                new Inflection("ばれる", "ぶ", PASSIVE),
                new Inflection("びそう", "ぶ", SOU),
                new Inflection("びたい", "ぶ", TAI),
                new Inflection("びます", "ぶ", POLITE),
                new Inflection("ませる", "む", CAUSATIVE),
                new Inflection("まない", "む", NEGATIVE),
                new Inflection("まれる", "む", PASSIVE),
                new Inflection("みそう", "む", SOU),
                new Inflection("みたい", "む", TAI),
                new Inflection("みます", "む", POLITE),
                new Inflection("りそう", "る", SOU),
                new Inflection("りたい", "る", TAI),
                new Inflection("ります", "る", POLITE),
                new Inflection("わせる", "う", CAUSATIVE),
                new Inflection("わない", "う", NEGATIVE),
                new Inflection("われる", "う", PASSIVE),
                new Inflection("んだら", "ぬ", TARA),
                new Inflection("んだら", "ぶ", TARA),
                new Inflection("んだら", "む", TARA),
                new Inflection("んだり", "ぬ", TARI),
                new Inflection("んだり", "ぶ", TARI),
                new Inflection("んだり", "む", TARI),
                new Inflection("いた", "く", PAST),
                new Inflection("いだ", "ぐ", PAST),
                new Inflection("いて", "く", TE),
                new Inflection("いで", "ぐ", TE),
                new Inflection("えば", "う", BA),
                new Inflection("える", "う", POTENTIAL),
                new Inflection("おう", "う", VOLITIONAL),
                new Inflection("かず", "く", ZU),
                new Inflection("がず", "ぐ", ZU),
                new Inflection("けば", "く", BA),
                new Inflection("げば", "ぐ", BA),
                new Inflection("ける", "く", POTENTIAL),
                new Inflection("げる", "ぐ", POTENTIAL),
                new Inflection("こう", "く", VOLITIONAL),
                new Inflection("ごう", "ぐ", VOLITIONAL),
                new Inflection("さず", "す", ZU),
                new Inflection("した", "す", PAST),
                new Inflection("して", "す", TE),
                new Inflection("せば", "す", BA),
                new Inflection("せる", "す", POTENTIAL),
                new Inflection("そう", "す", VOLITIONAL),
                new Inflection("たず", "つ", ZU),
                new Inflection("った", "う", PAST),
                new Inflection("った", "つ", PAST),
                new Inflection("った", "る", PAST),
                new Inflection("って", "う", TE),
                new Inflection("って", "つ", TE),
                new Inflection("って", "る", TE),
                new Inflection("てば", "つ", BA),
                new Inflection("てる", "つ", POTENTIAL),
                new Inflection("とう", "つ", VOLITIONAL),
                new Inflection("なず", "ぬ", ZU),
                new Inflection("ねば", "ぬ", BA),
                new Inflection("ねる", "ぬ", POTENTIAL),
                new Inflection("のう", "ぬ", VOLITIONAL),
                new Inflection("ばず", "ぶ", ZU),
                new Inflection("べば", "ぶ", BA),
                new Inflection("べる", "ぶ", POTENTIAL),
                new Inflection("ぼう", "ぶ", VOLITIONAL),
                new Inflection("まず", "む", ZU),
                new Inflection("めば", "む", BA),
                new Inflection("める", "む", POTENTIAL),
                new Inflection("もう", "む", VOLITIONAL),
                new Inflection("らず", "る", ZU),
                new Inflection("れば", "る", BA),
                new Inflection("れる", "る", POTENTIAL),
                new Inflection("ろう", "る", VOLITIONAL),
                new Inflection("わず", "う", ZU),
                new Inflection("んだ", "ぬ", PAST),
                new Inflection("んだ", "ぶ", PAST),
                new Inflection("んだ", "む", PAST),
                new Inflection("んで", "ぬ", TE),
                new Inflection("んで", "ぶ", TE),
                new Inflection("んで", "む", TE),
                new Inflection("い", "いる", MASU_STEM),
                new Inflection("い", "う", MASU_STEM),
                new Inflection("え", "う", IMPERATIVE),
                new Inflection("え", "える", MASU_STEM),
                new Inflection("き", "きる", MASU_STEM),
                new Inflection("き", "く", MASU_STEM),
                new Inflection("ぎ", "ぎる", MASU_STEM),
                new Inflection("ぎ", "ぐ", MASU_STEM),
                new Inflection("け", "く", IMPERATIVE),
                new Inflection("け", "ける", MASU_STEM),
                new Inflection("げ", "ぐ", IMPERATIVE),
                new Inflection("げ", "げる", MASU_STEM),
                new Inflection("し", "す", MASU_STEM),
                new Inflection("じ", "じる", MASU_STEM),
                new Inflection("せ", "す", IMPERATIVE),
                new Inflection("せ", "せる", MASU_STEM),
                new Inflection("ぜ", "ぜる", MASU_STEM),
                new Inflection("ち", "ちる", MASU_STEM),
                new Inflection("ち", "つ", MASU_STEM),
                new Inflection("て", "つ", IMPERATIVE),
                new Inflection("て", "てる", MASU_STEM),
                new Inflection("で", "でる", MASU_STEM),
                new Inflection("に", "にる", MASU_STEM),
                new Inflection("に", "ぬ", MASU_STEM),
                new Inflection("ね", "ぬ", IMPERATIVE),
                new Inflection("ね", "ねる", MASU_STEM),
                new Inflection("ひ", "ひる", MASU_STEM),
                new Inflection("び", "びる", MASU_STEM),
                new Inflection("び", "ぶ", MASU_STEM),
                new Inflection("へ", "へる", MASU_STEM),
                new Inflection("べ", "ぶ", IMPERATIVE),
                new Inflection("べ", "べる", MASU_STEM),
                new Inflection("み", "みる", MASU_STEM),
                new Inflection("み", "む", MASU_STEM),
                new Inflection("め", "む", IMPERATIVE),
                new Inflection("め", "める", MASU_STEM),
                //new Inflection("よ", "る", IMPERATIVE), //FIXME: ???
                new Inflection("り", "りる", MASU_STEM),
                new Inflection("り", "る", MASU_STEM),
                new Inflection("れ", "る", IMPERATIVE),
                new Inflection("れ", "れる", MASU_STEM),
                new Inflection("れ", "れる", MASU_STEM),

                /* Duplicated from Ichidan inflections to avoid conflicts */
                new Inflection("らせる", "る", CAUSATIVE),
                new Inflection("らない", "る", NEGATIVE),
                new Inflection("られる", "る", POTENTIAL_OR_PASSIVE),
                new Inflection("られ", "る", POTENTIAL_OR_PASSIVE),
                new Inflection("れば", "る", BA)
        ));
    }
}
