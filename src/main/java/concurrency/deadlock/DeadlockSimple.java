package concurrency.deadlock;

import java.util.concurrent.CountDownLatch;

public class DeadlockSimple {

    public static void main(String... args) throws Exception {
        DeadLocked helper = new DeadLocked();

        final CountDownLatch count = new CountDownLatch(2);
        final CountDownLatch go = new CountDownLatch(1);

        final Thread t1 = new Thread(() -> {
            try {
                count.countDown();
                go.await();
                helper.bar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        final Thread t2 = new Thread(() -> {
            try {
                count.countDown();
                go.await();
                helper.foo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        t1.start();
        t2.start();

        count.await();
        go.countDown();

        t1.join();
        t2.join();
    }
}
