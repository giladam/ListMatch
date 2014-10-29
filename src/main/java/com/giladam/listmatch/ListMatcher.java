package com.giladam.listmatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The main class that holds the different {@link PatternList}s to test against.
 *
 * For convenience, use readPatternsFromFile() to read patterns from a text file.
 *
 * If you just need to test against one list, then you can use {@link PatternList} by itself.
 *
 * @author Gil Adam
 *
 */
public class ListMatcher {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static Logger log = LoggerFactory.getLogger(ListMatcher.class);

    private Map<String,PatternList> listByName = Collections.emptyMap();


    /**
     * Creates a new ListMatcher with multiple {@link PatternList}s each which must be referenced by
     * name when performing a match.
     *
     * It takes a Map of LIST_NAME => PatterList
     *
     * @param listByName
     */
    public ListMatcher(Map<String,PatternList> listByName) {
        this.listByName = listByName;
    }


    /**
     * Loads patterns to use from a File, ignoring lines that start with # characters as commented.
     *
     * @throws IOException
     */
    public static Set<String> readPatternsFromFile(File whiteListFile) throws IOException {

        Set<String> patternsFound = new HashSet<>();

        try (BufferedReader fileReader = Files.newBufferedReader(whiteListFile.toPath(), UTF_8)) {
            String line = null;
            while ((line = fileReader.readLine()) != null) {

                String contentBeforeComments = StringUtils.trim(StringUtils.substringBefore(line, "#"));
                if (StringUtils.isNotBlank(contentBeforeComments)) {
                    patternsFound.add(contentBeforeComments);
                }
            }
        }

        return patternsFound;
    }


    /**
     * Returns true if the valueToTest matches any of the entries in the list specified by name.
     *
     * If the list specified by name is not know to this ListMatcher, it returns false.
     *
     * @param listName The case-sensitive name of the list to test with.
     * @param valueToTest The value to check for matching against the list.
     * @return
     */
    public boolean matchesList(String listName, String valueToTest) {

        PatternList listToUse = listByName.get(listName);
        if (listToUse == null) {
            log.debug("No such list [{}]", listName);
            return false;
        } else {
            return listToUse.matches(valueToTest);
        }
    }

}