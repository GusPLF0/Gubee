package com.gusplf.springwebfluxessentials.service;

import com.gusplf.springwebfluxessentials.domain.Anime;
import com.gusplf.springwebfluxessentials.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    // Spring faz o subscribe automático para nós!
    public Flux<Anime> findAll() {
        return animeRepository.findAll();
    }

    public Mono<Anime> findById(int id) {
        return animeRepository.findById(id)
                .switchIfEmpty(Mono.just(new Anime(2, "Testando")));
    }
}
