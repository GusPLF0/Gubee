package annotation.gubeeexerciseproxyandpattern;

import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;
import annotation.gubeeexerciseproxyandpattern.factory.RepositoryFactory;

public class RepositoryFactoryApplication {
    private RepositoryInterface repository;

    public RepositoryFactoryApplication(RepositoryFactory factory) {
        this.repository = factory.createRepository();
    }

    public void updateSomething() {
        repository.updateSomething();
    }
}
