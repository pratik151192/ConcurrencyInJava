package concurrency.oddeven;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class OddEvenCV {

    // re-entrancy allows a thread to re-acquire a lock if it had already been holding it from before.
    // this is powerful during object oriented concurrent programming. note that default java mutexes
    // or intrinsic locks are reentrant by default. You need to use a binary semaphore to behave as a mutex
    // if you want non-reentrant behaviors from a mutex in Java
    final ReentrantLock lock = new ReentrantLock(true);
    final Condition condition = lock.newCondition();

    @GuardedBy("lock")
    private int count = 1;

    private int n;
    public OddEvenCV(int n) {
        this.n = n;
    }

    public void odd() throws InterruptedException {

        // the template followed here is slightly different from others. In case of a synchronized
        // block, we used the object's intrinsic "wait" and "notify" methods for inter thread communication.
        // In case of reentrant locks, we extend it to include a condition and invoke the wait and signal
        // of those objects. Look at Yanis's video for more on this.
        while (count < n) {
            lock.lock();

            // always make sure your critical section is in a try block else we might not release the
            // associated mutex in case of an exception
            try {
                while (count % 2 == 0) {
                    if (count >= n) return;
                    condition.await();
                }

                System.out.println(count);
                count++;
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public void even() throws InterruptedException {
        while (count < n) {
            lock.lock();
            try {
                while (count % 2 == 1) {
                    if (count >= n) return;
                    condition.await();
                }

                System.out.println(count);
                count++;
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}
