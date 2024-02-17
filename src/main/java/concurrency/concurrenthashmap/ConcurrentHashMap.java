package concurrency.concurrenthashmap;

import concurrency.concurrenthashmap.model.ConcurrentBucket;
import concurrency.concurrenthashmap.model.Item;

/**
 * Implement a concurrent hash map
 */
public class ConcurrentHashMap {

    // use prime number for the number of buckets

    // segment my hashmap into 13 buckets; create 16 locks for 16 buckets

    // 1. implement a simple hash map
    // 2. make it concurrency safe
//           - will be a mutex, can make it
    // 3. create a read-write locking scheme: active readers, active writers, and number of waiting writers

    private static final int NUM_BUCKETS = 2069;

    private final ConcurrentBucket[] buckets = new ConcurrentBucket[NUM_BUCKETS];

    public ConcurrentHashMap() {
        for (int i = 0; i < NUM_BUCKETS; i++) {
            buckets[i] = new ConcurrentBucket(i);
        }
    }

    public Item getItem(final String key) {
        return buckets[getBucketId(key)].getItem(key);
    }

    public void putItem(final Item item) {
        this.buckets[getBucketId(item.getKey())].addItem(item);
    }

    private int hash(final String key) {
        return key.hashCode();
    }

    private int getBucketId(final String key) {
        final int hash = hash(key);
        return Math.floorMod(hash, NUM_BUCKETS);
    }

}
