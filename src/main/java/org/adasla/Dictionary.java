package org.adasla;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {
    private final List<Word> words;
    private final long[] freqs;
    private final long[][] freqPosition;

    public Dictionary(String filePath) {
        this(readWords(filePath));
    }

    public Dictionary(List<Word> words) {
        this.words = words;
        freqs = computeFrequency();
        freqPosition = computeFrequencyWithPosition();
    }

    private static List<Word> readWords(String filePath) {
        List<Word> words = new ArrayList<>(30000);
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(new Word(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private long[] computeFrequency() {
        long[] freq = new long[App.UNICODE_CODE_LIMIT];
        words.forEach(word -> {
            List<Character> characters = word.getCharacterSet().asList();
            for (Character character : characters) {
                freq[(int) character]++;
            }
        });
        return freq;
    }

    private long[][] computeFrequencyWithPosition() {
        long[][] freq = new long[App.WORD_LENGTH][App.UNICODE_CODE_LIMIT];
        words.forEach(word -> {
            List<Character> characters = word.getCharacterSet().asList();
            for (int i = 0; i < characters.size(); i++) {
                freq[i][(int) characters.get(i)]++;
            }
        });
        return freq;
    }

    public long calcBasicScore(Iterable<Word> words) {
        long result = 0L;
        CharacterSet characterSet = new CharacterSet();
        for (Word word : words) {
            characterSet.add(word.getText());
        }
        List<Character> characters = characterSet.asList();
        for (Character character : characters) {
            result += freqs[character];
        }
        return result;
    }

    public long calcScoreWithPosition(Iterable<Word> words) {
        long result = 0L;
        CharacterSetWithPosition setWithPosition = new CharacterSetWithPosition();
        for (Word word : words) {
            setWithPosition.add(word.getText());
        }
        for (int i = 0; i < App.WORD_LENGTH; i++) {
            List<Character> characters = setWithPosition.getCharacterSets()[i].asList();
            for (Character character : characters) {
                result += freqPosition[i][character];
            }
        }
        return result;
    }

    public long calcCombinedScore(List<Word> words) {
        return calcBasicScore(words) + calcScoreWithPosition(words);
    }

    public List<Word> getWords() {
        return words;
    }
}
