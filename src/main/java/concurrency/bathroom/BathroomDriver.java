package concurrency.bathroom;

public class BathroomDriver {

    public static void main(String... args) throws InterruptedException {
        final Bathroom bathroom = new Bathroom();

        System.out.println("---Testing different type people don't enter. Bob should wait for 5 seconds while Alice " +
                "is using the restroom.---\n\n");
        // Creating threads to simulate people entering and leaving the bathroom
        Thread person1 = new Thread(() -> {
            try {
                bathroom.enter(new Bathroom.Person("Alice", Bathroom.PersonType.WOMAN));
                Thread.sleep(5000); // Simulating the time spent in the bathroom
                bathroom.leave(new Bathroom.Person("Alice", Bathroom.PersonType.WOMAN));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread person2 = new Thread(() -> {
            try {
                bathroom.enter(new Bathroom.Person("Bob", Bathroom.PersonType.MAN));
                Thread.sleep(1000);
                bathroom.leave(new Bathroom.Person("Bob", Bathroom.PersonType.MAN));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread person3 = new Thread(() -> {
            try {
                bathroom.enter(new Bathroom.Person("Charlie", Bathroom.PersonType.WOMAN));
                Thread.sleep(1000);
                bathroom.leave(new Bathroom.Person("Charlie", Bathroom.PersonType.WOMAN));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Starting the threads
        person1.start();
        person2.start();
        person3.start();

        // Waiting for threads to finish
        person1.join();
        person2.join();
        person3.join();
    }
}
