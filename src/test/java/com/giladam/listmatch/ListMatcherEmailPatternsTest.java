package com.giladam.listmatch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;


public class ListMatcherEmailPatternsTest {

    private static final String EMAIL_PATTERNS = "emails";


    private ListMatcher setupWithEmailPatterns(Collection<String> emailPatterns, boolean caseSensitive) {

        Map<String,PatternList> listByName = new HashMap<>();

        listByName.put(EMAIL_PATTERNS, new PatternList(emailPatterns, "@", caseSensitive));

        return new ListMatcher(listByName);
    }


    @Test
    public void testNullAlwaysReturnsFalse() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet(PatternList.WILDCARD), false);

        Assert.assertFalse(listMatcher.matchesList(EMAIL_PATTERNS, null));
    }


    @Test
    public void testWildcardAnyAlwaysReturnsTrueForValue() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet(PatternList.WILDCARD), false);

        Assert.assertTrue(listMatcher.matchesList(EMAIL_PATTERNS, ""));
    }


    @Test
    public void testEmailPattern_AnyLocalPart_Matches() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet("*@anylocalpart.com"), false);

        String[] expectedMatches = {"something@anylocalpart.com",
                                    "something.else@anylocalpart.com",
                                    "a@anylocalpart.com"};

        for (String emailAddressToTest : expectedMatches) {
            Assert.assertTrue(
                    "Expected " + emailAddressToTest + " to match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }
    }


    @Test
    public void testEmailPattern_AnyLocalPart_NotMatches() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet("*@anylocalpart.com"), false);

        String[] expectedNotMatches = {"something@notjustanylocalpart.com",
                                       "something.else@notjustanylocalpart.com",
                                       "a@notjustanylocalpart.com"};

        for (String emailAddressToTest : expectedNotMatches) {
            Assert.assertFalse(
                    "Expected " + emailAddressToTest + " to NOT match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }
    }


    @Test
    public void testEmailPattern_AnyDomainPart_Matches() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet("someuser@*"), false);

        String[] expectedMatches = {"someuser@example.com",
                                    "someuser@another.com",
                                    "someuser@example.with.dots.com"};

        for (String emailAddressToTest : expectedMatches) {
            Assert.assertTrue(
                    "Expected " + emailAddressToTest + " to match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }
    }


    @Test
    public void testEmailPattern_AnyDomainPart_NotMatches() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet("someuser@*"), false);

        String[] expectedNotMatches = {"notsomeuser@example.com",
                                       "notsomeuser@another.com",
                                       "notsomeuser@example.with.dots.com"};

        for (String emailAddressToTest : expectedNotMatches) {
            Assert.assertFalse(
                    "Expected " + emailAddressToTest + " to NOT match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }
    }



    @Test
    public void testEmailPattern_ExactMatch() {

        Set<String> patterns = Sets.newHashSet("test@example.com", "user@domain.com");

        ListMatcher listMatcher = setupWithEmailPatterns(patterns, false);

        for (String emailAddressToTest : patterns) {
            Assert.assertTrue(
                    "Expected " + emailAddressToTest + " to match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }
    }

    @Test
    public void testEmailPattern_ExactMatch_CaseSensitive() {

        Set<String> patterns = Sets.newHashSet("TEST@example.com",
                                               "USER@domain.com");

        ListMatcher listMatcher = setupWithEmailPatterns(patterns, true);

        String[] expectedMatches = {"TEST@example.com",
                                    "USER@domain.com"};

        String[] expectedNotMatches = {"test@example.com",
                                       "user@domain.com"};

        for (String emailAddressToTest : expectedMatches) {
            Assert.assertTrue(
                    "Expected " + emailAddressToTest + " to match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
            );
        }


        for (String emailAddressToTest : expectedNotMatches) {
            Assert.assertFalse(
                    "Expected " + emailAddressToTest + " to NOT match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
            );
        }
    }


    @Test
    public void testEmailPattern_SomeDomain_LocalPartStartsWith() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet("startswith*@domain.com"), false);

        String[] expectedMatches = {"startswith@domain.com",
                                    "startswith_andhasmore@domain.com",
                                    "startswith.has.dots@domain.com"};

        String[] expectedNotMatches = {"startswith.but.not.domain@example.com",
                                       "notstartswith@domain.com",
                                       "completely.different@domain.com"};

        for (String emailAddressToTest : expectedMatches) {
            Assert.assertTrue(
                    "Expected " + emailAddressToTest + " to match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }


        for (String emailAddressToTest : expectedNotMatches) {
            Assert.assertFalse(
                    "Expected " + emailAddressToTest + " to NOT match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }
    }


    @Test
    public void testEmailPattern_SomeDomain_LocalPartEndsWith() {

        ListMatcher listMatcher = setupWithEmailPatterns(Sets.newHashSet("*endswith@domain.com"), false);

        String[] expectedMatches = {"something.endswith@domain.com",
                                    "startswith_and_endswith@domain.com",
                                    "endswith@domain.com"};

        String[] expectedNotMatches = {"not_domain_but_endswith@example.com",
                                       "endswithout@domain.com",
                                       "completely.different@domain.com"};

        for (String emailAddressToTest : expectedMatches) {
            Assert.assertTrue(
                    "Expected " + emailAddressToTest + " to match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }


        for (String emailAddressToTest : expectedNotMatches) {
            Assert.assertFalse(
                    "Expected " + emailAddressToTest + " to NOT match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
                   );
        }
    }



    @Test
    public void testEmailPattern_WildcardUser() {

        Set<String> patterns = Sets.newHashSet("*@example.com",
                                               "*@another.com",
                                               "*@a-third.com");

        ListMatcher listMatcher = setupWithEmailPatterns(patterns, false);

        String[] expectedMatches = {"TEST@example.com",
                                    "USER@another.com",
                                    "me@a-third.com"};

        String[] expectedNotMatches = {"test@notexample.com",
                                       "user@domain.com"};

        for (String emailAddressToTest : expectedMatches) {
            Assert.assertTrue(
                    "Expected " + emailAddressToTest + " to match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
            );
        }


        for (String emailAddressToTest : expectedNotMatches) {
            Assert.assertFalse(
                    "Expected " + emailAddressToTest + " to NOT match.",
                    listMatcher.matchesList(EMAIL_PATTERNS, emailAddressToTest)
            );
        }
    }

}
