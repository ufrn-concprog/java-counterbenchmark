import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe counter implementation using {@link AtomicLong}.
 * This class leverages atomic operations provided by the {@code AtomicLong} class
 * to ensure that updates and retrievals to the counter are performed without requiring
 * explicit synchronization mechanisms, such as locks or synchronized blocks.
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class AtomicCounter implements Counter {
    /**
     * The underlying atomic long representing the counter value.
     */
    private final AtomicLong value = new AtomicLong();

    /**
     * Constructs a new {@code AtomicCounter} with an initial value of zero.
     */
    public AtomicCounter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increment() {
        value.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long get() {
        return value.get();
    }
}