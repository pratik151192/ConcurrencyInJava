package concurrency.deadlock;

import net.jcip.annotations.GuardedBy;

/**
 * Simulate a deadlock
 */
public class DeadLocked {
    @GuardedBy("this")
    private final Object o1 = new Object();
    @GuardedBy("this")
    private final Object o2 = new Object();

    public void foo() throws Exception {
        synchronized (o1) {
            // adding sleep for guaranteed deadlock
            Thread.sleep(1000);
            synchronized (o2) {
                // do work
            }
        }
    }

    public void bar() throws Exception {
        synchronized (o2) {
            // adding sleep for guaranteed deadlock
            Thread.sleep(1000);
            synchronized (o1) {
                // do work
            }
        }
    }
}
