package com.giladam.listmatch;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.Sets;


public class ListMatcherIpPatternsTest {



    @Test
    public void testIpPatternHandling() {

        Collection<String> patterns = Sets.newHashSet("10.0.0.*",
                                                      "10.100.0.1");

        PatternList patternList = new PatternList(patterns, ".", false);

        String[] expectedMatches = {"10.0.0.1",
                                    "10.0.0.100",
                                    "10.100.0.1"};

        String[] expectedNotMatches = {"20.0.0.2",
                                       "20.0.0.200",
                                       "20.100.0.1",
                                       "10.100.0.2",
                                       "10.100.1.0"};

        TestingUtil.assertMatchingCorrectly(patternList, expectedMatches, expectedNotMatches);
    }


}
