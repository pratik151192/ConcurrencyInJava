package concurrency.mcp;

public class MultiProducerConsumer {

    public static void main(String... args) throws Exception {
        MultiProducerConsumerQueue queue = new MultiProducerConsumerQueue(10);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 9; i++) {
                queue.push(10);
            }
            queue.push(12);
        });

        Thread t2 = new Thread(() -> {
            queue.pop();
        });

        t1.start();
        Thread.sleep(2000);
        t2.start();
        t1.join();
        t2.join();
    }
}
