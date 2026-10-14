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
 */
public class SpinLockCounter implements Counter {
    private final AtomicBoolean locked = new AtomicBoolean(false);

    private long value; // guarded by the spin lock

    private void lock() {
        while (!locked.compareAndSet(false, true)) {
            Thread.onSpinWait();    // busy-waiting: blocks the thread until the lock is released
        }
    }

    private void unlock() {
        locked.set(false);
    }

    @Override
    public void increment() {
        lock();
        try {
            value++;
        } finally {
            unlock();
        }
    }

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