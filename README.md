# Benchmarking Synchronization Mechanisms with a Shared Counter in Java
 
[![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
 
This benchmark measures the cost of four ways to increment a shared counter from several threads in Java: using no synchronization, synchronized methods, an atomic variable, and a spin lock. This repository serves a pedagogical purpose, showing that correctness and speed are different questions, and what contention costs. 

This project is part of the **Concurrent Programming** module at the [Federal University of Rio Grande do Norte (UFRN)](https://www.ufrn.br), Natal, Brazil.
 
## 📃 About
 
### Mechanisms
 
| Mechanism            | Class                                                 | How it works                                                                                                                                                                     | Correct?             |
|:---------------------|:------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------|
| Unsynchronized       | [`UnsafeCounter`](src/UnsafeCounter.java)             | Increment to a volatile variable                                                                                                                                                 | No: it loses updates |
| Synchronized methods | [`SynchronizedCounter`](src/SynchronizedCounter.java) | Increment and get methods synchronized (mutual exclusion)                                                                                                                        | Yes                  |
| Atomic               | [`AtomicCounter`](src/AtomicCounter.java)             | [`AtomicLong.incrementAndGet()`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/atomic/AtomicLong.html), a hardware atomic read-modify-write  | Yes                  |
| Spin lock            | [`SpinLockCounter`](src/SpinLockCounter.java)         | A compare-and-swap loop on an `AtomicBoolean`, with `Thread.onSpinWait()`                                                                                                        | Yes                  |
 
All four implement the [`Counter`](src/Counter.java) interface, so the benchmark treats them uniformly.
 
### Methodology
 
1. **Fixed total work.** Every configuration performs 5,000,000 increments in total, divided evenly among 1, 2, 4, 8, and 16 threads. The 1-thread case refers to each mechanism's *uncontended* cost, and the other rows show what contention adds.
2. **Starting gate.** The worker threads and the main thread wait at a [`CyclicBarrier`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CyclicBarrier.html), so all workers begin incrementing together. Timing starts when the gate opens and ends when all workers have finished. Thread creation is not timed.
3. **Warm-up.** For each configuration, 5 trials run and are discarded.
4. **Measurement.** 20 measured trials per configuration, reporting the mean and standard deviation.
5. **Correctness check.** After each trial, the final count is compared with the number of increments performed, and the number of correct trials is reported.

### Design Notes
 
* **Why the unsynchronized counter is *volatile*.** `volatile` guarantees visibility but not atomicity: the increment is still a read, an addition, and a write, so threads can interleave between those steps and updates are lost. Without `volatile`, the JVM could keep the value in a register across the loop and hide the problem.
* **Why a starting gate.** Staggered thread start-up would hide contention.
* **A spin lock is a lock.** If the thread holding it is descheduled, every other thread spins until the holder runs again. This is visible when threads outnumber cores.
* **No speedup is expected.** All threads increment the same counter, so adding threads cannot make the work faster. The benchmark measures the *cost of sharing*, not parallel speedup.

## 📂 Repository Structure
 
```text
java-counterbenchmark/
└── doc/                          # Javadoc HTML documentation
└── LICENSE
└── README.md
└── src/
    ├── AtomicCounter.java        # AtomicLong
    ├── Counter.java              # Interface implemented by all four mechanisms
    ├── CounterBenchmark.java     # One trial: threads, starting gate, timing, correct
    ├── Main.java                 # Benchmark's entry point
    ├── SpinLockCounter.java      # Spin lock built on AtomicBoolean compare-and-swap
    ├── Stats.java                # Mean and sample standard deviation
    ├── SynchronizedCounter.java  # Synchronized methods
    └── UnsafeCounter.java        # Volatile counter, no mutual exclusion (loses updates)
```
 
## 🚀 Getting Started
 
### Prerequisites
 
* Java Development Kit (JDK) 17 or newer (the code uses a `record`; `Thread.onSpinWait()` requires Java 9)
* A terminal or IDE

The program uses only the Java standard library, so there are no additional dependencies.
 
### Running
 
Run each mechanism at once:
 
```bash
for m in unsynchronized synchronized atomic spinlock; do java -cp out Main $m; done
```
 
Or run a single mechanism. For example:
 
```bash
java -cp out Main atomic
```
 
With no argument, all four mechanisms run in the same JVM. This is convenient for a quick check, but the benchmark results will be less reliable.
 
The workload is configured by constants at the top of [`Main`](src/Main.java): 

| Constant           | Description                                                                                | Value set      |
|:-------------------|:-------------------------------------------------------------------------------------------|:---------------|
| `TOTAL_INCREMENTS` | Total increments per trial, divided among the threads (all thread counts divide it evenly) | 5,000,000      |
| `THREAD_COUNTS`    | Number of threads to use for each benchmark run                                            | 1, 2, 4, 8, 16 |
| `MEASURED_RUNS`    | Number of runs to measure for each configuration                                           | 20             |
| `WARMUP_RUNS`      | Discarded runs before measuring each configuration to let the JVM warm up                  | 5              |
 
### Output Example
 
```text
=== Atomic ===
 1 threads | Mean: 0.0099 s | Standard deviation: 0.0003 s | Correct: 20/20
 2 threads | Mean: 0.0184 s | Standard deviation: 0.0013 s | Correct: 20/20
 4 threads | Mean: 0.0353 s | Standard deviation: 0.0028 s | Correct: 20/20
 8 threads | Mean: 0.1852 s | Standard deviation: 0.0231 s | Correct: 20/20
16 threads | Mean: 0.1683 s | Standard deviation: 0.0554 s | Correct: 20/20
```
 
The `Correct` column shows how many of the 20 measured trials ended with the right count.
 
These are hypotheses to check, not guaranteed results:
 
* **Unsynchronized:** correct with one thread and wrong with two or more threads; it is the fastest strategy due to zero synchronization overhead.
* **Synchronized method:** always correct; under contention, threads block and wake up.
* **Atomic:** always correct; it delivers near-optimal performance at low thread counts using hardware-level compare-and-swap, but suffers moderate performance loss under high contention due to repeated retry loops.
* **Spin lock:** it may be competitive with fewer threads, but causes severe performance degradation as thread count scales because threads consume CPU cycles while busy-waiting for the lock.

## 🧪 Things to Explore
 
1. Add [`LongAdder`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/atomic/LongAdder.html) as a fifth mechanism.
2. Replace the `synchronized` block with a [`ReentrantLock`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html) object. Do the results change?
3. Make the critical section longer (e.g., add some computation inside the lock). At what point does the spin lock stop being competitive?
 
## 🤝 Contributing
 
Contributions are welcome! Fork this repository and submit a pull request 🚀
 
## 📜 License
 
This project is licensed under the [MIT License](LICENSE).
