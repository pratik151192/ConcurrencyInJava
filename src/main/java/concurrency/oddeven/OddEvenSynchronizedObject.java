package concurrency.oddeven;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class OddEvenSynchronizedObject {
    // every object has an implicit monitor associated with it and can serve as a lock
    private final Object lock = new Object();

    @GuardedBy("lock")
    int count = 1;

    private int n;

    public OddEvenSynchronizedObject(int n) {
        this.n = n;
    }

    public void odd() throws InterruptedException {
        synchronized (lock) {
            while (count < n) {

                while (count % 2 == 0) {
                    if (count >= n) return;
                    lock.wait();
                }

                System.out.println(count);
                count++;
                lock.notify();
            }
        }
    }

    public void even() throws InterruptedException {
        synchronized (lock) {
            while (count < n) {
                while (count % 2 == 1) {
                    if (count >= n) return;
                    lock.wait();
                }

                System.out.println(count);
                count++;
                lock.notify();
            }
        }
    }
}
