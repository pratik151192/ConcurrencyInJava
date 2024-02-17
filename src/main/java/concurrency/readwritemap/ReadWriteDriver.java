package concurrency.readwritemap;

import java.util.Arrays;

public class ReadWriteDriver {
    public static void main(String... args) throws Exception {
        ReadWriteMap readWriteMap = new ReadWriteMap();

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
               readWriteMap.acquireReadLock();
               try {
                   Thread.sleep(2000);
               } catch (Exception e) {

               } finally {
                   readWriteMap.releaseReadLock();
               }

            });
            thread.start();
            threads[i] = thread;
        }
        Thread printer = new Thread(() -> {
            while ((readWriteMap.cacheReadWriteLock.getReadLockCount() != 0)) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
                System.out.println(readWriteMap.cacheReadWriteLock.getReadLockCount());
                System.out.println(readWriteMap.cacheReadWriteLock.getReadHoldCount());
                System.out.println(readWriteMap.cacheReadWriteLock.getQueueLength());
            }
        });

        printer.start();
        Arrays.stream(threads).forEach(t -> {
            try {
                t.join();
            } catch (Exception e) {

            }
        });
        printer.join();
    }
}
