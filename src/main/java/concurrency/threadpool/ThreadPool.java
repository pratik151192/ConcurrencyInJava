package concurrency.threadpool;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * A queue with a bunch of worker threads
 */
@ThreadSafe
public class ThreadPool {

    // linkedBlockingQueue's take() method blocks until work is available. Note that poll() doesn't
    // have this wait semantics. It's fair game in an interview setting to be asked to implement this yourself.
    // Look at Yanis's Multi Producer Consumer queue problem for the same.
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private final int numThreads;
    private final Worker[] workers;

    public ThreadPool(int threads) throws Exception {
        this.numThreads = threads;
        this.workers = new Worker[numThreads];

        // initialize all the threads
        init();
    }

    class Worker extends Thread {
        // only the ThreadPool is managing individual threads and their shutdown flag. Hence, using
        // volatile here suffices for the purposes of visibility. This has been reviewed by an IK instructor.
        volatile boolean shutdownSignalled = false;

        @Override
        public void run() {

            // we keep trying to take work until we are asked to shutdown
            while (!shutdownSignalled) {
                Runnable r = null;
                try {
                    // block until work is submitted/available
                    r = queue.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                r.run();
            }
        }

        public void setShutdownSignalled(boolean signal) {
            shutdownSignalled = signal;
        }
    }

    private void init() throws Exception {
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Worker();
        }

        for (int i = 0; i < numThreads; i++) {
            workers[i].start();
        }
    }

    /**
     * Straightforward public contract to submit work to the ThreadPool
     */
    public void addWork(final Runnable r) {
        if (r != null) queue.offer(r);
    }

    /**
     * Straightforward public contract to shutdown the ThreadPool
     * The shutdown is synchronous, i.e, the thread pool exits AFTER all already submitted work is complete.
     */
    public void quit() throws Exception {
        for (int i = 0; i < numThreads; i++) {
           workers[i].setShutdownSignalled(true);
        }

        // removing the join will make the shutdown asynchronous
        for (int i = 0; i < numThreads; i++) {
            workers[i].join();
        }
    }
}
