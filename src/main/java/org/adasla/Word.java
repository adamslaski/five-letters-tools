package org.adasla;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Word {
    private final String text;
    private final CharacterSet characterSet;

    public Word(String text) {
        this.text = text;
        characterSet = new CharacterSet(text);
    }

    public String getText() {
        return text;
    }

    public CharacterSet getCharacterSet() {
        return characterSet;
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
