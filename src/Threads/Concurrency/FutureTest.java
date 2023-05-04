package Threads.Concurrency;

import java.util.concurrent.*;

public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<Double> dollarRequest = executorService.submit(() -> {
            TimeUnit.SECONDS.sleep(10);
            return 4.35D;
        });

        System.out.println(doingSomething());
        Double dollarResponse = dollarRequest.get(3, TimeUnit.SECONDS);
        System.out.println("Dollar: " + dollarResponse);

        executorService.shutdown();
    }

    private static long doingSomething() {
        System.out.println(Thread.currentThread().getName());
        long sum = 0;
        for (int i = 0; i < 100000; i++) {
            sum += i;
        }
        return sum;
    }
}
