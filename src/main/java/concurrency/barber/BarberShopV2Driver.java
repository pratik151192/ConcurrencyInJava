package concurrency.barber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BarberShopV2Driver {

    public static void main(String... args) throws Exception {

        BarberShopV2 barberShopV1 = new BarberShopV2(5);

        for (int i = 0; i < 1; i++) {
            int barberId = i;
            Thread barber = new Thread(() -> {
                try {
                    barberShopV1.addBarber(barberId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            barber.start();
        }

        AtomicInteger customersServed = new AtomicInteger(0);
        List<Thread> customers = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            final int custId = i;
            Thread custThread = new Thread(() -> {
                try {
                    barberShopV1.cutMyHair(new BarberShopV2.Customer(custId));
                    customersServed.getAndIncrement();
                } catch (Exception e) {
                    System.err.println(e.getMessage() + " : " + custId + " exited");
                }
            });

            customers.add(custThread);

        }

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
