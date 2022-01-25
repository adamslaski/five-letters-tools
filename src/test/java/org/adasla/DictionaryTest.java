package org.adasla;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DictionaryTest {
    List<Word> words = List.of(new Word("axxxx"), new Word("abxxx"), new Word("abcxx"));
    Dictionary dictionary = new Dictionary(words);

    @Test
    public void shouldCalcBasicScore() {
        long score = dictionary.calcBasicScore(List.of(new Word("edcba")));
        Assert.assertThat(score, CoreMatchers.equalTo(6L));

        score = dictionary.calcBasicScore(List.of(new Word("zazzz")));
        Assert.assertThat(score, CoreMatchers.equalTo(3L));

        score = dictionary.calcBasicScore(List.of(new Word("zzczz")));
        Assert.assertThat(score, CoreMatchers.equalTo(1L));

        score = dictionary.calcBasicScore(List.of(new Word("zzczz"), new Word("zzczz")));
        Assert.assertThat(score, CoreMatchers.equalTo(1L));
    }

    @Test
    public void shouldCalcScoreWithPosition() {
        long score = dictionary.calcScoreWithPosition(List.of(new Word("zabcz")));
        Assert.assertThat(score, CoreMatchers.equalTo(0L));

        score = dictionary.calcScoreWithPosition(List.of(new Word("zazzz")));
        Assert.assertThat(score, CoreMatchers.equalTo(0L));

        score = dictionary.calcScoreWithPosition(List.of(new Word("azzzz")));
        Assert.assertThat(score, CoreMatchers.equalTo(3L));

        score = dictionary.calcScoreWithPosition(List.of(new Word("azzzz"), new Word("azzzz")));
        Assert.assertThat(score, CoreMatchers.equalTo(3L));
    }

    @Test
    public void shouldOnlyCountOnce() {
        List<Word> words = List.of(new Word("aaaaa"));
        Dictionary dictionary = new Dictionary(words);

        long score = dictionary.calcBasicScore(List.of(new Word("aaaaa")));
        Assert.assertThat(score, CoreMatchers.equalTo(1L));

        score = dictionary.calcScoreWithPosition(List.of(new Word("aaaaa")));
        Assert.assertThat(score, CoreMatchers.equalTo(1L));
    }
}