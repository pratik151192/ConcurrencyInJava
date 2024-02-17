package concurrency.boundedset;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implement a bounded set.
 */
@ThreadSafe
public class BoundedSet {

    @GuardedBy("lock")
    private final HashSet<Integer> set;

    private final ReentrantLock lock = new ReentrantLock();
    private final Semaphore semaphore;

    private final int limit;

    public BoundedSet(final int limit) {
        this.set = new HashSet<>();
        this.limit = limit;

        // this will be used to "bound" the number of elements allowed in the set, or the "number" of permits
        // allowed to add elements to the set.
        this.semaphore = new Semaphore(limit);
    }

    /**
     * Add element to the bounded set if the set is not bounded by the provided limit, or in other words,
     * we do not have enough permits left to go ahead and mutate our set.
     * @param v value to put
     */
    public void put(final int v) throws Exception {
        boolean success;
        semaphore.acquire();
        lock.lock();
        try {
            success = set.add(v);
        } finally {
            lock.unlock();
        }

        // this is important; we don't want to keep a semaphore blocked if our addition to the set wasn't successful.
        // think why this is outside our critical section :)
        if (!success) {
            semaphore.release();
        }
    }

    /**
     *
     * @param v the element to be removed from the set
     * @return if the element was successfully removed from the set
     */
    public boolean erase(int v) {
        boolean success;
        lock.lock();
        try {
            success = set.remove(v);
        } finally {
            lock.unlock();
        }

        // same reasoning as above; we only release the semaphore if the removal of the element was successful
        if (success) {
            semaphore.release();
        }

        return success;
    }
}

