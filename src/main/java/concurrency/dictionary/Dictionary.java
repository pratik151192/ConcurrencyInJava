package concurrency.dictionary;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public class Dictionary {

    @GuardedBy("readWriteLock")
    private final Map<String, String> dictionary = new HashMap<>();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    /**
     * Controls the number of readers to the dictionary
     */
    private final Semaphore readerPermits;

    public Dictionary(int readers) {
        this.readerPermits = new Semaphore(readers);
    }

    public void upsertDictionary(final String word, final String definition) {
        readWriteLock.writeLock().lock();
        try {
            dictionary.put(word, definition);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public String getDefinition(final String word) {
        readerPermits.acquireUninterruptibly();
        readWriteLock.readLock().lock();
        try {
            return dictionary.get(word);
        } finally {
            readWriteLock.readLock().unlock();
            readerPermits.release();
        }
    }
}
