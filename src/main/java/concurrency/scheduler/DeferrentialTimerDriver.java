package concurrency.scheduler;

public class DeferrentialTimerDriver {

    public static void main(String... args) {
        final DeferrentialTimer timer = new DeferrentialTimer(10);
        timer.schedule(4000, new Runnable() {
            @Override
            public void run() {
                System.out.println("print");
            }
        });
    }
}
