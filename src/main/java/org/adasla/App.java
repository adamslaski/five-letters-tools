package org.adasla;

import com.google.common.collect.MinMaxPriorityQueue;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class App {
    public static final int SPAM_LIMIT = 1000;
    public static final int WORD_LIMIT = 150000;
    public static final int WORD_LENGTH = 5;
    public static final int UNICODE_CODE_LIMIT = 500;
    public static FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("HH:mm");
    public static final Comparator<Pair<List<Word>, Long>> PAIR_COMPARATOR = Comparator.<Pair<List<Word>, Long>, Long>comparing(Pair::getRight)
            .reversed();

    public static void main(String[] args) {
        final Dictionary dictionary = new Dictionary(args[0]);
        final List<Word> words = dictionary.getWords();

        StopWatch watch = new StopWatch();
        watch.start();
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(cores);

        List<Future<List<Pair<List<Word>, Long>>>> futures = new ArrayList<>();
        long counter = 0L;
        for (int i = 0; i < words.size(); i++) {
            for (int j = i + 1; j + 1 < words.size(); j++) {
                final int finalJ = j;
                Word word = words.get(i);
                Word word1 = words.get(j);
                boolean stop = false;
                for (int k = 0; k < App.WORD_LENGTH; k++) {
                    if (word1.getCharacterSet().contains(word.getText().charAt(k))) {
                        stop = true;
                    }
                }
                if (stop) {
                    continue;
                }
                Callable<List<Pair<List<Word>, Long>>> callable = () -> {
                    List<Word> sublist = words.subList(finalJ+1, words.size());
                    return extracted(dictionary, List.of(word, word1), sublist);
                };
                Future<List<Pair<List<Word>, Long>>> future;
                counter++;
                if (counter % SPAM_LIMIT == 0) {
                    long finalCounter = counter;
                    future = executorService.submit(() -> {
                        StopWatch watch1 = new StopWatch();
                        watch1.start();
                        List<Pair<List<Word>, Long>> list = callable.call();
                        watch1.stop();
                        Pair<List<Word>, Long> top = list.get(0);
                        if (top != null) {
                            String date = DATE_FORMAT.format(new Date());
                            String duration = watch1.formatTime();
                            System.out.printf("%d  %s  %s  %s  %d  %s%n",
                                    finalCounter, date, duration, top.getLeft(), top.getRight(), Thread.currentThread().getName());
                        }
                        return list;
                    });
                } else {
                    future = executorService.submit(callable);
                }

                futures.add(future);
            }
        }

        List<Pair<List<Word>, Long>> result = new ArrayList<>();
        futures.forEach(listFuture -> {
            try {
                List<Pair<List<Word>, Long>> pairs = listFuture.get();
                result.addAll(pairs);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        watch.stop();

        System.out.printf("Time Elapsed: %s Threads: %d Word Limit: %d%n", watch.formatTime(), cores, WORD_LIMIT);

        result.stream().sorted(PAIR_COMPARATOR).limit(20L)
        .forEach(pair -> {
            List<Pair<List<Word>, Long>> w = pair.getLeft().stream().map(word -> {
                List<Word> tuple = List.of(word);
                long score = dictionary.calcBasicScore(tuple) + dictionary.calcScoreWithPosition(tuple);
                return Pair.of(tuple, score);
            })
                    .sorted(PAIR_COMPARATOR)
                    .collect(Collectors.toList());
            System.out.println(w + " " + pair.getRight());
        });
        executorService.shutdown();
    }

    private static List<Pair<List<Word>, Long>> extracted(Dictionary dictionary, List<Word> selectedWords, List<Word> words) {
        MinMaxPriorityQueue<Pair<List<Word>, Long>> queue = MinMaxPriorityQueue.orderedBy(PAIR_COMPARATOR).maximumSize(20).create();
        for (Word word : words) {
            ArrayList<Word> tuple = new ArrayList<>(selectedWords);
            tuple.add(word);
            long score = dictionary.calcBasicScore(tuple) + dictionary.calcScoreWithPosition(tuple);
            Pair<List<Word>, Long> pair = Pair.of(tuple, score);
            queue.offer(pair);
        }
        return new ArrayList<>(queue);
    }
}
