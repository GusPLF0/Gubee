package annotation.gubeeexerciseproxy;

import annotation.gubeeexerciseproxy.handler.DatabaseHandler;
import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;
import annotation.gubeeexerciseproxy.repository.Repository;

import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        RepositoryInterface realRepository = new Repository();

        RepositoryInterface proxyRepository = (RepositoryInterface) Proxy.newProxyInstance(
                realRepository.getClass().getClassLoader(), // Loader da classe para o proxy
                new Class<?>[]{RepositoryInterface.class},  // Interfaces para o proxy
                new DatabaseHandler(realRepository) // Handler para o proxy
        );


        System.out.println("====================================================");

        proxyRepository.updateSomething();

        System.out.println("====================================================");

        proxyRepository.createSomething();
    }
}
