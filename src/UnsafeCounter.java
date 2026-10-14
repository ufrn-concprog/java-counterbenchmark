/**
 * A counter implementation that does not guarantee thread-safety.<br>
 * {@code UnsafeCounter} uses a volatile long to store the counter value, but
 * the {@code increment()} method is not thread-safe due to the lack of atomic operations.
 * This means that simultaneous updates from multiple threads can result in race conditions,
 * leading to an inconsistent or incorrect counter value.
 */
public class UnsafeCounter implements Counter {
    private volatile long value;

    @Override
    public void increment() {
        value++; // read, add, write: not atomic
    }

    @Override
    public long get() {
        return value;
    }
}