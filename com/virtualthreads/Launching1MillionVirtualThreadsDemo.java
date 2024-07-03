package com.virtualthreads;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Launching1MillionVirtualThreadsDemo {
    public static final Pattern POOL_PATTERN = Pattern.compile("ForkJoinPool-[\\d?]");
    public static final Pattern WORKER_PATTERN = Pattern.compile("worker-[\\d?]");

    public static void main(String[] args) throws InterruptedException {
        Set<String> poolNames = ConcurrentHashMap.newKeySet();
        Set<String> pThreadNames = ConcurrentHashMap.newKeySet();

        int N_THREADS = 1_000_000;

        var threads = IntStream.range(0, N_THREADS)
                .mapToObj(i -> Thread.ofVirtual()
                        .unstarted(
                                () -> {
                                    String poolName = readPoolName();
                                    poolNames.add(poolName);

                                    String workerName = readWorkerName();
                                    pThreadNames.add(workerName);
                                }
                        )).toList();

        Instant begin = Instant.now();

        for (var thread : threads) {
            thread.start();
        }

        for (var thread : threads) {
            thread.join();
        }

        Instant end = Instant.now();
        System.out.println("# Virtual Threads: " + N_THREADS);
        System.out.println("# cores: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Time: " + Duration.between(begin, end).toMillis() + "ms");
        System.out.println("### Pools");
        poolNames.forEach(System.out::println);
        System.out.println("### Platform Threads: " + pThreadNames.size());
    }

    private static String readWorkerName() {
        String name = Thread.currentThread().toString();
        Matcher workerMatcher = WORKER_PATTERN.matcher(name);
        if (workerMatcher.find()) {
            return workerMatcher.group();
        }
        return "not found";
    }

    private static String readPoolName() {
        String name = Thread.currentThread().toString();
        Matcher poolMatcher = POOL_PATTERN.matcher(name);
        if (poolMatcher.find()) {
            return poolMatcher.group();
        }
        return "not found";
    }


}
