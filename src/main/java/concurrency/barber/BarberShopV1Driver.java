package concurrency.barber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BarberShopV1Driver {

    public static void main(String... args) throws Exception {

        BarberShopV1 barberShopV1 = new BarberShopV1(2);

        Thread barber = new Thread(() -> {
            try {
                barberShopV1.barbers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AtomicInteger customersServed = new AtomicInteger(0);
        List<Thread> customers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final int custId = i;
            Thread custThread = new Thread(() -> {
                try {
                    barberShopV1.cutMyHair(new BarberShopV1.Customer(custId));
                    customersServed.getAndIncrement();
                } catch (Exception e) {
                    System.err.println("Shops is full. Customer " + custId + " exited");
                }
            });

            customers.add(custThread);

        }

        barber.start();
        customers.parallelStream().forEach(c -> {
//                    try {
//                        Thread.sleep(ThreadLocalRandom.current().nextInt(0, 200));
//                    } catch (Exception e) {
//
//                    }
                    c.start();
                });

        customers.forEach(t -> {
                    try {
                        t.join();
                    } catch (Exception e) {

                    }
                });

        System.out.println("Total customers served " +  customersServed);
    }
}
