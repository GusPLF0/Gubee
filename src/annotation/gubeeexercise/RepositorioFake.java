package annotation.gubeeexercise;

import java.util.Random;

public class RepositorioFake {

    @Transaction
    private void updateSomething() throws InterruptedException {
        Random random = new Random();

        int randomNumber = random.nextInt();

        System.out.println("Estou executando uma operação no Banco de dados");

        if (randomNumber % 2 == 0) {
            throw new DatabaseErrorException("Um erro ocorreu durante a execução da operação!");
        }

    }


}
