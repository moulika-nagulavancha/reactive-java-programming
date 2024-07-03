package com.virtualthreads;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class CreatingExecutorService {
    public static void main(String[] args) {
        int tasks = 2000;

        var set = ConcurrentHashMap.<String>newKeySet();
        Runnable task = () -> set.add(Thread.currentThread().toString());

        try (var es1 = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < tasks; i++) {
                es1.submit(task);
            }
        }
        System.out.println("# threads used: " + set.size());

        var set2 = ConcurrentHashMap.<String>newKeySet();
        Runnable task1 = () -> set2.add(Thread.currentThread().toString());
        try (var es1 = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tasks; i++) {
                es1.submit(task1);
            }
        }
        System.out.println("# threads used: " + set2.size());
    }
}
