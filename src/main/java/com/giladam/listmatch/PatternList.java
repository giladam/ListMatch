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
    public static final String WILDCARD = "*";

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

            //if the entry is incompatible with the value to test, it's not a match:
            if (listEntryComponents.length != valueToTestComponents.length) {
                return false;
            }

            boolean allPartsMatch = true;
            for (int i=0; i<valueToTestComponents.length; i++) {
                boolean componentMatches = matchesPattern(valueToTestComponents[i], listEntryComponents[i]);
                if (!componentMatches) {
                    return false;
                }
            }

            if (allPartsMatch) {
                if (log.isDebugEnabled()) {
                    log.debug("Found match for [{}] with pattern {}", valueToCheck, listEntry);
                }

                return true;
            }
        }

        return false;
    }


    private static boolean containsSpecialMatchingCharacters(String value) {
        return (value != null) && value.contains(WILDCARD);
    }


    private boolean matchesPattern(String value, String pattern) {

        //if the pattern is all a * then anything matches
        if (pattern.equals(WILDCARD)) {
            return true;
        }

        if (pattern.startsWith(WILDCARD)) { //if the pattern starts with a wildcard,
            //then everything before its position can be different, but after must be the same.
            String everythingAfterWildcardInPattern = StringUtils.substringAfter(pattern, WILDCARD);

            //so we can just check if the value we have ends with the pattern
            return StringUtils.endsWith(value,  everythingAfterWildcardInPattern);
        } else if (pattern.endsWith(WILDCARD)) { //if the pattern ends with a wildcard,
            int wildcardPositionInPattern = pattern.indexOf(WILDCARD);

            //then everything after its position can be different, but before it must be the same
            String everythingBeforeWildcardInValueToTest = StringUtils.substring(value, 0, wildcardPositionInPattern);
            String everythingBeforeWildcardInPattern = StringUtils.substringBefore(pattern, WILDCARD);

            return StringUtils.equals(everythingBeforeWildcardInValueToTest,  everythingBeforeWildcardInPattern);
        } else { //if the wildcard is in the middle or not at all
            // then just check full equality
            return StringUtils.equals(pattern, value);
        }
    }



}
