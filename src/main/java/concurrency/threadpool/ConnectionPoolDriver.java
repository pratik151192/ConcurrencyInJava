package concurrency.threadpool;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolDriver {

    public static void main(String... args) throws Exception {
        final ConnectionPool connectionPool = new ConnectionPool(10);

        final List<Object> connections = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                Object ob = null;
                try {
                    ob = connectionPool.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connections.add(ob);
            });
            t.start();
        }

        Thread.sleep(5000);
        connectionPool.ret(connections.get(0));
        connectionPool.ret(connections.get(1));
        connectionPool.ret(connections.get(2));
    }
}
