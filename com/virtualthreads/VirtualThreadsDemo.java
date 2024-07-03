package com.virtualthreads;

public class VirtualThreadsDemo {
    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            System.out.println("Running a thread: " + Thread.currentThread().getName());
            System.out.println("Running in a daemon thread: " + Thread.currentThread().isDaemon());
        };

        Thread thread1 = new Thread(task);
        thread1.start();
        thread1.join();

        System.out.println();
        Thread thread2 = Thread.ofPlatform()
                .daemon()
                .name("Platform Thread 2")
                .unstarted(task);
        thread2.start();
        thread2.join();

        System.out.println();
        Thread thread3 = Thread.ofVirtual()
                .name("Virtual Thread 3")
                .unstarted(task);
        thread3.start();
        thread3.join();

        System.out.println();
        Thread thread4 = Thread.startVirtualThread(task);
        thread4.join();
    }
}
