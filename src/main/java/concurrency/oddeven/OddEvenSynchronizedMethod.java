package concurrency.oddeven;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * There are four annotations provided by the JCIP package. These are highly recommended to be used
 * during concurrent programming for the purposes of a/ maintainability and b/ reducing confusion aroun
 * the state variables of an object. "State" variables are the ones that are shared across threads and
 * demand concurrency around it to achieve correctness. Note that these annotations are for readability
 * and the JVM won't do anything magical to make a class @ThreadSafe, for instance.
 */

// ThreadSafe simply means the class is thread safe. A class without this annotation can be assumed
// to be not thread safe. Immutables are thread safe by default (since they are stateless).
@ThreadSafe
public class OddEvenSynchronizedMethod {

    @GuardedBy("this")
    int count = 1;

    private int  n;

    public OddEvenSynchronizedMethod(int n) {
        this.n = n;
    }

    public synchronized void odd() throws InterruptedException {
        // the reason i've added this dummy method is to convey that using synchronization method
        // might lead to needless synchronization on work that isn't part of our critical section.
        doUnrelatedWork();
        while (count < n) {

            // if number is even, wait until it's odd
            while (count % 2 == 0) {
                // this is required as we dont want to wait if our terminal condition has been reached
                if (count >= n) return;
                wait();
            }

            System.out.println(count);
            count++;
            // notify that the number is now even
            notify();
        }
    }

    public synchronized void even() throws InterruptedException {
        doUnrelatedWork();
        while (count < n) {

            // if number is odd, wait until it's odd
            while (count % 2 == 1) {
                // this is required as we dont want to wait if our terminal condition has been reached
                if (count >= n) return;
                wait();
            }

            System.out.println(count);
            count++;
            // notify that the number is now odd
            notify();
        }
    }

    private static void doUnrelatedWork() {
        //
    }
}
