package concurrency.barber;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * V1 with single concurrency.barber and multiple customers
 */
public class BarberShopCV {

    private final Barber barber = new Barber(1);

    private int numChairs;

    private final LinkedBlockingQueue<Customer> customers = new LinkedBlockingQueue<>();
    private final ReentrantLock shopsLock = new ReentrantLock();

    public BarberShopCV(int numChairs) {
        this.numChairs = numChairs;
    }

    private class Barber {
        // no need to track concurrency.barber's chair
        private final int id;
        private final ReentrantLock barberLock = new ReentrantLock();
        private final Condition free = barberLock.newCondition();

        public Barber(int id) {
            this.id = id;
        }

        public void serve() throws Exception {
            while (true) {
//                barberLock.lock();
////                free.await();
                final Customer customer = customers.take();
                serveCustomer(customer);
//                free.signal();
//                barberLock.unlock();
            }
        }
    }

    private void serveCustomer(final Customer customer) throws Exception {
        System.out.println("Serving customer " + customer.id);
        Thread.sleep(100);
    }

    public static class Customer {
        private final int id;

        public Customer(int id) {
            this.id = id;
        }
    }

    public void cutMyHair(Customer customer) throws Exception {
        if (isShopFull()) {
            throw new RuntimeException("All the barbers are busy. Please come again later.");
        }

        try {
            barber.barberLock.lock();
            shopsLock.lock();
            System.out.println("Submitting to concurrency.barber " + customer.id);
            customers.add(customer);
            numChairs++;
        } finally {
            shopsLock.unlock();
            barber.barberLock.unlock();
        }
    }

    private boolean isShopFull() {
        shopsLock.lock();
        try {
            if (numChairs == 0) {
                return true;
            }
            numChairs--;
        } finally {
            shopsLock.unlock();
        }
        return false;
    }

    public void barbers() throws Exception {
        barber.serve();
    }

    private void dummy() {
        System.out.println("");
    }
    // trim my beard
}
