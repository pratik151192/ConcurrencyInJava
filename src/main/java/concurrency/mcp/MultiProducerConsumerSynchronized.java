package concurrency.mcp;

import java.util.ArrayDeque;
import java.util.Deque;

public class MultiProducerConsumerSynchronized {

    static class MultiProducerConsumerQueue {
        private final Deque<Integer> queue = new ArrayDeque<>();
        final Object lock = new Object();
        int limit;

        public MultiProducerConsumerQueue(int limit) {
            this.limit = limit;
        }

        void push(int val) throws InterruptedException {
            synchronized (lock) {
                while (queue.size() == limit) {
                    lock.wait();
                }
                queue.addFirst(val);
                lock.notify();

            }
        }

        int pop() throws InterruptedException {
            synchronized (lock) {
                while (queue.isEmpty()) {
                    lock.wait();
                }
                int val = queue.removeFirst();
                lock.notify();
                return val;
            }
        }
    }

    public static void main(String... args) throws Exception {
        MultiProducerConsumerQueue queue = new MultiProducerConsumerQueue(10);

    }
}
