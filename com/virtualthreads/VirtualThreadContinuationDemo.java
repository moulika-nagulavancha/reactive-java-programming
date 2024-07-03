package com.virtualthreads;

/**
 * How Virtual Thread runs under the hood when there is a Blocking code present
 */
public class VirtualThreadContinuationDemo {
    /*
    public static void main(String[] args) {
        var scope = new ContinuationScope("My Scope");
        var continuation = new Continuation(
                scope,
                () -> {
                    System.out.println("Running in a continuation");
                    Continuation.yield(scope);
                    System.out.println("After the call to yield");
                }
        );

        System.out.println("Running in a main method");
        continuation.run();
        System.out.println("Back in the main method");
        continuation.run();
        System.out.println("Back again in the main method");
    }*/
}

// run using '--enable-preview --add-exports java.base/jdk.internal.vm=ALL-UNNAMED'

/***
 * Output:-
 * Running in a main method
 * Running in a continuation
 * Back in the main method
 * Back again in the main method
 */
