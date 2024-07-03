package com.virtualthreads;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class PinningSyncIssueInVirtualThreadsDemo {
    private static int counter = 0;
    public static void main(String[] args) throws InterruptedException {
        traditionalSynchronizedTasks();
        System.out.println("\n\n");
        reentrantLockTasks();
    }

    private static void traditionalSynchronizedTasks() throws InterruptedException {
        var lock = new Object();

        Runnable task1 = () -> {
            System.out.println(Thread.currentThread());
            synchronized (lock) {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            }
            System.out.println(Thread.currentThread());
            synchronized (lock) {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            }
            System.out.println(Thread.currentThread());
            synchronized (lock) {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            }
            System.out.println(Thread.currentThread());
        };

        Runnable task2 = () -> {
            synchronized (lock) {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            }
            synchronized (lock) {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            }
            synchronized (lock) {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            }
        };

        int N_THREADS = 2_000;

        var threads = new ArrayList<Thread>();

        for (int i = 0; i < N_THREADS; i++) {
            var thread = (i == 0) ? Thread.ofVirtual().unstarted(task1) : Thread.ofVirtual().unstarted(task2);

            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("# Threads: " + N_THREADS);
        System.out.println("# counter: " + counter);
    }

    private static void reentrantLockTasks() throws InterruptedException {
        var lock = new ReentrantLock();

        Runnable task1 = () -> {
            System.out.println(Thread.currentThread());
            lock.lock();
            try {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread());
            lock.lock();
            try {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread());
            lock.lock();
            try {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread());
        };

        Runnable task2 = () -> {
            lock.lock();
            try {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            } finally {
                lock.unlock();
            }
            lock.lock();
            try {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            } finally {
                lock.unlock();
            }
            lock.lock();
            try {
                counter++;
                sleepFor(1, ChronoUnit.MICROS);
            } finally {
                lock.unlock();
            }
        };

        int N_THREADS = 2_000;

        var threads = new ArrayList<Thread>();

        for (int i = 0; i < N_THREADS; i++) {
            var thread = (i == 0) ? Thread.ofVirtual().unstarted(task1) : Thread.ofVirtual().unstarted(task2);

            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("# Threads: " + N_THREADS);
        System.out.println("# counter: " + counter);
    }


    private static void sleepFor(int amount, ChronoUnit unit) {
        try {
            Thread.sleep(Duration.of(amount, unit));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
