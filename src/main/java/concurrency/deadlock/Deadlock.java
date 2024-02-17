package concurrency.deadlock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {

    private final ReentrantLock m1 = new ReentrantLock();
    private final Condition c1 = m1.newCondition();

    private final ReentrantLock m2 = new ReentrantLock();
    private final Condition c2 = m2.newCondition();

    boolean toggle = false;

    private void foo() throws Exception {
        m1.lock();
        try {
            while (!toggle) {
                c2.await();
            }
            toggle = true;
            c1.signal();
        } finally {
            m1.unlock();
        }
    }

    private void bar() throws Exception {
        m2.lock();
        try {
            while (toggle) {
                c1.await();
            }
            toggle = false;
            c2.signal();
        } finally {
            m2.unlock();
        }

    }

    public static void main(String... arg) throws Exception {
        Deadlock d = new Deadlock();
        Thread t1 = new Thread(() -> {
            try {
                d.bar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                d.foo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
