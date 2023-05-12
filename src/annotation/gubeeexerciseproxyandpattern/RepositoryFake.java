package annotation.gubeeexerciseproxyandpattern;

import annotation.gubeeexerciseproxy.interfaces.RepositoryInterface;
import annotation.gubeeexercisewithoutproxy.DatabaseErrorException;

import java.util.Random;

public class RepositoryFake {

    public void updateSomething() {
        Random random = new Random();

        int randomNumber = random.nextInt();

        System.out.println("Estou executando uma operação no Banco de dados");

        if (randomNumber % 2 == 0) {
            throw new DatabaseErrorException("Um erro ocorreu durante a execução da operação!");
        }

    }


}
