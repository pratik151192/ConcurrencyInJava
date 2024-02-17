package concurrency.concurrenthashmap.model;

public class Item {
    private final String key;
    private final Object value;

    public Item(final String key,
                 final Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}