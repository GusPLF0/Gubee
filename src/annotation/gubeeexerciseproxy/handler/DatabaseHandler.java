package annotation.gubeeexerciseproxy.handler;

import annotation.Transaction;
import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DatabaseHandler implements InvocationHandler {
    private final RepositoryInterface repositoryInterface;

    public DatabaseHandler(RepositoryInterface repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(Transaction.class)) {

            try {
                System.out.println("Iniciando execução do método " + method.getName());
                Object result = method.invoke(repositoryInterface, args);
                System.out.println("Finalizando a execução do método " + method.getName() + " com sucesso!");
                return result;
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println("Finalizando a execução do método " + method.getName() + " com erro!");
                throw e.getCause();
            }


        }else {
            return method.invoke(repositoryInterface, args);
        }
    }


}
