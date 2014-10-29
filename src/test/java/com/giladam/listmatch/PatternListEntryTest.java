package com.giladam.listmatch;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Objects;


public class PatternListEntryTest {

    @Test
    public void testHashCode() {

        PatternListEntry pe1 = new PatternListEntry("test", "");
        PatternListEntry pe2 = new PatternListEntry("test2", "");

        Assert.assertNotEquals(pe1.hashCode(), pe2.hashCode());
        Assert.assertFalse(Objects.equal(pe1, pe2));
    }


    @Test
    public void testEquals() {
        EqualsVerifier.forClass(PatternListEntry.class).verify();
    }


    @Test
    public void testToString_EmptyComponents() {

        PatternListEntry pe = new PatternListEntry("", "");

        Assert.assertEquals("[]", pe.toString());
    }


    @Test
    public void testToString_EmailAddressPattern() {

        PatternListEntry pe = new PatternListEntry("email@domain.com", "@");

        Assert.assertEquals("['email', 'domain.com']", pe.toString());
    }
}
