package com.scopedvalues;

import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;

public class ScopedValuesDemo {
        public static void main(String[] args) throws InterruptedException {
            ScopedValue<String> scopedValue = ScopedValue.newInstance();
    
            Runnable task = () -> {
              if (scopedValue.isBound()) {
                  System.out.println("Scoped Value bound to: " + scopedValue.get());
              } else {
                  System.out.println("Scoped value not bound");
              }
            };
    
            ScopedValue.where(scopedValue, "KEY").run(task);
    
            Thread thread1 = Thread.ofPlatform().unstarted(task);
            ScopedValue.where(scopedValue, "KEY")
                            .run(thread1::start);
            thread1.join();
    
            Thread thread2 = Thread.ofVirtual().unstarted(task);
            ScopedValue.where(scopedValue, "KEY")
                    .run(thread2::start);
            thread2.join();
    
            System.out.println("Running task");
    
            task.run();
    
            // Within the structured task scope
            Callable task2 = () -> {
                if (scopedValue.isBound()) {
                    System.out.println("Scoped Value bound to: " + scopedValue.get());
                    return scopedValue.get();
                } else {
                    System.out.println("Scoped value not bound");
                    return "unbound";
                }
            };
    
            ScopedValue.where(scopedValue, "KEY")
                    .run(
                            () -> {
                                try ( var scope = new StructuredTaskScope<String>() ) {
                                    scope.fork(task2);
                                    scope.join();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
        }
    }