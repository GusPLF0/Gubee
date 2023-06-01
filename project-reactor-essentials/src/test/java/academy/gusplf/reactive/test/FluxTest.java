package academy.gusplf.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
public class FluxTest {

    @Test
    public void fluxSubscriber() {

        Flux<String> fluxString = Flux.just("Luffy", "Naruto", "Goku")
                .log();

        StepVerifier.create(fluxString)
                .expectNext("Luffy", "Naruto", "Goku")
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbers() {

        Flux<Integer> fluxString = Flux.range(1, 5)
                .log();

        fluxString.subscribe(i -> log.info("Number: {}", i));

        log.info("===================================================");

        StepVerifier.create(fluxString)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberFromList() {
        // Criando um flux usando uma lista de inteiros
        Flux<Integer> fluxString = Flux.fromIterable(List.of(1, 2, 3, 4, 5))
                .log();

        fluxString.subscribe(i -> log.info("Number: {}", i));

        log.info("===================================================");

        StepVerifier.create(fluxString)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersError() {
        Flux<Integer> fluxString = Flux.range(1, 5)
                .log()
                .map(i -> {
                    if (i == 4) {
                        throw new IndexOutOfBoundsException("Index error");
                    }
                    return i;
                });

        fluxString.subscribe(
                i -> log.info("{}", i),
                Throwable::printStackTrace,
                () -> log.info("Cheguei até o final!"),
                subscription -> subscription.request(3));

        log.info("===================================================");

        StepVerifier.create(fluxString)
                .expectNext(1, 2, 3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }

    @Test
    public void fluxSubscriberNumbersUglyBackPressure() {
        Flux<Integer> fluxString = Flux.range(1, 10)
                .log();

        fluxString.subscribe(new Subscriber<>() {
            private final int requestCount = 3;
            private int count = 0;
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;

                if (count >= requestCount) {
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });

        log.info("===================================================");

        StepVerifier.create(fluxString)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersNotSoUglyBackPressure() {
        Flux<Integer> fluxString = Flux.range(1, 10)
                .log();

        fluxString.subscribe(new BaseSubscriber<>() {
            private final int requestCount = 2;
            private int count = 0;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(requestCount);
            }

            @Override
            protected void hookOnNext(Integer value) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    request(requestCount);
                }
            }
        });

        log.info("===================================================");

        StepVerifier.create(fluxString)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }


    @Test
    public void fluxSubscriberPrettyBackPressure() {
        Flux<Integer> fluxString = Flux.range(1, 10)
                .log()
                .limitRate(3);


        fluxString.subscribe(i -> log.info("Number {}", i));

        log.info("=================================");
        StepVerifier.create(fluxString)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberIntervalOne() throws Exception {
        /* Tudo que está entre os traços ocorre em uma thread separada,
         * da forma em que está >não< veremos o log, mas se colocarmos um Thread.sleep();
         * consiguiremos ver
         * */
        // -------------------------------------------------
        Flux<Long> interval = Flux.interval(Duration.ofMillis(100))
                // .take(10) --> Quantidade de valores que você quer gerar (contando com o 0)
                .log();

        interval.subscribe(i -> log.info("number {}", i));
        // -------------------------------------------------

    }

    @Test
    public void fluxSubscriberIntervalTwo() {
        // Aqui temos uma forma de testar um Flux que gera números em intervalos de tempo....
        StepVerifier.withVirtualTime(this::createInterval)
                .expectSubscription()
                .expectNoEvent(Duration.ofDays(1))
                .thenAwait(Duration.ofDays(1))
                .expectNext(0L)
                .thenAwait(Duration.ofDays(1))
                .expectNext(2L)
                .thenCancel()
                .verify();


    }

    private Flux<Long> createInterval() {
        return Flux.interval(Duration.ofDays(1))
                .log();
    }

    @Test
    public void connectableFlux() {
        // HOT Publisher = Continua funcionando mesmo que não tenha inscritos
        ConnectableFlux<Integer> connectableFlux = Flux.range(1, 10)
                .log()
                .delayElements(Duration.ofMillis(100))
                .publish();

//        connectableFlux.connect();

//        log.info("Thread sleeping for 300 ms");
//        Thread.sleep(300);

//        connectableFlux.subscribe(i -> log.info("Sub1 Number {}", i));

//        log.info("Thread sleeping for 200 ms");
//        Thread.sleep(200);

//        connectableFlux.subscribe(i -> log.info("Sub2 Number {}", i));


        StepVerifier
                .create(connectableFlux)
                .then(connectableFlux::connect)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .expectComplete()
                .verify();

    }

    @Test
    public void connectableFluxAutoConnect() {
        // Precisamos de no mínimo 2 subscribers para começar a produção
        Flux<Integer> connectableFlux = Flux.range(1, 10)
                .log()
                .delayElements(Duration.ofMillis(100))
                .publish()
                .autoConnect(2);


        StepVerifier
                .create(connectableFlux)
                .then(connectableFlux::subscribe)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .expectComplete()
                .verify();

    }
}
