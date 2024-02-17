package concurrency.mcp;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class MultiProducerConsumerQueue {

    @GuardedBy("reentrantLock")
    private final Queue<Integer> queue = new LinkedList<>();

    private final Lock reentrantLock = new ReentrantLock();
    private final Condition notFull = reentrantLock.newCondition();
    private final Condition notEmpty = reentrantLock.newCondition();

    private final int limit;

    public MultiProducerConsumerQueue(int limit) {
        this.limit = limit;
    }

    public void push(int val) {
        reentrantLock.lock();
        try {
            while (queue.size() == limit) {
                System.out.println("Now waiting");
                notFull.awaitUninterruptibly();
            }
            notEmpty.signal();
            System.out.println("Now pushed");
            queue.offer(val);
        } finally {
            reentrantLock.unlock();
        }
    }

    public int pop() {
        reentrantLock.lock(); // required for exclusive access of our share data structure, i.e., the queue
        try {
            while (queue.size() == 0) {
                // I auto release the lock so that someone else can mutate the data structure by reacquiring the lock,
                // and signal me that the queue is no longer empty. After we return from this method, we re-acquire
                // the lock again and is guaranteed to do so.
                notEmpty.awaitUninterruptibly();
            }
            notFull.signal();
            return queue.poll();
        } finally {
            reentrantLock.unlock();
        }
    }
}

