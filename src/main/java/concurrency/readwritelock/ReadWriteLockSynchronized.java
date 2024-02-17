package concurrency.readwritelock;

public class ReadWriteLockSynchronized {

    private boolean isWriting = false; // simply maintain counts of writers as well
    private int readers = 0;

    // track pending writers to avoid starvation

    public synchronized void acquireReadLock() throws InterruptedException {
        while (isWriting) {
            wait();
        }
        readers++;
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        while (isWriting || readers != 0) {
            wait();
        }
        isWriting = true;
    }

    public synchronized void releaseReadLock() {
        readers--;
        notifyAll();
    }

    public synchronized void releaseWriteLock() {
        isWriting = false;
        notifyAll();
    }
}
