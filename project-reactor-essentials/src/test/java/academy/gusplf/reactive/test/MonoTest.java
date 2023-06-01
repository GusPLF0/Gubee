package academy.gusplf.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Subscription;
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
                .map(s -> {
                    throw new RuntimeException("Testing with error");
                });

        mono.subscribe(s -> log.info("Eae {}, como você está?", s), s -> log.error("Algo deu errado"));
        mono.subscribe(s -> log.info("Eae {}, como você está?", s), Throwable::printStackTrace);

        log.info("=====================================================");

        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoSubscriberConsumerErrorAndComplete() {
        String name = "Gustavo";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Eae {}, como você está?", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));

        log.info("=====================================================");

        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerSubscription() {
        String name = "Gustavo";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Eae {}, como você está?", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"),
                Subscription::cancel);

        log.info("=====================================================");

//        StepVerifier.create(mono)
//                .expectNext(name.toUpperCase())
//                .verifyComplete();
    }

    @Test
    public void monoDoOnMethods() {
        String name = "Gustavo";
        Mono<Object> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Someone subscribed (doOnSubscribe)"))
                .doOnRequest(longNumber -> log.info("Request received, doing something (doOnRequest)"))
                .doOnNext(S -> log.info("Value is here... (doOnNext) "))
                .flatMap(s -> Mono.empty())
                .doOnNext(S -> log.info("Value is here... (doOnNext) ")) // Não é executado, já que não tem nada
                .doOnSuccess(s -> log.info("Everything works! (doOnSucess)"));

        mono.subscribe(s -> log.info("Eae {}, como você está?", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));

    }


    @Test
    public void monoDoOnError() {
        Mono<Object> error = Mono.error(IllegalArgumentException::new)
                .doOnError(e -> log.error("Error message {}", e.getMessage()))
                .log();



        StepVerifier.create(error)
                .expectError(IllegalArgumentException.class)
                .verify();
    }


    @Test
    public void monoDoOnErrorContinue() {
        String name = "Naruto";

        Mono<Object> error = Mono.error(IllegalArgumentException::new)
                .doOnError(e -> log.error("Error message {}", e.getMessage()))
                .onErrorResume(s -> {
                    log.info("Continuando após o erro");
                    return Mono.just(name);
                })
                .log();



        StepVerifier.create(error)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    public void monoOnErroReturn() {
        String name = "Naruto";

        Mono<Object> error = Mono.error(IllegalArgumentException::new)
                .doOnError(e -> log.error("Error message {}", e.getMessage()))
                .onErrorReturn("EMPTY")
                .log();



        StepVerifier.create(error)
                .expectNext("EMPTY")
                .verifyComplete();
    }
}
