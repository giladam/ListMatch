package com.giladam.listmatch;

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

}
