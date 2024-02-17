package concurrency.observers;

public interface SetObserver<E> {

    void added(ObservableSet<E> set, E element);
}
