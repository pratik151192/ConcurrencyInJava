package concurrency.oddeven;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class OddEvenSynchronizedBlock {

    @GuardedBy("this")
    int count = 1;

    private final int n;

    public OddEvenSynchronizedBlock(int n) {
        this.n = n;
    }

    public void odd() throws InterruptedException {
        // now the unrelated work is not blocked by other threads
        doUnrelatedWork();

        // synchronized(this) is similar a synchronized method except that the
        // synchronization will span across the method body. Here we limit the scope.
        // Note that static methods which are synchronized take the lock of the class, as opposed to the lock on
        // the object on which the method is invoked in case of non-static methods.
        synchronized (this) {
            while (count < n) {
                    while (count % 2 == 0) {
                        if (count >= n) return;
                        wait();
                    }
                    System.out.println(count);
                    count++;
                    notify();
                }
            }
    }

    public void even() throws InterruptedException {

        doUnrelatedWork();

        synchronized (this) {
            while (count < n) {
                    while (count % 2 == 1) {
                        if (count >= n) return;
                        wait();
                    }
                    System.out.println(count);
                    count++;
                    notify();
                }
            }
    }

    private static void doUnrelatedWork() {
        //
    }
}
