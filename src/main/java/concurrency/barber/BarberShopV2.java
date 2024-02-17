package concurrency.barber;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * V1 with single concurrency.barber and multiple customers
 */
public class BarberShopV2 {

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

    private int numChairs;

    private final LinkedBlockingQueue<Customer> customers = new LinkedBlockingQueue<>();
    private final Semaphore freeBarbersSemaphore = new Semaphore(0);
    private final ReentrantLock shopsLock = new ReentrantLock();

    public BarberShopV2(int numChairs) {
        this.numChairs = numChairs;
    }

    public void addBarber(int barberId) throws Exception {
        Barber barber = new Barber(barberId);
        freeBarbersSemaphore.release();
        addChair();
        barber.serve();
    }

    private void addChair() {
        shopsLock.lock();
        try {
            numChairs++;
        } finally {
            shopsLock.unlock();
        }
    }

    public class Barber {
        // no need to track concurrency.barber's chair
        private final int id;

        public Barber(int id) {
            this.id = id;
        }

        public void serve() throws Exception {
            while (true) {
                final Customer customer = customers.take();
                serveCustomer(customer, this);
                freeBarbersSemaphore.release();
            }
        }
    }

    private void serveCustomer(final Customer customer, final Barber barber) throws Exception {
        System.out.println("Serving customer " + customer.id + " through concurrency.barber " +  barber.id);
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

        freeBarbersSemaphore.acquire();

        shopsLock.lock();
        try {
            customers.add(customer);
            numChairs++;
        } finally {
            shopsLock.unlock();
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

    // trim my beard
}
