package concurrency.boundedset;

import java.util.concurrent.atomic.AtomicInteger;

public class BoundedSetDriver {

    public static void main(String... args) throws Exception {
        final BoundedSet set = new BoundedSet(10);

        final AtomicInteger count = new AtomicInteger(0);
        Thread t1 = new Thread(() -> {
            while (count.get() < 100) {
                try {
                    set.put(count.getAndIncrement());
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        });

        final AtomicInteger count2 = new AtomicInteger(0);
        Thread t2 = new Thread(() -> {
            while (count2.get() < 100) {
                try {
                    set.erase(count2.getAndIncrement());
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
