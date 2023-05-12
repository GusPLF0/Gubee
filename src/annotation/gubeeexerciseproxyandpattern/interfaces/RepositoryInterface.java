package annotation.gubeeexerciseproxyandpattern.interfaces;

import annotation.Transaction;

public interface RepositoryInterface {
    @Transaction
    void updateSomething();

    @Transaction
    void createSomething();
}