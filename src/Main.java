import java.util.function.Supplier;

/**
 * Benchmark driver that compares four ways of incrementing a shared counter:
 * no mutual exclusion, a mutex, an atomic, and a spin lock.
 *
 * <p>Usage: {@code java Main [unsynchronized|synchronized|atomic|spinlock]}. With no argument, all four
 * run in the same JVM, which adds call overhead to every mechanism.</p>
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class Main {
    /**
     * Total increments per trial, divided among the threads (all thread counts divide it evenly).
     */
    private static final long TOTAL_INCREMENTS = 5000000;

    /**
     * Number of threads to use for each benchmark run.
     */
    private static final int[] THREAD_COUNTS = {1, 2, 4, 8, 16};

    /**
     * Number of runs to measure for each configuration.
     */
    private static final int MEASURED_RUNS = 20;

    /**
     * Discarded runs before measuring each configuration to let the JVM warm up.
     */
    private static final int WARMUP_RUNS = 5;

    /**
     * Constructs a new {@code Main} instance.
     */
    public Main() {
    }

    /**
     * Main entry point for executing the benchmarks.
     *
     * @param args Command-line arguments specifying which counter mechanism to benchmark
     *             ({@code unsynchronized}, {@code synchronized}, {@code atomic}, {@code spinlock}, or {@code all})
     * @throws Exception If an error occurs during benchmark execution
     */
    public static void main(String[] args) throws Exception {
        String selected = args.length > 0 ? args[0] : "all";

        if (selected.equals("all") || selected.equals("unsynchronized")) {
            runBenchmark("Unsynchronized", UnsafeCounter::new);
        }
        if (selected.equals("all") || selected.equals("synchronized")) {
            runBenchmark("Synchronized", SynchronizedCounter::new);
        }
        if (selected.equals("all") || selected.equals("atomic")) {
            runBenchmark("Atomic", AtomicCounter::new);
        }
        if (selected.equals("all") || selected.equals("spinlock")) {
            runBenchmark("Spin Lock", SpinLockCounter::new);
        }
    }

    /**
     * Measures one mechanism at every thread count. Each configuration gets its own warm-up, since
     * contended code paths are only exercised (and compiled) when several threads compete.
     *
     * @param label   name printed in the output
     * @param factory creates a fresh counter for every trial
     * @throws Exception if an error occurs during trial runs
     */
    private static void runBenchmark(String label, Supplier<Counter> factory) throws Exception {
        System.out.printf("=== %s ===%n", label);
        for (int threads : THREAD_COUNTS) {
            long perThread = TOTAL_INCREMENTS / threads;

            for (int i = 0; i < WARMUP_RUNS; i++) {
                CounterBenchmark.runTrial(factory.get(), threads, perThread);
            }

            double[] times = new double[MEASURED_RUNS];
            int correctRuns = 0;
            for (int i = 0; i < MEASURED_RUNS; i++) {
                CounterBenchmark.Trial trial = CounterBenchmark.runTrial(factory.get(), threads, perThread);
                times[i] = trial.seconds();
                if (trial.correct()) {
                    correctRuns++;
                }
            }

            double mean = Stats.mean(times);
            double stdDev = Stats.stdDev(times, mean);
            System.out.printf("%2d threads | Mean: %.4f s | Standard deviation: %.4f s | Correct: %d/%d%n",
                    threads, mean, stdDev, correctRuns, MEASURED_RUNS);
        }
        System.out.println();
    }
}