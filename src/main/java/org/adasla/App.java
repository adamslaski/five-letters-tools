package org.adasla;

import com.google.common.collect.MinMaxPriorityQueue;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class App {
    public static final int WORD_LENGTH = 5;
    public static final int UNICODE_CODE_LIMIT = 500; // max unicode code supported
    public static final Comparator<Pair<List<Word>, Long>> PAIR_COMPARATOR = Comparator.<Pair<List<Word>, Long>, Long>comparing(Pair::getRight)
            .reversed();
    public static final int RESULT_LIMIT = 20;

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("This program expects 1 argument: path to list of words");
        }
        final Dictionary dictionary = new Dictionary(args[0]);
        final List<Word> words = dictionary.getWords();

        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        final ExecutorService threadPool = Executors.newFixedThreadPool(numberOfProcessors);

        final StopWatch wallClock = new StopWatch();
        final List<Pair<List<Word>, Long>> result = Collections.synchronizedList(new ArrayList<>());
        wallClock.start();

        final int chunkSize = words.size() / (numberOfProcessors * 10); // equalize workloads
        for (int i = 0; i < words.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, words.size());
            threadPool.submit(() -> {
                final StopWatch watch = new StopWatch();
                watch.start();
                MinMaxPriorityQueue<Pair<List<Word>, Long>> bestPairs = getBestPairs(dictionary, words, start, end);
                watch.stop();
                result.addAll(bestPairs);
            });
        }

        threadPool.shutdown();
        //noinspection ResultOfMethodCallIgnored
        threadPool.awaitTermination(10, TimeUnit.MINUTES);
        wallClock.stop();
        result.sort(PAIR_COMPARATOR);
        System.out.println("Wall clock: " + wallClock.formatTime());
        print(result);

        MinMaxPriorityQueue<Pair<List<Word>, Long>> queue3 = getTuples(dictionary, words, result.subList(0, RESULT_LIMIT));
        print(queue3);

        MinMaxPriorityQueue<Pair<List<Word>, Long>> queue4 = getTuples(dictionary, words, queue3);
        print(queue4);

        MinMaxPriorityQueue<Pair<List<Word>, Long>> queue5 = getTuples(dictionary, words, queue4);
        print(queue5);
    }

    private static MinMaxPriorityQueue<Pair<List<Word>, Long>> getTuples(Dictionary dictionary, List<Word> words, Collection<Pair<List<Word>, Long>> selectedWords) {
        MinMaxPriorityQueue<Pair<List<Word>, Long>> queue3 = MinMaxPriorityQueue.orderedBy(PAIR_COMPARATOR).maximumSize(RESULT_LIMIT).create();
        selectedWords.forEach(listLongPair -> combine(listLongPair.getLeft(), words.iterator(), dictionary::calcCombinedScore, queue3::offer));
        return queue3;
    }

    private static MinMaxPriorityQueue<Pair<List<Word>, Long>> getBestPairs(Dictionary dictionary, List<Word> words, int start, int end) {
        MinMaxPriorityQueue<Pair<List<Word>, Long>> queue = MinMaxPriorityQueue.orderedBy(PAIR_COMPARATOR).maximumSize(RESULT_LIMIT).create();
        for (int i = start; i +1 < end; i++) {
            Word word = words.get(i);
            Iterator<Word> iterator = words.subList(i + 1, words.size()).iterator();
            combine(List.of(word), iterator, dictionary::calcCombinedScore, queue::offer);
        }
        return queue;
    }

    private static void print(Collection<Pair<List<Word>, Long>> queue) {
        queue.stream().limit(10).forEach(pair -> {
            List<Word> left = pair.getLeft();
            String tuple = left.stream().map(Word::getText).collect(Collectors.joining(" "));
            System.out.printf("%s %d%n", tuple, pair.getRight());
        });
    }

    private static void combine(List<Word> selectedWords,
                                Iterator<Word> words,
                                Function<List<Word>, Long> calcScore,
                                Consumer<Pair<List<Word>, Long>> consumer) {
        while (words.hasNext()) {
            Word word = words.next();
            List<Word> combinedWords = new ArrayList<>(selectedWords.size()+1);
            combinedWords.addAll(selectedWords);
            combinedWords.add(word);
            long score = calcScore.apply(combinedWords);
            consumer.accept(Pair.of(combinedWords, score));
        }
    }

}
