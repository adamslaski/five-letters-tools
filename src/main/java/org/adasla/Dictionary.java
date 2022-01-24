package org.adasla;

import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Dictionary {
    private final List<Word> words;
    private final Map<Character, Long> freqs;
    private final Map<Pair<Character, Integer>, Long> freqPosition;

    public Dictionary(String filePath) {
        words = readWords(filePath);
        freqs = computeFrequency(Word::getChars);
        freqPosition = computeFrequency(Word::getCharsWithPosition);
    }

    private static List<Word> readWords(String filePath) {
        List<Word> words = new ArrayList<>(30000);
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(new Word(line));
                if (words.size() > App.WORD_LIMIT) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private <T> Map<T, Long> computeFrequency(Function<Word, Collection<T>> getKey) {
        Map<T, Long> map = new HashMap<>();

        words.stream()
                .map(getKey)
                .flatMap(Collection::stream)
                .forEach(character -> map.merge(character, 1L, Long::sum));

        return map;
    }

    public long calcScoreWithPosition(List<Word> words) {
        Set<Pair<Character, Integer>> positions = words.stream().map(Word::getCharsWithPosition).flatMap(Collection::stream).collect(Collectors.toSet());
        return positions.stream().map(freqPosition::get).reduce(0L, Long::sum);
    }

    public long calcBasicScore(List<Word> words) {
        Set<Character> chars = words.stream().map(Word::getChars).flatMap(Collection::stream).collect(Collectors.toSet());
        return chars.stream().map(freqs::get).reduce(0L, Long::sum);
    }

    public List<Word> getWords() {
        return words;
    }
}
