package annotation.gubeeexercise;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        RepositorioFake repositorioFake = new RepositorioFake();

        doingAnnotationFunction(repositorioFake);
    }

    private static void doingAnnotationFunction(Object object) {

        if (Objects.isNull(object)) {
            throw new DatabaseErrorException("Object is Null");
        }

        Class<?> myClass = object.getClass();

        for (Method method : myClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Transaction.class)) {
                method.setAccessible(true);
                System.out.printf("Iniciando e xecução do método %s \n", method.getName());
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    System.out.println("Finalizando execução do método com erro!!");
                    throw new DatabaseErrorException("Erro durante a execução de uma operação no banco");
                }

                System.out.println("Finalizando execução do método com sucesso");
            }
        }
    }
}
