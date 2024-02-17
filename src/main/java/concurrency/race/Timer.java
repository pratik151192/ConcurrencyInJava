package concurrency.race;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class Timer {

    private final int workers;
    private final CountDownLatch ready;
    private final CountDownLatch go;
    private final CountDownLatch done;

    public Timer(final int workers) {
        this.workers = workers;
        this.ready = new CountDownLatch(workers);
        this.go = new CountDownLatch(1);
        this.done = new CountDownLatch(workers);
    }

    public long timeNanos(final ExecutorService service, final Runnable action) {

        List<String> list = new ArrayList<>();
        list.add("apple");
        list.add("banana");

        list.replaceAll(String::toUpperCase);


        for (int i = 0; i < workers; i++) {

            service.execute(() -> {
               ready.countDown();
               try {
                   go.await();
                   action.run();
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
               } finally {
                   done.countDown();
               }
            });

        }

        long timetaken = -1;
        try {
            // wait for all threads to REACH starting state (READY_TO_RUN)
            ready.await();

            // start the timer
            long time = System.nanoTime();

            // signal all threads waiting that they should start running (RUNNING)
            go.countDown();

            // wait for all of them to complete (DONE)
            done.await();
            timetaken = System.nanoTime() - time;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return timetaken;
    }
}
