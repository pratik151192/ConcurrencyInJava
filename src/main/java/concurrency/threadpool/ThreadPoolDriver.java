package concurrency.threadpool;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadPoolDriver {

    public static void main(String... args) throws Exception {
        final ThreadPool threadPool = new ThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPool.addWork(() -> {
                System.out.println(ThreadLocalRandom.current().nextInt());
            });
        }
        threadPool.quit();
    }
}
