package concurrency.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimerDriver {

    public static void main(String... args) {
        final Timer timer = new Timer(100);

        final ExecutorService executorService = Executors.newFixedThreadPool(100);

        long timeTaken = timer.timeNanos(executorService, new Runnable() {
            @Override
            public void run() {
                // do nothing
            }
        });
        executorService.shutdown();

        System.out.println(timeTaken);
    }

    private static void run() {
        return;
    }
}
