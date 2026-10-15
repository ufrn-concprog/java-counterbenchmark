/**
 * A shared counter, implemented with a different synchronization mechanism in each implementation.
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public interface Counter {

    /**
     * Adds one to the counter. Safe to call from several threads only if the implementation says so.
     */
    void increment();

    /**
     * Retrieves the current value of the counter.
     *
     * @return the current value of the counter
     */
    long get();
}