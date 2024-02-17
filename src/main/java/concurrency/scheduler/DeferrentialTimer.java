package concurrency.scheduler;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class DeferrentialTimer {

    private final ExecutorService executorService;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition work = lock.newCondition();

    @GuardedBy("lock")
    private final HashMap<String, ScheduledTask> map = new HashMap<>();

    @GuardedBy("lock")
    private final Queue<ScheduledTask> priorityQueue;

    private final int limit;

    public DeferrentialTimer(final int limit) {
        this.priorityQueue = new PriorityQueue<>(Comparator.comparingLong(o -> o.absoluteRunTime));
        this.limit = limit;
        this.executorService = Executors.newFixedThreadPool(limit);
        timer();
    }

    public String schedule(final long delayMillis, final Runnable task) {
        final String taskId = UUID.randomUUID().toString();
        final ScheduledTask scheduledTask = new ScheduledTask(taskId, task, System.currentTimeMillis() + delayMillis);

        lock.lock();
        try {
            priorityQueue.offer(scheduledTask);
            map.put(taskId, scheduledTask);
            work.signal();
        } finally {
            lock.unlock();
        }

        return taskId;
    }

    private void timer() {
        final Thread start =  new Thread(() ->{
            while (true) {
                ScheduledTask t  = null;
                lock.lock();
                try {
                    long time = System.currentTimeMillis();
                    while (priorityQueue.isEmpty() || priorityQueue.peek().absoluteRunTime > time) {

                        try {
                            if (priorityQueue.isEmpty()) {
                                work.await();
                            } else if (priorityQueue.peek().absoluteRunTime > time) {
                                // we don't care if the result is false or true for this await as we check the condition
                                // again before proceeding
                                work.await(priorityQueue.peek().absoluteRunTime - time, TimeUnit.MILLISECONDS);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        time = System.currentTimeMillis();
                    }

                    t = priorityQueue.poll();
                    map.remove(t.taskId);
                } finally {
                    lock.unlock();
                }

                executorService.submit(t.task);
            }
        });

        start.start();
        
    }

    public void cancel(final String taskId) {
        lock.lock();
        try {
             final ScheduledTask t = map.get(taskId);
             if (t == null) throw new IllegalArgumentException();
             map.remove(t.taskId);
             priorityQueue.remove(t); //
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        try {
            executorService.shutdown();
        } finally {
            executorService.shutdownNow();
        }
    }

    static class ScheduledTask {
        final String taskId;
        final Runnable task;
        final long absoluteRunTime;

        public ScheduledTask(final String taskId, final Runnable r, final long absoluteRunTime) {
            this.taskId = taskId;
            this.task = r;
            this.absoluteRunTime = absoluteRunTime;
        }
    }
}
