package concurrency.concurrenthashmap.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentBucket {
    private final int bucketId;

    // do this on your own
    // get(), put(), resize(loadFactor),
    private final Map<String, Item> items = new HashMap<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ConcurrentBucket(int bucketId) {
        this.bucketId = bucketId;
    }

    public void addItem(final Item item) {
        readWriteLock.writeLock().lock();
        try {
            this.items.put(item.getKey(), item);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public Item getItem(final String key) {
        readWriteLock.readLock().lock();
        try {
            return this.items.get(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}