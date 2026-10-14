import java.util.concurrent.locks.ReentrantLock;

/**
 * A counter implementation that uses synchronized methods to ensure mutual exclusion,
 * making it thread-safe. This class guarantees that only one thread can access the
 * critical sections of the code (incrementing or retrieving the counter value) at any given time.
 */
public class SynchronizedCounter implements Counter {
    private final ReentrantLock lock = new ReentrantLock();

    private long value;

    @Override
    public synchronized void increment() {
        value++;
    }

    @Override
    public synchronized long get() {
        return value;
    }
}