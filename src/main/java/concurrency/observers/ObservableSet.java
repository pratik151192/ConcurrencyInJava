package concurrency.observers;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@ThreadSafe
public class ObservableSet<E> extends HashSet<E> {

    @GuardedBy("this")
    private final List<SetObserver<E>> observers = new CopyOnWriteArrayList<>();

    public ObservableSet(final Set<E> set) {
        super(set);
    }

    public void addObserver(final SetObserver<E> setObserver) {
        synchronized (observers) {
            observers.add(setObserver);
        }
    }

    public void removeObserver(final SetObserver<E> setObserver) {
        synchronized (observers) {
            observers.remove(setObserver);
        }
    }

    private void notifyElementAdded(E element) {
        synchronized (observers) {
            observers.forEach(o -> o.added(this, element));
        }
    }

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (added) {
            notifyElementAdded(element);
        }
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element : c) {
            result |= add(element); // calls notifyElementAdded
        }
        return result;
    }


}
