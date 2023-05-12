package annotation.gubeeexerciseproxyandpattern;

import annotation.gubeeexerciseproxyandpattern.factory.ProxyRepositoryFactory;
import annotation.gubeeexerciseproxyandpattern.factory.RealRepositoryFactory;

public class Main {
    public static void main(String[] args) {
        RepositoryFactoryApplication repositoryFactoryApplication = new RepositoryFactoryApplication(new ProxyRepositoryFactory());

        RepositoryFactoryApplication repositoryFactoryApplication2 = new RepositoryFactoryApplication(new RealRepositoryFactory());


        repositoryFactoryApplication2.updateSomething(); // Repositorio Real!
        System.out.println("============");
        repositoryFactoryApplication.updateSomething(); // Proxy




    }
}
