package concurrency;

public class DistributedTransactions {

    /**
     *      Distributed Transactions:
     *          - Concurrency control
     *          - Atomic commit
     *
     *      Transaction 1 | Transaction 2
     *
     *        set(x++)    |    read(x)
     *        set(y--)    |    read(y)
     *
     *      Serializability
     *
     *      A
     *      C
     *      I
     *      D
     *
     *      Two - phase locking
     *
     *      Two - phase commit
     *          - Transaction Coordinator
     *
     *
     *
     */
}
