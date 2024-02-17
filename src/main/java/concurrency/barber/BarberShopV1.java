package concurrency.barber;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * V1 with single concurrency.barber and multiple customers
 */
public class BarberShopV1 {

    // concurrency.barber shop

    // 1 concurrency.barber chair (for the customer) and a concurrency.barber
    // waiting room with n number of chairs

    // a customer shows up
    // - if concurrency.barber chair and all chairs are occupied, customer leaves
    // - if a chair is available to sit or wait, then the customer will wait
    // - if a concurrency.barber doesn't have anyone to service, he/she goes to sleep
    // - if a concurrency.barber is asleep and customer walks in, they'd wake up the concurrency.barber

    // addCustomerToWaitList(Customer c) -> concurrency.barber shop/receptionist
    // serveCustomer(Customer c) -> concurrency.barber

    private final Barber barber = new Barber(1);

    private int numChairs;

    private final LinkedBlockingQueue<Customer> customers = new LinkedBlockingQueue<>();
    private final ReentrantLock shopsLock = new ReentrantLock();

    public BarberShopV1(int numChairs) {
        this.numChairs = numChairs;
    }

    private class Barber {
        // no need to track concurrency.barber's chair
        private final int id;
        // am i free or not
        private final Semaphore free = new Semaphore(1);
        private final Semaphore sleeping = new Semaphore(0);

        public Barber(int id) {
            this.id = id;
        }

        public void serve() throws Exception {
            while (true) {

                final Customer customer = customers.take();
                serveCustomer(customer);

                free.release();
                sleeping.acquire();
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
        if (!tryGrabAChair()) {
            throw new RuntimeException("All the barbers are busy. Please come again later.");
        }

        // this is a concern; customers should be decoupled from the concurrency.barber
        barber.sleeping.release();
        barber.free.acquire();

        shopsLock.lock();
        try {
            System.out.println("Submitting to concurrency.barber " + customer.id);
            customers.add(customer);
            numChairs++;
        } finally {
            shopsLock.unlock();
        }
    }

    private boolean tryGrabAChair() {
        shopsLock.lock();
        try {
            if (numChairs == 0) {
                return false;
            }
            numChairs--;
        } finally {
            shopsLock.unlock();
        }
        return true;
    }

    public void barbers() throws Exception {
        barber.serve();
    }

    private void dummy() {
        System.out.println("");
    }
    // trim my beard
}
