package annotation.gubeeexerciseproxyandpattern.factory;

import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;
import annotation.gubeeexerciseproxyandpattern.proxy.RepositoryProxy;

public class ProxyRepositoryFactory implements RepositoryFactory {
    @Override
    public RepositoryInterface createRepository() {
        return new RepositoryProxy();
    }
}
