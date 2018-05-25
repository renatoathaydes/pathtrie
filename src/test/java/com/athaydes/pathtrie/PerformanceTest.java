package com.athaydes.pathtrie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Ignore("Run performance tests only manually")
public class PerformanceTest {

    static final int WARMUP_RUNS = 50_000;
    static final int RUNS = 100_000;

    static PerformanceTestData NO_PARAMETERS = new PerformanceTestData(PathTrie.<Integer>newBuilder()
            .put("abc", 123)
            .put("abc/def", 456)
            .put("mno/pqr", 789)
            .put("mno/pqr/stu/vxz/wy", 100)
            .build(),
            new ArrayList<>(Arrays.asList(
                    new Example("abc", true),
                    new Example("abc/def", true),
                    new Example("mno/pqr", true),
                    new Example("mno/pqr/stu/vxz/wy", true),
                    new Example("wrong", false),
                    new Example("not/existing/path", false)))
    );

    static PerformanceTestData PARAMETERIZED = new PerformanceTestData(PathTrie.<Integer>newBuilder()
            .put("home/:param", 123)
            .put("abc/:param", 456)
            .put("mno/pqr", 789)
            .put("mno/pqr/:p1/vxz/:p2", 100)
            .build(),
            new ArrayList<>(Arrays.asList(
                    new Example("home/joe", true),
                    new Example("home/mary", true),
                    new Example("abc/def", true),
                    new Example("abc/ghi", true),
                    new Example("mno/pqr", true),
                    new Example("mno/pqr/stu/vxz/wy", true),
                    new Example("mno/pqr/stu/vxz", false),
                    new Example("wrong", false),
                    new Example("not/existing/path", false)))
    );

    @Test
    public void noParameters() {
        Map<String, long[]> results = run(NO_PARAMETERS);
        Map<String, LongSummaryStatistics> stats = collectStats(results);
        printReport(stats);
    }

    @Test
    public void parameterized() {
        Map<String, long[]> results = run(PARAMETERIZED);
        Map<String, LongSummaryStatistics> stats = collectStats(results);
        printReport(stats);
    }

    static Map<String, LongSummaryStatistics> collectStats(Map<String, long[]> data) {
        Map<String, LongSummaryStatistics> statsMap = new HashMap<>();
        System.out.println("Results:");
        data.forEach((path, times) -> {
            LongStream effectiveData = take98Percentile(times);
            LongSummaryStatistics stats = effectiveData.summaryStatistics();
            statsMap.put(path, stats);
        });
        return statsMap;
    }

    static LongStream take98Percentile(long[] times) {
        int onePercentIndex = (int) (times.length * 0.01);
        long[] sortedTimes = new long[times.length];
        System.arraycopy(times, 0, sortedTimes, 0, times.length);
        Arrays.sort(sortedTimes);
        long min = sortedTimes[onePercentIndex];
        long max = sortedTimes[times.length - onePercentIndex];
        return LongStream.of(times).filter(l -> l > min && l < max);
    }

    static Map<String, long[]> run(PerformanceTestData data) {
        final boolean parameterized = data == PARAMETERIZED;
        final List<Example> examples = data.examples;
        final PathTrie<?> trie = data.trie;
        final Random random = new Random();

        for (int i = 0; i < WARMUP_RUNS; i++) {
            String path = examples.get(random.nextInt(examples.size())).path;
            trie.get(path);
        }

        Map<String, long[]> results = new HashMap<>(examples.size());
        for (Example ex : examples) {
            results.put(ex.path, new long[RUNS]);
        }

        for (int i = 0; i < RUNS; i++) {
            Example ex = examples.get(random.nextInt(examples.size()));
            String path = ex.path;
            TimerResult<Optional<?>> timerResult = withTimer(() ->
                    parameterized ? trie.getParameterized(path) : trie.get(path));
            assertEquals("Example path: " + ex.path, timerResult.result.isPresent(), ex.exists);
            results.get(path)[i] = timerResult.time;
        }

        return results;
    }

    static void printReport(Map<String, LongSummaryStatistics> statisticsMap) {
        statisticsMap.forEach((path, stats) -> {
            System.out.println("  " + path + ":");
            System.out.println("    - min: " + stats.getMin());
            System.out.println("    - max: " + stats.getMax());
            System.out.println("    - avg: " + stats.getAverage());
        });
    }

    private static <T> TimerResult<T> withTimer(Supplier<T> action) {
        long startTime = System.nanoTime();
        T result = action.get();
        long endTime = System.nanoTime();
        return new TimerResult<>(result, endTime - startTime);
    }
}

class TimerResult<T> {
    final T result;
    final long time;

    TimerResult(T result, long time) {
        this.result = result;
        this.time = time;
    }
}

class Example {
    final String path;
    final boolean exists;

    Example(String path, boolean exists) {
        this.path = path;
        this.exists = exists;
    }
}

class PerformanceTestData {
    final PathTrie<?> trie;
    final List<Example> examples;

    PerformanceTestData(PathTrie<?> trie, List<Example> examples) {
        this.trie = trie;
        this.examples = examples;
    }
}