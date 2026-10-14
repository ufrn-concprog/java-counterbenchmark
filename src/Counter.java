/** A shared counter, implemented with a different synchronization mechanism in each subclass. */
public interface Counter {

    /** Adds one to the counter. Safe to call from several threads only if the implementation says so. */
    void increment();

    /** @return the current value of the counter */
    long get();
}