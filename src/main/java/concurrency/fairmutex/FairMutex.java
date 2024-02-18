package concurrency.fairmutex;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class FairMutex {

    // This queue will contain a semaphore for each waiting thread that we will
    // use for signalling. Look at isLocked below for handling special case for the
    // first thread that asks for the mutex.

    @GuardedBy("lock")
    final Queue<Semaphore> queue = new LinkedList<>();

    // A reentrant lock to ensure atomicity of operations within the lock() and unlock() methods.

    final ReentrantLock lock = new ReentrantLock(false /* fair */);

    // The first thread that asks for the lock is a special case because we don't want
    // to add a semaphore for it to the queue. This is because the way we signal the next
    // thread to resume is execution is when we signal its semaphore from the current thread
    // which originally held the lock. So this flag is simply set to true by the first thread
    // without doing anything else. When it returns after doing its work, it will check the
    // queue if it had any elements that might indicate waiting threads and wake them up.

    @GuardedBy("lock")
    private boolean isLocked = false;

    public void lock() {
        lock.lock();  // Acquire the reentrant lock to ensure atomicity
        try {
            if (isLocked) {
                // Lock is held by another thread, enqueue a semaphore to be signaled when the lock is released
                final Semaphore semaphore = new Semaphore(0);
                queue.offer(semaphore);
                lock.unlock();  // Release the reentrant lock while waiting for the semaphore
                try {
                    semaphore.acquire();  // Wait for the semaphore to be signaled
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lock.lock();  // Reacquire the reentrant lock before proceeding
            }
            isLocked = true;  // Set the flag to indicate the mutex is now locked
        } finally {
            lock.unlock();  // Release the reentrant lock
        }
    }

    public void unlock() {
        lock.lock();  // Acquire the reentrant lock to ensure atomicity
        try {
            if (isLocked) {
                // Check if there are waiting threads
                if (!queue.isEmpty()) {
                    final Semaphore semaphore = queue.poll();
                    semaphore.release();  // Signal the semaphore to wake up the next waiting thread
                } else {
                    isLocked = false;  // If no waiting threads, set the flag to indicate the mutex is now unlocked
                }
            }
        } finally {
            lock.unlock();  // Release the reentrant lock
        }
    }
}
