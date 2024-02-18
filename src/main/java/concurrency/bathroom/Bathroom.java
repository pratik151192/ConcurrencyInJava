package concurrency.bathroom;

import net.jcip.annotations.GuardedBy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * See README.md for problem statement
 */
public class Bathroom {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition menAbsent = lock.newCondition();

    private final Condition womenAbsent = lock.newCondition();

    @GuardedBy("lock")
    private int menCount = 0;

    @GuardedBy("lock")
    private int womenCount = 0;

    @GuardedBy("lock")
    private final Set<Person> occupiedToilet = new HashSet<>();

    private final int limit = 3;

    static class Person {
        private final String name;
        private final PersonType personType;

        public Person(String name, PersonType personType) {
            this.name = name;
            this.personType = personType;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return Objects.equals(name, person.name) && personType == person.personType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, personType);
        }
    }

    enum PersonType {
        MAN,
        WOMAN
    }

    public void enter(final Person p) throws Exception {
        lock.lock();
        try {
            // condition if the toilet is full. we wait for the notFull signal
            while (occupiedToilet.size() == limit) {
                notFull.await();
            }

            // condition if the type of person is different than the one in toilet. we
            // wait for the differentTypeAbsent signal
            switch (p.personType) {
                case MAN:
                    // if there are women present, lets wait for them to be out
                    if (womenCount > 0) {
                        womenAbsent.await();
                    }
                    menCount++;
                    break;
                case WOMAN:
                    // if there are men present, lets wait for them to be out
                    if (menCount > 0) {
                        menAbsent.await();
                    }
                    womenCount++;
                    break;
            }


            System.out.println(p.name + " entered of type " + p.personType);
            occupiedToilet.add(p);
        } finally {
            lock.unlock();
        }
    }

    public void leave(final Person p) {
        lock.lock();
        try {
            boolean success = occupiedToilet.remove(p);
            if (success) {
                notFull.signal();
                switch (p.personType) {
                    case MAN:
                        menCount--;
                        if (menCount == 0) {
                            menAbsent.signal();
                        }
                        break;
                    case WOMAN:
                        womenCount--;
                        if (womenCount == 0) {
                            womenAbsent.signal();
                        }
                }
            }
            System.out.println(p.name + " left of type " + p.personType);
        } finally {
            lock.unlock();
        }

    }
}
