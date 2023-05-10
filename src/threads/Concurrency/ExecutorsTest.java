package threads.Concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Printer implements Runnable {

    private final int num;

    public Printer(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        System.out.printf("%s inicio: %d%n", Thread.currentThread().getName(), num);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.printf("%s finalizou: %n", Thread.currentThread().getName());

    }
}
public class ExecutorsTest {
    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        ExecutorService executorService2 = Executors.newSingleThreadExecutor();
//        ExecutorService executorService3 = Executors.newCachedThreadPool();
//        executorService.execute(new Printer(1));
//        executorService.execute(new Printer(2));
//        executorService.execute(new Printer(3));
//        executorService.execute(new Printer(4));
//        executorService.shutdown();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.schedule(new Printer(1), 5, TimeUnit.SECONDS);

        executorService.shutdown();
    }
}
