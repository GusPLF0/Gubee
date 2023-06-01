package academy.gusplf.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class OperatorsTest {

    @Test
    public void subscribeOnSimples() {
        Flux<Integer> map = Flux.range(1, 4)
                .map(i -> {
                    log.info("Map 1 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single()) // AFETA TODAS AS OPERAÇÕES
                .map(i -> {
                    log.info("Map 2 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(map)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @Test
    public void publishOnSimples() {
        Flux<Integer> map = Flux.range(1, 4)
                .map(i -> {
                    log.info("Map 1 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic()) // AFETA APENAS O SCHEDULER DO QUE ESTÁ APÓS É AFETADO, OS DE CIMA FICA NA MAIN!!
                .map(i -> {
                    log.info("Map 2 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(map)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @Test
    public void multipleSubscribeOnSimple() {
        // Quando temos essa situação o primeiro subscribeOn que aparece é escolhido
        Flux<Integer> map = Flux.range(1, 4)
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 1 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(map)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @Test
    public void multiplePublishOnSimples() {
        // Respeitando o funcionamento do publishOn aqui vai mudar, é considerado oq estiver mais acima da operação executada
        Flux<Integer> map = Flux.range(1, 4)
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 1 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(map)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @Test
    public void publishAndSubscribeOn() {
        Flux<Integer> map = Flux.range(1, 4)
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 1 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic()) // "IGNORADO"
                .map(i -> {
                    log.info("Map 2 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(map)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @Test
    public void subscribeAndPublishOn() {
        Flux<Integer> map = Flux.range(1, 4)
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 1 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 2 - number: {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(map)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @Test
    public void subscribeOnIO() throws InterruptedException {
        // Executa em uma thread separada no background
        Mono<List<String>> list = Mono.fromCallable(() -> Files.readAllLines(Path.of("text-file")))
                .log()
                .subscribeOn(Schedulers.boundedElastic());

//        list.subscribe(s -> log.info("{}", s));
        StepVerifier.create(list)
                .expectSubscription()
                .thenConsumeWhile(l -> {
                    Assert.assertFalse(l.isEmpty());
                    log.info("List size : {}", l.size());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void switchIfEmptyOperator() {
        Flux<Object> flux = emptyFlux()
                .switchIfEmpty(Flux.just("Not empty anymore"))
                .log();

        StepVerifier
                .create(flux)
                .expectSubscription()
                .expectNext("Not empty anymore")
                .verifyComplete();
    }

    @Test
    public void deferOperator() throws Exception {
        Mono<Long> just = Mono.just(System.currentTimeMillis()); // Dessa forma ele pega o tempo do momento que executa, não importa o momento em que o subscriber fez sua inscrição
        Mono<Long> defer = Mono.defer(() -> Mono.just(System.currentTimeMillis())); // Pega algo diferente para cada subscriber


        defer.subscribe(l -> log.info("Time {}", l));
        defer.subscribe(l -> log.info("Time {}", l));
        defer.subscribe(l -> log.info("Time {}", l));
        defer.subscribe(l -> log.info("Time {}", l));

        AtomicLong atomicLong = new AtomicLong();
        defer.subscribe(atomicLong::set);
    }

    private Flux<Object> emptyFlux() {
        return Flux.empty();
    }
}
