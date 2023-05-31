package academy.gusplf.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
/**
 * Reactive Streams
 * 1. Assincrona
 * 2. Non-blocking
 * 3. Backpressure
 *
 * Publisher = Cria os eventos
 *     ^--(subscribe)--- Subscriber = Se inscreve para os eventos
 *            ^-------- Subscription = Criada quando um subscriber se inscreve para um publisher
 *
 * De forma automatica, quando você se inscreve em um publisher é chamado um onSubscribe com um subscription
 *
 * O subscription é usado para gerencia backpressure (subscriber gerencia o relacionamento)
 * **/
public class MonoTest {

    @Test
    public void monoSubscriber() {
        String name = "Gustavo";
        Mono<String> mono = Mono.just(name)
                .log();

        mono.subscribe();
        log.info("=====================================================");
        StepVerifier.create(mono)
                        .expectNext("Gustavo").verifyComplete();
    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "Gustavo";
        Mono<String> mono = Mono.just(name)
                .log();

        mono.subscribe(s -> log.info("Eae {}, como você está?", s));

        log.info("=====================================================");

        StepVerifier.create(mono)
                        .expectNext("Gustavo").verifyComplete();
    }
    @Test
    public void monoSubscriberConsumerError() {
        String name = "Gustavo";
        Mono<String> mono = Mono.just(name)
                        .map(s -> {throw new RuntimeException("Testing with error");});

        mono.subscribe(s -> log.info("Eae {}, como você está?", s), s -> log.error("Algo deu errado"));
        mono.subscribe(s -> log.info("Eae {}, como você está?", s),Throwable::printStackTrace);

        log.info("=====================================================");

        StepVerifier.create(mono)
                        .expectError(RuntimeException.class).verify();
    }
}
