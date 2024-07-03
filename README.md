# reactive-java-programming

A dive into the Java 21 features related to the reactive programming.

# Throughput of the Application

    - No. of operations(requests/transactions/messages) that you can process per second.
    - Best throughput = reactive programming = asynchronous programming

# Synchronous vs Asynchronous execution

    - Synchronous = executed immediately. ex: Math.sqrt()
    - Asynchronous = executed only when needed. ex: List.forEach() = done at runtime.
    - Both these approaches are executed within the 'main' thread only.

# Concurrent Execution

    - Code being executed on different threads.
    - ExecutorService (Runnable & Callable) = asynchronous + concurrent
    - Concurrent code is always asynchronous but contrary is not always true.

# Blocking Code

    - Using CPU as a core resource for execution and is not doing anything = resource wastage.
    - Causes: Thread waiting for I/O, synchronized block waiting
    - Outcome: Context switching = costly process.
    - Solution: Reactive programming to write non-blocking code

# Analyzing the Performance of Web Request

    - creating the request = memory location = CPU busy
    - sending the request = network request = Waiting state = CPU usage is 0
    - Getting Response = memory location = CPU busy
    - During this request/response cycle, the CPU code is idle waiting for your data.
    - Solution: Send your network requests in parallel = one request per thread model
    - Problem: Operating System overhead.
    - Java Threads = Platform Threads = Operating System Threads = Kernel Threads.
    - Even Thread Pools are expensive.
    - Solution: Reactive programming (many requests per thread), Virtual Threads (lighter 1 req per thread approach)

# Reactive Programming

    - One thread handle many requests.
    - Split the request/response ecycle into small units without blocking code.
    - CompletableFuture API feature can be used.
    - Problem: complicated code, unit tests, error handling, profiling is impossible, Debugging is hard.
    - Works with Platform threads.
    - Cost (maintaenance) is actually high.
    - Solution: Virtual Threads

# Virtual Threads

    - one request per thread model approach.
    - Increases the throughput of the application significantly.
    - Adapted by application development frameworks tomcat, jetty, spring boot, Quarkus, Micronaut, Helidon.
    - Virtul threads extend Thread in Java.
    - Create the virtual threads on demand and die once the task is done.
    - Carrier Threads
        - Virtual Threads are running on top of Platform Threads = Carrier Threads
        - present inside the Fork Join Pool.
        - Carrier Threads = Cores on CPU    
        - Worker Stealing is done to keep the carrier threads busy all time.
    - Running a task in Virtual Thread is actually an overhead.
    - However, if this task is non-blocking code then running the task in the Platform thread is less expensive than using Virtual Threads.
    - Virtual Threads scheduler is non-preemptive whereas platform threads is preemptive.
    - Don't use Virtual Threads for in-memory computations.
    - Use Virtual Threads only when you have the blocking code in place.
    - During Blocking operation, Virtual thread calls Continuation.yield() and moves the thread away from its carrier thread and callback handler is issued.
    - This moved away virtual thread is stored in heap memory. Virtual thread stacks are moved accordingly.
    - Once the data is ready, the callback will notify Continuation.run() and thus another carrier thread uses work stealing approach.
    - This is why, task is seen jumping from one carrier thread to another thread.

    Virtual Threads stack --> Heap Memory --> ForkJoinPool Waitlist --> Work Stealing approach --> Carrier Threads
    - Pinning a Virtual Thread:
        - There is a case where you cannot move the virtual thread stack to a heap memory.
        - When we call the native code from Java, this code is blocking, and playing with stack addresses and if this executed on Virtual Threads.
        - In this case, Virtual Thread API actually detects this native code, and pins this virtual thread on your carrier thread and no more usage of Heap memory.
        - The performance depends on the time used for the Blocking code.
        - Synchonized block with the in-memory code (non-blocking code) has no performance hit.

# Reactive Programming vs Virtual Threads

    - Reactive = Complicated Programming model whereas Virtual Threads = Simple Programming Model
    - Reactive = Non-Blocking Code whereas Virtual Threads = Blocking Code

# Structured Concurrency

    - New way to organize the Virtual Threads code and Reactive code.
    - There can be cases where the web server as part of the blocking code never responds and throws timeout exception causing the other reliable threads to become uninterrupted and loose.
    - Rebooting the application fixes but comes back again after few days of running.
    - Solution: Structured concurrency = managing the threads.
    - Three types:
        1. Using StructuredTaskScope
        2. Shutting down on success
        3. Shutting down on failure
    - Each virtual thread is given a bound lifecycle. This cannot be done with Platform threads as we pool the threads beforehand.
    - begin --> Create a StructuredTaskScope --> perform async tasks --> Close the StructuredScopeTask --> end
    - Structured Concurency = No more Loose Threads.

# Thread Locals & Scoped Values

    - Thread Local variables
        - Passing arguments from one element of the application to another element typically methods, classes etc.,
        - These arguments are not passed within the method calls or so.
        - Internally handled through maps using key, value pairs.
        - Application servers utilize the Thread Locals to handle HTTP sessions, Authentication, DB transactions etc.,
        - Virtual Threads support Thread Local Variables.
        - Problem: After the introduction of ExecutorService thread pools, these Thread Local variables are unbound and may retain the information within them across the threads.
        - Also these variables are mutable and are costly.
        - Solution: Scoped Variables
    - Scoped Variables
        - These variables are not bound to any thread.
        - These are also immutable and improve the better performance.
        - Bound to the execution of the method call.