package Threads.RyanMonicaProblem;

class BankAccount {
    private int balance = 50;

    public int getBalance() {
        return balance;
    }

    public void withdrawal(int amount) {
        balance = balance - amount;
    }
}

class RyanAndMonicaJob implements Runnable {

    private BankAccount account = new BankAccount();

    public static void main(String[] args) {
        RyanAndMonicaJob theJob = new RyanAndMonicaJob();

        Thread one = new Thread(theJob);
        Thread two = new Thread(theJob);
        one.setName("Ryan");
        two.setName("Monica");
        one.start();
        two.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            makeWithdrawal(10);
            if (account.getBalance() < 0) {
                System.out.println("Estouro!");
            }
        }
    }

    private synchronized void makeWithdrawal(int amount) {
        if (account.getBalance() >= amount) {
            System.out.println(Thread.currentThread().getName() + " verificou o saldo");
            try {
                System.out.println(Thread.currentThread().getName() + " foi dormir");
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName() + " acordou");
                account.withdrawal(amount);
                System.out.println(Thread.currentThread().getName() + " concluiu a retirada, saldo atual: " + account.getBalance());
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }

        } else {
            System.out.println("Saldo insuficiente " + Thread.currentThread().getName());
        }
    }
}
