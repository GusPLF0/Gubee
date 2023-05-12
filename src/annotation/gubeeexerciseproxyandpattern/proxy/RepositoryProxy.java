package annotation.gubeeexerciseproxyandpattern.proxy;

import annotation.Transaction;
import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;
import annotation.gubeeexerciseproxyandpattern.repository.RepositoryFake;
import annotation.gubeeexercisewithoutproxy.DatabaseErrorException;

import java.lang.reflect.Method;

public class RepositoryProxy implements RepositoryInterface {
    private final RepositoryInterface repository;

    public RepositoryProxy() {
        this.repository = new RepositoryFake();
    }

    @Override
    public void updateSomething() {
        Class<? extends RepositoryInterface> myClass = repository.getClass();

        Method method = null;

        try {
            method = myClass.getMethod("updateSomething");
        } catch (NoSuchMethodException e) {
            System.out.println("Método não existe");
            throw new RuntimeException(e);
        }

        if (method.isAnnotationPresent(Transaction.class)) {
            try {
                System.out.println("Iniciando e execução do método " + method.getName());
                repository.updateSomething();
                System.out.println("Finalizando a execução do método " + method.getName() + " com sucesso!");
            } catch (Exception e) {
                System.out.println("Finalizando a execução do método " + method.getName() + " com erro!");
                throw new DatabaseErrorException("Erro ao fazer operação no banco");
            }

        } else {
            repository.updateSomething();
        }
    }

    @Override
    public void createSomething() {

    }
}
