/**
 * A counter implementation that does not guarantee thread-safety.<br>
 * {@code UnsafeCounter} uses a volatile long to store the counter value, but
 * the {@code increment()} method is not thread-safe due to the lack of atomic operations.
 * This means that simultaneous updates from multiple threads can result in race conditions,
 * leading to an inconsistent or incorrect counter value.
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class UnsafeCounter implements Counter {
    /**
     * The current value of the counter; updates are not atomic.
     */
    private volatile long value;

    /**
     * Constructs a new {@code UnsafeCounter} with an initial value of zero.
     */
    public UnsafeCounter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment() {
        value++; // read, add, write: not atomic
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long get() {
        return value;
    }
}