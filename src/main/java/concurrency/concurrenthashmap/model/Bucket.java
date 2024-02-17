package concurrency.concurrenthashmap.model;

import java.util.HashMap;
import java.util.Map;

public class Bucket {
    private final int bucketId;
    private final Map<String, Item> items = new HashMap<>();

    public Bucket(int bucketId) {
        this.bucketId = bucketId;
    }

    public void addItem(final Item item) {
        this.items.put(item.getKey(), item);
    }

    public Item getItem(final String key) {
        return this.items.get(key);
    }
}