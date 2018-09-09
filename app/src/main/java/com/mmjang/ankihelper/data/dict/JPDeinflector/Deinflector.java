package com.mmjang.ankihelper.data.dict.JPDeinflector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mmjang.ankihelper.data.dict.JPDeinflector.Inflections.*;

/**
 * Provides a static API for removing inflections from Japanese words.
 *
 * @author James Kirk
 */
public class Deinflector {

    /**
     * Ensures non-instantiability.
     */
    private Deinflector() {
    }

    /**
     * Finds all potentially valid deinflections for a given term.
     *
     * @param inflectedWord the initial inflected term
     * @return list of deinflected terms with inflection type data
     */
    public static List<Deinflection> deinflect(String inflectedWord) {
        final List<Deinflection> terms = new ArrayList<>();
        terms.add(new Deinflection(inflectedWord, inflectedWord, -1, -1));

        /* The order is important here */
        deinflectRegular(terms, ADJECTIVE_INFLECTIONS, InflectionType.ADJECTIVE, true);
        deinflectRegular(terms, ICHIDAN_INFLECTIONS, InflectionType.ICHIDAN, true);
        deinflectRegular(terms, GODAN_INFLECTIONS, InflectionType.GODAN, false);
        deinflectIrregular(terms, SURU_INFLECTIONS, InflectionType.SURU);
        deinflectIrregular(terms, KURU_INFLECTIONS, InflectionType.KURU);
        deinflectIrregular(terms, SPECIAL_INFLECTIONS, InflectionType.SPECIAL);
        deinflectIrregular(terms, IKU_INFLECTIONS, InflectionType.IKU);

        return filterBogusEndings(terms);
    }

    /**
     * Tries to match regular inflections against the end of a word.
     *
     * @param terms the list of deinflections so far
     * @param inflections the inflections to test against
     * @param inflectionType the type of the inflections
     * @param processAsAdded true if we should process terms added in this method
     */
    private static void deinflectRegular(List<Deinflection> terms, List<Inflection> inflections,
                                         int inflectionType, boolean processAsAdded) {
        int initialSize = terms.size();
        for (int i = 0; i < terms.size(); i++) {

            if (!processAsAdded && i >= initialSize)
                break;

            Deinflection deinflection = terms.get(i);

            for (Inflection inflection : inflections) {
                final String deinflectedWord = deinflectWord(deinflection.getBaseForm(), inflection);

                if (deinflection.getInflectionType() == InflectionType.ADJECTIVE
                        && !isAuxAdjective(inflection.getForm())) {
                    continue;
                } else if (deinflectedWord != null &&
                        (!(inflectionType == InflectionType.ICHIDAN)
                                || hasIchidanEnding(deinflectedWord))) {
                    terms.add(new Deinflection(
                            deinflection.getBaseForm(), deinflectedWord,
                            inflection.getForm(), inflectionType));
                }
            }
        }
    }

    /**
     * Tries to match irregular inflections against the
     * entire term string.
     *
     * @param terms the list of deinflections so far
     * @param inflections the inflections to test against
     * @param inflectionType the type of the inflections
     */
    private static void deinflectIrregular(List<Deinflection> terms, List<Inflection> inflections, int inflectionType) {
        int initialSize = terms.size();
        for (int i = 0; i < initialSize; i++) {//Use cached size to avoid processing newly added terms

            final Deinflection deinflection = terms.get(i);

            for (Inflection inflection : inflections) {
                Matcher matcher = Pattern.compile(inflection.getInflection())
                        .matcher(deinflection.getBaseForm());
                if (matcher.matches()) {
                    String word = inflection.getBase();
                    if (matcher.groupCount() == 1)
                        word = deinflection.getBaseForm().replace(matcher.group(1), word);

                    terms.add(new Deinflection(
                            deinflection.getBaseForm(), word, inflection.getForm(), inflectionType));
                }
            }
        }
    }

    /**
     * Remove duplicates and terms with non-existent verb endings.
     *
     * @param terms a list of deinflections
     * @return list of filtered terms
     */
    public static List<Deinflection> filterBogusEndings(List<Deinflection> terms) {
        for (Iterator<Deinflection> iterator = terms.iterator(); iterator.hasNext(); ) {
            final Deinflection deinflection = iterator.next();

            if (deinflection.getBaseForm().equals(deinflection.getInflectedWord())) {
                iterator.remove();
                continue;
            }

            for (String bogusEnding : BOGUS_INFLECTIONS)
                if (containsInflection(deinflection.getBaseForm(), bogusEnding))
                    iterator.remove();
        }
        return new ArrayList<>(new HashSet<>(terms));
    }

    /**
     * Remove an inflection from a word.
     *
     * @param inflectedWord the word to be deinflected
     * @param inflection    the inflection to remove
     * @return the base form of the word
     */
    private static String deinflectWord(String inflectedWord, Inflection inflection) {
        if (containsInflection(inflectedWord, inflection.getInflection())) {
            final int endIndex = inflectedWord.length() - inflection.getInflection().length();
            final String baseWord = inflectedWord.substring(0, endIndex) + inflection.getBase();

            if (baseWord.length() > 1)
                return baseWord;
        }
        return null;
    }

    /**
     * Look for a given inflection ending in a given word.
     *
     * @param inflectedWord the word to check
     * @param inflection    the inflection to check for
     * @return true if the word contains the inflected ending
     */
    private static boolean containsInflection(String inflectedWord, String inflection) {
        final int inflectionLength = inflection.length();

        if (inflectionLength > inflectedWord.length())
            return false;
        else {
            final int from = inflectedWord.length() - inflectionLength;
            final String end = inflectedWord.substring(from);
            return end.equals(inflection);
        }
    }
}
