package concurrency.readwritemap;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteMap {

    public final ReentrantReadWriteLock cacheReadWriteLock;

    private final AdjustableSemaphore readLimiter;

    public ReadWriteMap() {
        this.cacheReadWriteLock = new ReentrantReadWriteLock(true /* fair */);
        this.readLimiter = new AdjustableSemaphore(5);

    }

    public void acquireReadLock() {
        readLimiter.acquireUninterruptibly();
        cacheReadWriteLock.readLock().lock();
    }

    public void releaseReadLock() {
        readLimiter.release();
        cacheReadWriteLock.readLock().unlock();
    }

    public void acquireCacheBuildLock() {
        cacheReadWriteLock.writeLock().lock();
    }

    public void releaseCacheBuildLock() {
        cacheReadWriteLock.writeLock().unlock();
    }

    public void updateReaderLimit(int newReaderLimit) {
        readLimiter.adjustPermits(newReaderLimit);

    }

    @ThreadSafe
    private static class AdjustableSemaphore extends Semaphore {

        @GuardedBy("this")
        private int permits;

        public AdjustableSemaphore(int permits) {
            super(permits, true /* fair */);
            this.permits = permits;
        }

        /**
         * Adjusts the number of permits.
         *
         * If #newPermits is greater than #permits then the difference
         * will be added as new available permits.
         *
         * If #newPermits is less than #permits then the difference
         * will be subtracted from the available permits. This can
         * bring the number of available permits to negative.
         *
         * @param newPermits the new amount of permits.
         */
        private synchronized void adjustPermits(int newPermits) {
            int permitDelta = newPermits - permits;
            if (permitDelta > 0) {
                release(permitDelta);
            } else {
                reducePermits(-permitDelta);
            }
            permits = newPermits;
        }
    }
}
