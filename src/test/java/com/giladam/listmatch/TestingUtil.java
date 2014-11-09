package com.giladam.listmatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;


/**
 * I'm using the same way to assert things a lot of times, so this helps cut down on the duplication.
 *
 * @author Gil Adam
 *
 */
public class TestingUtil {

    private TestingUtil() {
        //do not instantiate, just use the static utilities.
    }


    /**
     * Throws an exception if any of the expectedMatches do not match or if any of the expectedNotMatches
     * do match.
     *
     * @param patternList The configured {@link PatternList} to use for testing.
     * @param expectedMatches The values that must match successfully (may be null).
     * @param expectedNotMatches The values that must fail matching (may be null).
     */
    public static void assertMatchingCorrectly(PatternList patternList,
                                               String[] expectedMatches,
                                               String[] expectedNotMatches) {

        List<String> expectedMatchesAsList = expectedMatches != null ? Arrays.asList(expectedMatches) : Collections.<String>emptyList();
        List<String> expectedNotMatchesAsList = expectedNotMatches != null ? Arrays.asList(expectedNotMatches) : Collections.<String>emptyList();

        assertMatchingCorrectly(patternList, expectedMatchesAsList, expectedNotMatchesAsList);
    }


    /**
     * Throws an exception if any of the expectedMatches do not match or if any of the expectedNotMatches
     * do match.
     *
     * @param patternList The configured {@link PatternList} to use for testing.
     * @param expectedMatches The values that must match successfully (may be null).
     * @param expectedNotMatches The values that must fail matching (may be null).
     */
    public static void assertMatchingCorrectly(PatternList patternList,
                                               Iterable<String> expectedMatches,
                                               Iterable<String> expectedNotMatches) {

        if (expectedMatches != null) {
            for (String expectedMatch : expectedMatches) {
                Assert.assertTrue("Expected " + expectedMatch + " to match.",
                                  patternList.matches(expectedMatch));
            }
        }


        if (expectedNotMatches != null) {
            for (String expectedNotMatch : expectedNotMatches) {
                Assert.assertFalse("Expected " + expectedNotMatch + " to NOT match.",
                                   patternList.matches(expectedNotMatch));
            }
        }
    }

}
