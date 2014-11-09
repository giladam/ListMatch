package com.giladam.listmatch;

import java.util.Collection;

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

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
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

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }



    @Test
    public void testUrlPatternHandling_WildcardsInBeginning() {

        Collection<String> patterns = Sets.newHashSet("*://www.anyprotocol.com",
                                                      "*/robots.txt");

        PatternList patternList = new PatternList(patterns, null, false);

        String[] expectedMatches = {"http://www.anyprotocol.com",
                                    "https://www.anyprotocol.com",
                                    "ftp://www.anyprotocol.com",
                                    "http://www.examples.com/something/nested/deep/robots.txt",
                                    "https://www.examples.com/robots.txt",
                                    "ftp://www.examples.com/robots.txt"};

        String[] expectedNotMatches = {"10.100.1.0",
                                       "test@example.com",
                                       "http://www.notexample.com",
                                       "https://www.notexample.com",
                                       "https://www.examples.com/robots.txt.bak",};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


    @Test
    public void testUrlPatternHandling_WildcardsInMiddle_Single() {

        Collection<String> patterns = Sets.newHashSet("http*://www.example-https-or-http.com");

        PatternList patternList = new PatternList(patterns, null, false);

        String[] expectedMatches = {"https://www.example-https-or-http.com",
                                    "httpz://www.example-https-or-http.com",
                                    "http://www.example-https-or-http.com",
                                    "httpabcdefg://www.example-https-or-http.com"};

        String[] expectedNotMatches = {"httpabc-//www.example-https-or-http.com"};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


    @Test
    public void testUrlPatternHandling_WildcardsInMiddle_Multiple() {

        Collection<String> patterns = Sets.newHashSet("http://www.*.com/*/something.html");

        PatternList patternList = new PatternList(patterns, null, false);

        String[] expectedMatches = {"http://www.example.com/anythingok/something.html",
                                    "http://www.test.com/anythingok/something.html",
                                    "http://www.test2.com/2/something.html"};

        String[] expectedNotMatches = {"http://hello.example.com/anythingok/something.html",
                                       "http://hello.example.com/anythingok/notsomething.html",
                                       "http://www.example.com/anythingok/notsomething.html"};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


    @Test
    public void testUrlPatternHandling_WildcardsInBeginningMiddleAndEnd() {

        Collection<String> patterns = Sets.newHashSet("*www.*.com*");

        PatternList patternList = new PatternList(patterns, null, false);

        String[] expectedMatches = {"http://www.example.com/anythingok/something.html",
                                    "ftp://www.test.com/anythingok/something.html",
                                    "https://www.test2.com/2/something.html",
                                    "http://www.example.com",
                                    "www.example.com",
                                    "www.somethingelse.com"};

        String[] expectedNotMatches = {"http://www.example.edu/anythingok/something.html",
                                       "https://hello.example.com/anythingok/notsomething.html",
                                       "http://www.example.edu",
                                       "notallowed.somethingelse.com",
                                       "notallowed.somethingelse.edu/test.thml"};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }

}
