package concurrency.oddeven;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Semaphore;

@ThreadSafe
public class OddEvenSemaphore {

    // note that we aren't guarding this with anything. Semaphore is not
    // really a lock so I didn't want to add that annotation here even
    // though we are ensuring exclusive access in this code.
    int count = 1;

    // odd semaphore is initialized with 1 as we want the first number in the series to be odd
    private final Semaphore oddSemaphore = new Semaphore(1);

    // another binary semaphore that will be toggled along with the oddSemaphore so that they take
    // turns to print a number
    private final Semaphore evenSemaphore = new Semaphore(0);

    private final int n;
    public OddEvenSemaphore(int n) {
        this.n = n;
    }

    public void odd() throws InterruptedException {

        // note that we want the (count < n) to also be in our critical section
        // and hence we take a while true loop here as otherwise it wasn't achievable directly
        // like the other approaches.
        while (true) {

            // wait for an odd permit to be available
            oddSemaphore.acquire();

            try {
                if (count <= n) {
                    System.out.println(count);
                    count++;
                } else {
                    return;
                }
            } finally {
                evenSemaphore.release();
            }
        }
    }

    public void even() throws InterruptedException {

        while (true) {

            // wait for an even permit to be available
            evenSemaphore.acquire();

            try {
                if (count <= n) {
                    System.out.println(count);
                    count++;
                } else {
                    return;
                }
            } finally {
                oddSemaphore.release();
            }
        }
    }

}
