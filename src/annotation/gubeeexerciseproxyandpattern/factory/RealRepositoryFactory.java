package annotation.gubeeexerciseproxyandpattern.factory;

import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;
import annotation.gubeeexerciseproxyandpattern.repository.RepositoryFake;

public class RealRepositoryFactory implements RepositoryFactory {
    @Override
    public RepositoryInterface createRepository() {
        return new RepositoryFake();
    }
}
