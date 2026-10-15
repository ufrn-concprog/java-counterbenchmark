import java.util.concurrent.locks.ReentrantLock;

/**
 * A counter implementation that uses synchronized methods to ensure mutual exclusion,
 * making it thread-safe. This class guarantees that only one thread can access the
 * critical sections of the code (incrementing or retrieving the counter value) at any given time.
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class SynchronizedCounter implements Counter {
    /**
     * Lock instance for synchronization purposes.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * The current value of the counter, guarded by synchronization on {@code this}.
     */
    private long value;

    /**
     * Constructs a new {@code SynchronizedCounter} with an initial value of zero.
     */
    public SynchronizedCounter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void increment() {
        value++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized long get() {
        return value;
    }
}