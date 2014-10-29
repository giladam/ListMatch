package com.giladam.listmatch;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;


public class ListMatcherUrlPatternsTest {

    @Test
    public void testUrlPatternHandling_EmptyDelimiter() {

        Collection<String> patterns = Sets.newHashSet("http://www.example.com/allowed/*",
                                                      "https://www.example.com/specific.html");

        PatternList patternList = new PatternList(patterns, "", false);

        String[] expectedMatches = {"http://www.example.com/allowed/url",
                                    "http://www.example.com/allowed/path/here?withParams=true&more=here",
                                    "https://www.example.com/specific.html"};

        String[] expectedNotMatches = {"20.0.0.2",
                                       "20.0.0.200",
                                       "20.100.0.1",
                                       "10.100.0.2",
                                       "10.100.1.0",
                                       "test@example.com",
                                       "http://www.example.com/notallowed.html",
                                       "https://www.example.com/allowed.html"};

        for (String expectedMatch : expectedMatches) {
            Assert.assertTrue("Expected " + expectedMatch + " to match.",
                              patternList.matches(expectedMatch));
        }


        for (String expectedNotMatch : expectedNotMatches) {
            Assert.assertFalse("Expected " + expectedNotMatch + " to NOT match.",
                               patternList.matches(expectedNotMatch));
        }
    }



    @Test
    public void testUrlPatternHandling_NullDelimiter() {

        Collection<String> patterns = Sets.newHashSet("http://www.example.com/allowed/*",
                                                      "https://www.example.com/specific.html");

        PatternList patternList = new PatternList(patterns, null, false);

        String[] expectedMatches = {"http://www.example.com/allowed/url",
                                    "http://www.example.com/allowed/path/here?withParams=true&more=here",
                                    "https://www.example.com/specific.html"};

        String[] expectedNotMatches = {"20.0.0.2",
                                       "20.0.0.200",
                                       "20.100.0.1",
                                       "10.100.0.2",
                                       "10.100.1.0",
                                       "test@example.com",
                                       "http://www.example.com/notallowed.html",
                                       "https://www.example.com/allowed.html"};

        for (String expectedMatch : expectedMatches) {
            Assert.assertTrue("Expected " + expectedMatch + " to match.",
                              patternList.matches(expectedMatch));
        }


        for (String expectedNotMatch : expectedNotMatches) {
            Assert.assertFalse("Expected " + expectedNotMatch + " to NOT match.",
                               patternList.matches(expectedNotMatch));
        }
    }


}
