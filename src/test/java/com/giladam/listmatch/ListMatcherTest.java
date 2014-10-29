package com.giladam.listmatch;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;


public class ListMatcherTest {

    /**
     * This test makes sure that loading an empty file returns an empty collection.
     *
     * @throws IOException
     */
    @Test
    public void testReadPatternsInFile_EmptyFileReturnsEmpty() throws IOException {

        Collection<String> patterns = ListMatcher.readPatternsFromFile(new File("src/test/resources/empty_file.txt"));
        Assert.assertTrue(patterns.isEmpty());
    }


    /**
     * This test makes sure that it throws an IOException if something goes wrong loading the file..
     */
    @Test(expected=IOException.class)
    public void testReadPatternsInFile_ThrowsExceptionOnFailedReading() throws IOException {
        ListMatcher.readPatternsFromFile(new File("src/test/resources/does not exist.txt"));
        Assert.fail("Should not get here, because an IOException should have been thrown.");
    }


    /**
     * This test makes sure that commented out lines are ignored.
     *
     * @throws IOException
     */
    @Test
    public void testReadPatternsInFile_IgnoresCommentedLines() throws IOException {

        Set<String> readPatterns = ListMatcher.readPatternsFromFile(new File("src/test/resources/commented_lines_test.txt"));

        //make sure the read patterns contains the ones we care about and ONLY these
        Set<String> expectedPatterns = Sets.newHashSet("comments_after_with_whitespace_after@test.com",
                                                       "comments_after_without_whitespace_after@test.com",
                                                       "nocomments@test.com");

        //make sure the read patterns contains the ones we care about:
        for (String expectedPattern : expectedPatterns) {
            Assert.assertTrue("Expected to have read: " + expectedPattern, readPatterns.contains(expectedPattern));
        }

        //make sure we don't have any others:
        for (String readPattern : readPatterns) {
            Assert.assertTrue("Not expecting to have read: " + readPattern, expectedPatterns.contains(readPattern));
        }
    }


    /**
     * This test makes sure if you try to test again a list that does exist, you get false.
     */
    @Test
    public void testNoSuchListReturnsFalse() {

        ListMatcher listMatcher = new ListMatcher(Collections.<String,PatternList>emptyMap());

        Assert.assertFalse(listMatcher.matchesList("no such list", "something to check"));
    }

}
