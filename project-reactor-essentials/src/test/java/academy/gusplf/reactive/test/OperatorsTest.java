package academy.gusplf.reactive.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple3;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class OperatorsTest {

    @Before
    public void setUp() {
        BlockHound.install();
    }


    private Flux<Object> emptyFlux() {
        return Flux.empty();
    }

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

    @Test
    public void concatOperator() {
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        //Concatena flux1 em flux2 criando um novo flux para isso
        Flux<String> fluxConcat = Flux.concat(flux1, flux2).log();

        StepVerifier.create(fluxConcat)
                .expectSubscription()
                .expectNext("a", "b", "c", "d")
                .expectComplete()
                .verify();
    }

    @Test
    public void concatWithOperator() {
        Flux<String> flux1 = Flux.just("c", "d").delayElements(Duration.ofMillis(100));
        // Concatena sem criar um novo flux!
        Flux<String> flux2 = Flux.just("a", "b").concatWith(flux1).log();

        StepVerifier.create(flux2)
                .expectSubscription()
                .expectNext("a", "b", "c", "d")
                .expectComplete()
                .verify();
    }

    @Test
    public void combineLastestOperator() {
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> flux3 = Flux.combineLatest(flux1, flux2, (s1, s2) -> s1.toUpperCase() + s2.toUpperCase()).log();

        StepVerifier.create(flux3)
                .expectSubscription()
                .expectNext("BC", "BD")
                .expectComplete()
                .verify();
    }

    @Test
    public void mergeOperator() throws InterruptedException {
        // Assim que o publisher lançar o dado já coloca esse novo dado em um novo flux
        // Operação EAGER enquanto concat é LAZY
        // Pode dar diferença na ordem se tiver delay
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("a", "b", "c", "d")
                .expectComplete()
                .verify();
    }

    @Test
    public void mergeWithOperator() {
        // Mesma coisa do merge, mas sem "precisar criar um novo flux"
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = flux1.mergeWith(flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("a", "b", "c", "d")
                .expectComplete()
                .verify();
    }

    @Test
    public void mergeSequentialOperator() {
        // Faz de forma Eager (sem esperar), mas, faz colocando na ordem definida
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = Flux.mergeSequential(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("a", "b", "c", "d")
                .expectComplete()
                .verify();
    }

    @Test
    public void concatOperatorError() {
        Flux<String> flux1 = Flux.just("a", "b")
                .map(s -> {
                    if (s.equals("b")) {
                        throw new IllegalArgumentException();
                    }
                    return s;
                });

        Flux<String> flux2 = Flux.just("c", "d");

        //Espera o concat terminar para "jogar" o erro
        Flux<String> fluxConcat = Flux.concatDelayError(flux1, flux2).log();

        StepVerifier.create(fluxConcat)
                .expectSubscription()
                .expectNext("a", "c", "d")
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void mergeDelayErrorOperator() {
        Flux<String> flux1 = Flux.just("a", "b").map(s -> {
            if (s.equals("b")) {
                throw new IllegalArgumentException();
            }
            return s;
        });

        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergeFlux = Flux.mergeDelayError(1, flux1, flux2, flux1).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("a", "c", "d", "a")
                .expectError()
                .verify();
    }

    @Test
    public void flatMapOperator() {
        // Transforma cada elemento em um novo flux
        Flux<String> flux = Flux.just("a", "b")
                .flatMap(this::findByName)
                .log();

//          Exemplo de teste caso não tenha o Delay
//        StepVerifier
//                .create(flux)
//                .expectSubscription()
//                .expectNext("Adebayo", "Arnold", "Backer", "Boy")
//                .verifyComplete();

        // Quando adicionamos um delay nos flux que são emitidos dentro do nosso flatMap, teremos uma diferença, já que ele executa de forma EAGER
        // A ordem aqui vai ser diferente
        StepVerifier
                .create(flux)
                .expectSubscription()
                .expectNext("Backer", "Boy", "Adebayo", "Arnold")
                .verifyComplete();
    }


    @Test
    public void flatMapSequentialOperator() {
        // Acontece de forma Eager, mas mantém a ordem no final mesmo com delay!
        Flux<String> flux = Flux.just("a", "b")
                .flatMapSequential(this::findByName)
                .log();


        StepVerifier
                .create(flux)
                .expectSubscription()
                .expectNext("Adebayo", "Arnold", "Backer", "Boy")
                .verifyComplete();
    }

    public Flux<String> findByName(String name) {
        return name.equals("a") ? Flux.just("Adebayo", "Arnold").delayElements(Duration.ofMillis(100)) : Flux.just("Backer", "Boy");
    }

    @Test
    public void zipOperator() {
        // Faz uma combinação dos elementos dos flux formando um Flux de uma tupla da combinação dos elementos
        // O primeiro elemento combina com o primeiro elemento, o segundo com o segundo e assim vai até que o menor flux termine
        Flux<String> titleFlux = Flux.just("One punch man", "Code geass");
        Flux<String> studioFlux = Flux.just("Zero-G", "Sunrise");
        Flux<Integer> episodesFlux = Flux.just(100, 25);

        Flux<Anime> animeFlux = Flux.zip(titleFlux, studioFlux, episodesFlux)
                .flatMap(tuple -> Flux.just(new Anime(tuple.getT1(), tuple.getT2(), tuple.getT3())));

        animeFlux.subscribe(s -> log.info(s.toString()));
    }

    @Test
    public void zipWithOperator() {
        // A mesma coisa, mas aqui só suportamos combinação entre dois flux (só forma uma tupla de dois valores)
        Flux<String> titleFlux = Flux.just("One punch man", "Code geass");
        Flux<String> studioFlux = Flux.just("Zero-G", "Sunrise");
        Flux<Integer> episodesFlux = Flux.just(100, 25);

        Flux<Anime> animeFlux = titleFlux.zipWith(studioFlux)
                .flatMap(tuple -> Flux.just(new Anime(tuple.getT1(), tuple.getT2(), null)));

        animeFlux.subscribe(s -> log.info(s.toString()));
    }



    @AllArgsConstructor
    @Data
    @ToString
    class Anime {
        private String title;
        private String studio;
        private Integer  episodes;
    }

}
