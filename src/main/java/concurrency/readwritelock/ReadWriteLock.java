package concurrency.readwritelock;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class ReadWriteLock {

    private boolean isWriting = false;
    private int readers = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notWriting = lock.newCondition();
    private final Condition notReading = lock.newCondition();

    public void acquireReadLock() throws InterruptedException {
        lock.lock();
        try {
            while (isWriting) {
                notWriting.await();
            }
            readers++;
        } finally {
            lock.unlock();
        }
    }

    public void acquireWriteLock() throws InterruptedException {
        lock.lock();
        try {
            while (readers != 0 || isWriting) {
                if (readers != 0) notReading.await();
                // if someone is writing, then also wait
            }
            isWriting = true;
        } finally {
            lock.unlock();
        }
    }

    public void releaseReadLock() throws InterruptedException {
        lock.lock();
        try {
            readers--;
            notReading.signal();
        } finally {
            lock.unlock();
        }
    }

    public void releaseWriteLock() {
        lock.lock();
        try {
            isWriting = false;
            notWriting.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
