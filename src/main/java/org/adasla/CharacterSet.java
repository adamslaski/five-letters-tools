package org.adasla;

import java.util.ArrayList;
import java.util.List;

public class CharacterSet {
    private final List<Character> chars;
    private final boolean[] set;

    public CharacterSet() {
        chars = new ArrayList<>();
        set = new boolean[App.UNICODE_CODE_LIMIT];
    }

    public CharacterSet(String s) {
        this();
        add(s);
    }

    public boolean contains(char c) {
        return set[c];
    }

    public void add(char c) {
        if (!contains(c)) {
            set[c] = true;
            chars.add(c);
        }
    }

    public void add(String s) {
        for (int i = 0; i < s.length(); i++) {
            add(s.charAt(i));
        }
    }

    public List<Character> asList() {
        return chars;
    }

    static public boolean intersectionIsEmpty(CharacterSet cs1, CharacterSet cs2) {
        for (int k = 0; k < cs1.chars.size(); k++) {
            if (cs2.contains(cs1.chars.get(k))) {
                return false;
            }
        }
        return true;
    }
}
