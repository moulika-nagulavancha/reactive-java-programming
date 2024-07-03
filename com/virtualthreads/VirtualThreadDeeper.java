package com.virtualthreads;

import java.util.concurrent.Executors;

public class VirtualThreadDeeper {
    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            System.out.println("Running a thread: " + Thread.currentThread());
        };

        var thread = Thread.ofVirtual()
                .name("Virtual Thread")
                .unstarted(task);
        thread.start();
        thread.join();

        try (var es = Executors.newVirtualThreadPerTaskExecutor()) {
            es.submit(task);
        }
    }
}
