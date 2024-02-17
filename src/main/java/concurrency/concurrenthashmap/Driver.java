package concurrency.concurrenthashmap;

import concurrency.concurrenthashmap.model.Item;

import java.util.stream.IntStream;

public class Driver {

    final static int NUM_ELEMENTS = 1_000_000;

    public static void main(String... args) throws Exception {
        final ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        final HashMap hashMap = new HashMap();

        System.out.println("------HashMap Perf-------\n");
        Thread hashMapThread = new Thread(() -> {
            long time = System.nanoTime();
            IntStream.range(0, NUM_ELEMENTS).parallel().forEach(e -> {
                hashMap.putItem(new Item(String.valueOf(e), new Object()));
            });
            System.out.println("Total time to persist a million items " + (System.nanoTime() - time) + " nanos");
            IntStream.range(0, NUM_ELEMENTS).parallel().forEach(e -> {
                hashMap.getItem(String.valueOf(e));
            });
            System.out.println("Total round trip time to persist amd get a million items " + (System.nanoTime() - time) + " nanos\n");
        });

        hashMapThread.start();
        hashMapThread.join();

        System.out.println("------Concurrent HashMap Perf-------\n\n");
        Thread concurrentHashMapThread = new Thread(() -> {
            long time = System.nanoTime();
            IntStream.range(0, NUM_ELEMENTS).parallel().forEach(e -> {
                concurrentHashMap.putItem(new Item(String.valueOf(e), new Object()));
            });
            System.out.println("Total time to persist a million items " + (System.nanoTime() - time) / 1000 + " nanos");
            IntStream.range(0, NUM_ELEMENTS).parallel().forEach(e -> {
                concurrentHashMap.getItem(String.valueOf(e));
            });
            System.out.println("Total round trip time to persist amd get a million items " + (System.nanoTime() - time) / 1000 + " nanos\n");
        });

        concurrentHashMapThread.start();
        concurrentHashMapThread.join();


        System.out.println("------HashMap Same Key Perf-------\n\n");
        hashMapThread = new Thread(() -> {
            long time = System.nanoTime();
            IntStream.range(0, NUM_ELEMENTS).parallel().forEach(e -> {
                hashMap.getItem(String.valueOf(0)).getValue();
            });
            System.out.println("Total time to fetch the same key " + (System.nanoTime() - time) + " nanos\n");
        });

        hashMapThread.start();
        hashMapThread.join();

        System.out.println("------ConcurrentHashMap Same Key Perf-------\n\n");
        concurrentHashMapThread = new Thread(() -> {
            long time = System.nanoTime();
            IntStream.range(0, NUM_ELEMENTS).parallel().forEach(e -> {
                concurrentHashMap.getItem(String.valueOf(0)).getValue();
            });
            System.out.println("Total time to fetch the same key " + (System.nanoTime() - time) + " nanos\n");
        });

        concurrentHashMapThread.start();
        concurrentHashMapThread.join();

    }
}
