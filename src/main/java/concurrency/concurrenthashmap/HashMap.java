package concurrency.concurrenthashmap;

import concurrency.concurrenthashmap.model.Bucket;
import concurrency.concurrenthashmap.model.Item;

public class HashMap {

    private static final int NUM_BUCKETS = 2069;

    private final Bucket[] buckets = new Bucket[NUM_BUCKETS];

    public HashMap() {
        for (int i = 0; i < NUM_BUCKETS; i++) {
            buckets[i] = new Bucket(i);
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
