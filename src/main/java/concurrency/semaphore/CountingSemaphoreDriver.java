package concurrency.semaphore;

public class CountingSemaphoreDriver {

    public static void main(String... args) throws Exception {
        final CountingSemaphore semaphore = new CountingSemaphore(1);

        Thread first = new Thread(() -> {
            try {
                semaphore.acquire();
                Thread.sleep(2000);
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread second = new Thread(() -> {
            try {
                semaphore.acquire();
                Thread.sleep(2000);
                semaphore.release();
                semaphore.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        Thread third = new Thread(() -> {
            try {
                Thread.sleep(4000);
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        first.start();
        second.start();
        third.start();
        first.join();
        second.join();
        third.join();

    }
}
