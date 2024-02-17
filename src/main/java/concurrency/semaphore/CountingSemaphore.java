package concurrency.semaphore;

import net.jcip.annotations.GuardedBy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CountingSemaphore {

    private final int permits;

    @GuardedBy("lock")
    private int sharedCounter = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition acquire = lock.newCondition();
    private final Condition release = lock.newCondition();

    public CountingSemaphore(final int permits) {
        this.permits = permits;
    }

    public void acquire() throws Exception {
        lock.lock();
        try {
            while (sharedCounter == permits) {
                acquire.await();
            }
            sharedCounter++;
            release.signal();
        } finally {
            lock.unlock();
        }
    }

    public void release() throws Exception {
        lock.lock();
        try {
            while (sharedCounter == 0) {
                release.await();
            }
            sharedCounter--;
            acquire.signal();
        } finally {
            lock.unlock();
        }
    }
}
