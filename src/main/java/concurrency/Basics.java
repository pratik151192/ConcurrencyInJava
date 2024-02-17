package concurrency;

public class Basics {
    /**
     *
     * Mutex
     *  - Provides exclusive access to a data structure
     *  - In-process mutex
     *      - Has a performance optimization to avoid kernel transitions when locks are uncontended
     *      - Can do book-keeping within ourselves to see if someone is waiting on the mutex or not
     *      - Why is this optimization important?
     *          -
     *  - Cross-process mutex
     *      - Always requires a kernel transition, a.k.a, talking to the operating system.
     *      - Example: FileChannel lock for locking file access between multiple processes (java.nio)
     *      - Example:
     *
     *   - Thread
     *      - Is a data structure; a stack with some metadata
     *
     */

}
