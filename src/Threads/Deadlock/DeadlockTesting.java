package Threads.Deadlock;

public class DeadlockTesting {
    public static void main(String[] args) {
        Object obj1  = new Object();
        Object obj2  = new Object();

        Runnable r1 =  () -> {
            synchronized (obj1) { // Pega a chave do Object1
                System.out.println("T1 : Estou com a chave do objeto1");
                System.out.println(" T1 : Estou esperando a chave do objeto2");
                synchronized (obj2) {
                    System.out.println(" T1 : Estou com a chave do objeto2");
                }
            }
        };

        Runnable r2 =  () -> {
            synchronized (obj2) { // Pega a chave do Object2
                System.out.println("T2 : Estou com a chave do objeto2");
                System.out.println("T2: Estou esperando a chave do objeto1");
                synchronized (obj1) {
                    System.out.println("T2: Estou com a chave do objeto1");
                }
            }
        };

        new Thread(r1).start();
        new Thread(r2).start();
    }


}
