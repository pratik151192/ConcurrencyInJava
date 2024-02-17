package concurrency.threadpool;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A connection pool that allows us to create a certain number of connections and then re-use released ones.
 * The requester of the connection blocks until someone else has released it.
 */
@ThreadSafe
public class ConnectionPool {

    /**
     * We have two states in this particular problem to maintain. Needless to say, both should be guarded
     * by appropriate synchronization means.
     *
     *      - freeList -> Keeps track of the connections that have been released by the clients. If a new connection
     *                    is requested and this list is not empty, we simply return that connection.
     *
     *      - created -> Keeps track of the total number of unique connections created. Note that if we have nothing
     *                   in the freeList, and our created == limit of the connection pool, then future requesters
     *                   will be blocked until a previous connection was released.
     */

    @GuardedBy("this")
    private final Queue<Object> freeList = new LinkedList<>();
    @GuardedBy("this")
    private int created = 0;

    private final int limit;

    public ConnectionPool(int limit) {
        this.limit = limit;
    }

    /**
     * Give me a 'new' connection. For the client, it's always a new connection even though we reuse.
     */
    public Object get() throws InterruptedException {
        Object obj = null;

        // can be achieved with a Reentrant lock as well; I am just posting solutions with difference approaches
        // to convey the various notions of mutex in Java.
        synchronized (this) {

            // we have two conditions and two states that we need to validate to solve this problem
            while (freeList.size() == 0 && created == limit) {
                wait();
            }

            if (freeList.size() != 0) {
                obj = freeList.poll();
            } else {
                created++;
            }

            System.out.println("Acquired " + created);
            notify();
        }

        // this is important; we do not create the object in our critical section as it's wasteful to do so there.
        // once we come here; we know we have been permitted to give a connection. If it is a re-used connection,
        // we simply return it as it was assigned before, or we create a new one here.
        return obj == null ? new Object() : obj;
    }

    /**
     * I want to return my connection back to you.
     * @param o the connection object
     */
    public void ret(Object o) {

        // note that if you replace this with a lock, both get() and ret() should use the same lock. "this" simply
        // takes the lock of the object on which the method was invoked.
        synchronized (this) {
            freeList.offer(o);

            // let the waiting thread know that we have a new element in freeList
            notify();
        }
    }
}
