package concurrency.boundedset;

import net.jcip.annotations.GuardedBy;

import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedSetCV {

    @GuardedBy("lock")
    private final HashSet<Integer> set;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();

    int limit;

    public BoundedSetCV(int limit) {
        this.set = new HashSet<>();
        this.limit = limit;
    }

    void put(int v) throws Exception {
       lock.lock();
       try {
           while (set.size() == limit) {
               notFull.await();
           }
           set.add(v);
       } finally {
           lock.unlock();
       }
    }

    void erase(int v) throws Exception {
      lock.lock();
      try {
          boolean success = set.remove(v);
          if (success) {
              notFull.signal();
          }
      } finally {
          lock.unlock();
      }
    }
}
