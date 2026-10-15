import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A counter using a spin lock to achieve thread-safe operations.
 * The spin lock relies on busy-waiting through the use of {@code AtomicBoolean}.
 * This class ensures mutual exclusion by manually acquiring and releasing the lock
 * before and after accessing the counter's value.<br>
 *
 * Unlike other locking mechanisms (e.g., synchronized blocks), spin locks avoid
 * context switches by keeping threads in an active waiting state.
 * However, this can lead to high CPU utilization in contention-heavy environments.
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class SpinLockCounter implements Counter {
    /**
     * State of the spin lock: {@code true} if locked, {@code false} if unlocked.
     */
    private final AtomicBoolean locked = new AtomicBoolean(false);

    /**
     * The current value of the counter, guarded by the spin lock.
     */
    private long value;

    /**
     * Constructs a new {@code SpinLockCounter} with an initial value of zero.
     */
    public SpinLockCounter() {
    }

    /**
     * Acquires the spin lock, busy-waiting until it becomes available.
     */
    private void lock() {
        while (!locked.compareAndSet(false, true)) {
            Thread.onSpinWait();    // busy-waiting: blocks the thread until the lock is released
        }
    }

    /**
     * Releases the spin lock.
     */
    private void unlock() {
        locked.set(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment() {
        lock();
        try {
            value++;
        } finally {
            unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long get() {
        lock();
        try {
            return value;
        } finally {
            unlock();
        }
    }
}