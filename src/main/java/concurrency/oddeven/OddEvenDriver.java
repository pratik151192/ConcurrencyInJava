package concurrency.oddeven;

public class OddEvenDriver {

    public static void main(String... args) throws Exception {

        // change me to whichever synchronization technique you want to test for
        final OddEvenSemaphore oddEven = new OddEvenSemaphore(100);

        final Thread odd = new Thread(() -> {
            try {
                oddEven.odd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        final Thread even = new Thread(() -> {
            try {
                oddEven.even();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        even.start();
        odd.start();
        odd.join();
        even.join();
    }
}
