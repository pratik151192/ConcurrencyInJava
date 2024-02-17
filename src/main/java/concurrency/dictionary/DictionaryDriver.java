package concurrency.dictionary;

public class DictionaryDriver {

    public static void main(String... args) {
        final Dictionary dictionary = new Dictionary(100);

        Thread t = new Thread(() -> {
            dictionary.upsertDictionary("apple", "its a fruit");
        });
    }
}
