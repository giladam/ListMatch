package com.giladam.listmatch;

import java.util.Collection;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;


public class PatternListTest {

    /**
     * This test just makes sure nothing blows up if you initialize with null.
     */
    @Test
    public void testNullInitializationWorks() {

        PatternList pl = new PatternList(null, null, false);

        Assert.assertFalse(pl.matches("Should not match"));
    }


    @Test
    public void testIncompatibleEntryReturnsFalse() {

        Set<String> emailPatterns = Sets.newHashSet("test@example.com",
                                                    "test2*@example.com");

        PatternList plForEmails = new PatternList(emailPatterns, "@", false);

        Assert.assertFalse(plForEmails.matches("this@has@four@fields.com"));
    }


    @Test
    public void testWildcardStartsWith() {

        Collection<String> patterns = Sets.newHashSet("*something");

        PatternList patternList = new PatternList(patterns, "", false);

        String[] expectedMatches = {"anythingsomething",
                                    "somethingsomething",
                                    "這是中國人something"};

        String[] expectedNotMatches = {"somethingsomethingelse",
                                       "notit",
                                       null,
                                       ""};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


    @Test
    public void testWildcardEndsWith() {

        Collection<String> patterns = Sets.newHashSet("something*");

        PatternList patternList = new PatternList(patterns, "", false);

        String[] expectedMatches = {"somethinganything",
                                    "somethingsomething",
                                    "something這是中國人"};

        String[] expectedNotMatches = {"nonesomethingsomethingelse",
                                       "notit",
                                       null,
                                       ""};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


    @Test
    public void testWildcardAlone() {

        Collection<String> patterns = Sets.newHashSet("*");

        PatternList patternList = new PatternList(patterns, "", false);

        String[] expectedMatches = {"somethinganything",
                                    "somethingsomething",
                                    "something這是中國人",
                                    ""};

        String[] expectedNotMatches = {null};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


    @Test
    public void testNoWildcardExactValue() {

        Collection<String> patterns = Sets.newHashSet("這是中國人");

        PatternList patternList = new PatternList(patterns, "", false);

        String[] expectedMatches = {"這是中國人"};

        String[] expectedNotMatches = {null,
                                       "something else",
                                       "這是不是中國人"};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }



    @Test
    public void testWildcardInMiddle() {

        Collection<String> patterns = Sets.newHashSet("www.*.com");

        PatternList patternList = new PatternList(patterns, "", false);

        String[] expectedMatches = {"www.abc123.com",
                                    "www.더 유니 코드.com",
                                    "www.www.www.com"};

        String[] expectedNotMatches = {"www.com",
                                       null,
                                       "something else",
                                       "這是不是中國人",
                                       "123.abc.com",
                                       "abc.com",
                                       "",
                                       };

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }



    @Test
    public void testUnicodeHandling() {

        Collection<String> patterns = Sets.newHashSet("더 유니 코드.*.더 유니 코드");

        PatternList patternList = new PatternList(patterns, "", false);

        String[] expectedMatches = {"더 유니 코드.abc123.더 유니 코드",
                                    "더 유니 코드.더 유니 코드.더 유니 코드"};

        String[] expectedNotMatches = {"這是不是中國人.something.這是不是中國人"};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


}
