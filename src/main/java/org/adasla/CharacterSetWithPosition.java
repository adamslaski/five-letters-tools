package org.adasla;

public class CharacterSetWithPosition {
    private final CharacterSet[] characterSets;

    public CharacterSetWithPosition() {
        characterSets = new CharacterSet[App.WORD_LENGTH];
        for (int i = 0; i < App.WORD_LENGTH; i++) {
            characterSets[i] = new CharacterSet();
        }
    }

    public CharacterSetWithPosition(String s) {
        this();
        add(s);
    }

    public boolean contains(int i, char c) {
        return characterSets[i].contains(c);
    }

    public void add(int i, char c) {
        characterSets[i].add(c);
    }

    public void add(String s) {
        for (int i = 0; i < App.WORD_LENGTH; i++) {
            add(i, s.charAt(i));
        }
    }

    public CharacterSet[] getCharacterSets() {
        return characterSets;
    }
}
