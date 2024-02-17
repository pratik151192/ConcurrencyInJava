package concurrency.mcp;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MPCSemaphore {
    private  ReentrantReadWriteLock cacheReadWriteLock;

    static class MultiProducerConsumerQueue {
        final Set<Integer> set = new HashSet<>();
        final ReentrantLock lock = new ReentrantLock();
        final Semaphore semaphore = new Semaphore(1);
        int limit;

        public MultiProducerConsumerQueue(int limit) {
            this.limit = limit;
        }

        void push(int val) throws Exception {

            semaphore.acquire();
            lock.lock();
            try {
                System.out.println("Now pushed");
                set.add(val);
            } finally {
                lock.unlock();
            }
        }

        void pop(int k) throws Exception {
            lock.lock(); // required for exclusive access of our share data structure, i.e., the queue
            try {
                set.remove(k);
            } finally {
                lock.unlock();
            }
            semaphore.release();
        }

    }

    public static void main(String... args) throws Exception {
        MultiProducerConsumerQueue queue = new MultiProducerConsumerQueue(10);
        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.push(10);
                }
                //queue.push(12);
            } catch (Exception e) {
                System.err.println(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                queue.pop(0);
            } catch (Exception e) {
                System.err.println(e);
            }
        });

        Thread t4 = new Thread(() -> {
            try {
                queue.pop(1);
            } catch (Exception e) {
                System.err.println(e);
            }
        });

        t1.start();
        Thread.sleep(2000);
        t2.start();
        t1.join();
        t2.join();
        t4.start();
        t4.join();
    }



}
