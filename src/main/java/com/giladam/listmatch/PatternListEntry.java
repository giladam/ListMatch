package com.giladam.listmatch;

import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * An pattern that is used to test against in a {@link PatternList}
 *
 * @author Gil Adam
 */
public class PatternListEntry {

    private final String[] components;


    /**
     * Constructs a new PatternListEntry with the specified value and delimits components
     * of the pattern by componentDelimiter specified.
     *
     * If the componentDelimiter is null, then there will only be one component.
     *
     * @param value
     * @param componentDelimiter
     */
    public PatternListEntry(String value, String componentDelimiter) {

        if (StringUtils.isEmpty(value)) {
            this.components = new String[0];
        } else if (componentDelimiter == null) {
            this.components = new String[]{value};
        } else {
            this.components = value.split(Pattern.quote(componentDelimiter));
        }
    }


    public String[] getComponents() {
        return this.components;
    }


    @Override
    public final int hashCode() {
       return Objects.hash((Object[]) components);
    }


    @Override
    public final boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof PatternListEntry)) {
            return false;
        }

        PatternListEntry other = (PatternListEntry) obj;

        return Objects.deepEquals(this.components, other.components);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (int i=0; i<this.components.length; i++) {
            sb.append("'");
            sb.append(this.components[i]);
            sb.append("'");

            if (i < (this.components.length-1)) {
                sb.append(", ");
            }
        }

        sb.append("]");

        return sb.toString();
    }
}
