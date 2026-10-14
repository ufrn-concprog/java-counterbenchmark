import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe counter implementation using {@link AtomicLong}.
 * This class leverages atomic operations provided by the {@code AtomicLong} class
 * to ensure that updates and retrievals to the counter are performed without requiring
 * explicit synchronization mechanisms, such as locks or synchronized blocks.
 */
public class AtomicCounter implements Counter {
    private final AtomicLong value = new AtomicLong();

    @Override
    public void increment() {
        value.incrementAndGet();
    }

    @Override
    public long get() {
        return value.get();
    }
}