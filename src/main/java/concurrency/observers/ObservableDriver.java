package concurrency.observers;

import java.util.HashSet;
import java.util.Set;

public class ObservableDriver {

    public static void main(String... args) {
        final Set<Integer> set = new HashSet<>();
        final SetObserver<Integer> setObserver = new DummySetObserver();
        final ObservableSet<Integer> observableSet = new ObservableSet<>(set);

        observableSet.addObserver(setObserver);

        for (int i = 0; i < 100; i++) {
            observableSet.add(i);
        }
    }
}
