package annotation.gubeeexerciseproxy.repository;

import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;
import annotation.gubeeexercisewithoutproxy.DatabaseErrorException;

import java.util.Random;

public class Repository implements RepositoryInterface {

    public void updateSomething() {
        Random random = new Random();

        int randomNumber = random.nextInt();

        System.out.println("Estou executando uma operação de update no Banco de dados");

        // Simulando erros de forma aleatoria
        if (randomNumber % 2 == 0) {
            throw new DatabaseErrorException("Um erro ocorreu durante a execução da operação de Update!");
        }

    }

    @Override
    public void createSomething() {
        Random random = new Random();

        int randomNumber = random.nextInt();

        System.out.println("Estou executando uma operação create no Banco de dados");

        // Simulando erros de forma aleatoria
        if (randomNumber % 2 == 0) {
            throw new DatabaseErrorException("Um erro ocorreu durante a execução da operação Create!");
        }
    }


}
