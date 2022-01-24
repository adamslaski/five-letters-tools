package org.adasla;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class Word {
    private final String text;
    private final Set<Pair<Character, Integer>> charsWithPosition;
    private final Set<Character> chars;

    public Word(String text) {
        this.text = text;
        this.chars = new HashSet<>(5);
        this.charsWithPosition = new HashSet<>(5);
        for (int i = 0; i < text.length(); i++) {
            chars.add(text.charAt(i));
            charsWithPosition.add(Pair.of(text.charAt(i), i));
        }
    }

    public String getText() {
        return text;
    }

    public Set<Pair<Character, Integer>> getCharsWithPosition() {
        return charsWithPosition;
    }

    public Set<Character> getChars() {
        return chars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return new EqualsBuilder().append(text, word.text).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(text).toHashCode();
    }

    @Override
    public String toString() {
        return "Word{" +
                "text='" + text + '\'' +
                '}';
    }
}
