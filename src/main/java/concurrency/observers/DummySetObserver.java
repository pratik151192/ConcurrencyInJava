package concurrency.observers;

public class DummySetObserver implements SetObserver<Integer> {

    @Override
    public void added(ObservableSet<Integer> set, Integer element) {
        System.out.println(element);
    }
}
