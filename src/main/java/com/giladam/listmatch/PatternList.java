package com.giladam.listmatch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Keeps track of the patterns we want to test against and any matching settings.
 *
 * If you need to manage multiple lists you may prefer to use {@link ListMatcher} to
 * have multiple named lists, or you can manage it yourself any way you like.
 *
 * @author Gil Adam
 *
 */
public class PatternList {

    /**
     * This is a special matching character for the wildcard
     */
    public static final char WILDCARD_CHAR = '*';
    public static final String WILDCARD = String.valueOf(WILDCARD_CHAR);

    private static Logger log = LoggerFactory.getLogger(PatternList.class);

    private Set<PatternListEntry> patterns = Collections.emptySet();

    private Set<String> exactMatches = Collections.emptySet();

    private boolean caseSensitive;

    private String componentDelimiter;


    /**
     * Creates a new PatternList initialized with the patterns given.
     *
     * @param patternsToLoad
     *   Values for patterns to match against.
     * @param componentDelimiter
     *   The delimited to use if a pattern can have multiple components, such as an email with a 'domainpart' and 'localpart'.
     * @param caseSensitive
     *   If true, all matching will be performed in a case-insensitive manner.
     */
    public PatternList(Collection<String> patternsToLoad, String componentDelimiter, boolean caseSensitive) {

        this.caseSensitive = caseSensitive;
        this.componentDelimiter = componentDelimiter;

        if (patternsToLoad == null) {
            return;
        }

        Set<PatternListEntry> initPatterns = new HashSet<>();
        Set<String> initExactMatches = new HashSet<>();

        for (String patternToLoad : patternsToLoad) {

            String normalizedPatternToLoad = normalizePatternToLoad(patternToLoad);

            if (containsSpecialMatchingCharacters(normalizedPatternToLoad)) {

                //special case if it's just a wildcard and nothing else:
                if (normalizedPatternToLoad.equals(WILDCARD)) {
                    initExactMatches.add(WILDCARD);
                } else {
                    PatternListEntry patternListEntry = new PatternListEntry(normalizedPatternToLoad, this.componentDelimiter);
                    initPatterns.add(patternListEntry);
                }
            } else {
                initExactMatches.add(normalizedPatternToLoad);
            }
        }

        this.exactMatches = initExactMatches;
        this.patterns = initPatterns;
    }


    private String normalizePatternToLoad(String patternToLoad) {

        String duplicateWildcardsRemoved = patternToLoad.replaceAll("[*]+", "*");
        String normalized = caseSensitive ? duplicateWildcardsRemoved : StringUtils.upperCase(duplicateWildcardsRemoved);

        return normalized;
    }


    /**
     * Checks if a particular email address matches any pattern.
     *
     * @param emailAddress
     * @return
     */
    public boolean matches(String value) {

        //if there's nothing to check, it can't be in the whitelist
        if (value == null) {
            return false;
        }

        //do not convert to uppercase if case sensitive mode is used:
        final String valueToCheck = caseSensitive ? value : StringUtils.upperCase(value);

        //first just see if we have an exact match:
        if (exactMatches.contains(valueToCheck)) {
            log.debug("List contains matching exact value: {}", valueToCheck);
            return true;
        } else if (exactMatches.contains(WILDCARD)) { //or see if we just match anything by wildcard
            log.debug("List contains wildcard any '{}' pattern so all values match.", WILDCARD);
            return true;
        }


        PatternListEntry valueToTest = new PatternListEntry(valueToCheck, this.componentDelimiter);

        //unfortunately, now we must test each pattern that is not exact:
        for (PatternListEntry listEntry : this.patterns) {

            String[] listEntryComponents = listEntry.getComponents();
            String[] valueToTestComponents = valueToTest.getComponents();

            //if the entry is incompatible with the value to test, it's not a match so don't try:
            if (listEntryComponents.length == valueToTestComponents.length) {

                boolean foundNonMatchingComponent = false;

                for (int i=0; i<valueToTestComponents.length && (!foundNonMatchingComponent); i++) {
                    //does the component match?
                    if (!wildcardMatch(valueToTestComponents[i], listEntryComponents[i])) {
                        foundNonMatchingComponent = true;
                    }
                }

                if (!foundNonMatchingComponent) {
                    if (log.isDebugEnabled()) {
                        log.debug("Found match for [{}] with pattern {}", valueToCheck, listEntry);
                    }

                    return true;
                }
            }
        }

        return false;
    }



    private static boolean containsSpecialMatchingCharacters(String value) {
        return (value != null) && value.contains(WILDCARD);
    }


    /**
     * This wildcard matching was adapted from somebody's solution to wildcard matching problem from Leetcode.  It is
     * not entirely clear who wrote it, but it does work so I changed it a bit for what I needed.
     *
     * @param value
     * @param pattern
     * @return
     */
    private boolean wildcardMatch(String value, String pattern) {

        if (value == null || pattern == null) {
            return false;
        }

        if (value.equals(pattern) || pattern.equals(WILDCARD)) {
            return true;
        }

        int m = value.length();
        int n = pattern.length();
        int posS = 0;
        int posP = 0;
        int posStar = -1;
        int posOfS = -1;

        //if posS == posP || ++posS and ++posP.
        //posOfS, posStar, record the positon of '*' in s and p, ++posP and go on.
        //if not match, go back to star, ++posOfS
        while (posS < m) {
            if (posP < n && (value.charAt(posS) == pattern.charAt(posP))) {
                ++posS;
                ++posP;
            } else if (posP < n && pattern.charAt(posP) == WILDCARD_CHAR) {
                posStar = posP;
                posOfS = posS;
                ++posP;
                continue;
            } else if (posStar != -1) {
                posS = posOfS;
                posP = posStar + 1;
                ++posOfS;
            } else {
                return false;
            }
        }

        while (posP < n && pattern.charAt(posP) == WILDCARD_CHAR) {
            ++posP;
        }

        return ((posS == m) && (posP == n));
    }

}
