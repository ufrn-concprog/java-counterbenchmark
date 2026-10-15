import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Benchmarks the {@link Counter} implementations.
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public final class CounterBenchmark {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private CounterBenchmark() {}

    /**
     * Outcome of one trial.
     *
     * @param seconds Elapsed time, from the moment all threads were released until all had finished
     * @param correct Whether the final count equals the number of increments performed
     */
    public record Trial(double seconds, boolean correct) {}

    /**
     * Runs one trial: starts the threads, releases them together, and waits for all of them.
     *
     * @param counter               Counter (initialized at zero)
     * @param numThreads            Number of incrementing threads
     * @param incrementsPerThread   Incremented by each thread
     * @return Elapsed time and whether the final count was correct
     *
     * @throws InterruptedException   if interrupted while waiting
     * @throws BrokenBarrierException if the starting gate is broken
     */
    public static Trial runTrial(Counter counter, int numThreads, long incrementsPerThread)
            throws InterruptedException, BrokenBarrierException {
        CyclicBarrier startLine = new CyclicBarrier(numThreads + 1); // workers + this thread
        Thread[] threads = new Thread[numThreads];
        for (int t = 0; t < numThreads; t++) {
            threads[t] = new Thread(() -> {
                try {
                    startLine.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (long i = 0; i < incrementsPerThread; i++) {
                    counter.increment();
                }
            });
            threads[t].start();
        }

        startLine.await(); // opens the gate
        long start = System.nanoTime();
        for (Thread thread : threads) {
            thread.join();
        }
        double seconds = (System.nanoTime() - start) / 1e9;

        boolean correct = counter.get() == numThreads * incrementsPerThread;
        return new Trial(seconds, correct);
    }
}