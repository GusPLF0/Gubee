package com.gusplf.springwebfluxessentials.integration;

import com.gusplf.springwebfluxessentials.controller.AnimeController;
import com.gusplf.springwebfluxessentials.domain.Anime;
import com.gusplf.springwebfluxessentials.repository.AnimeRepository;
import com.gusplf.springwebfluxessentials.service.AnimeService;
import com.gusplf.springwebfluxessentials.util.AnimeCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@SpringBootTest()
public class AnimeControllerIT {

    private final Anime anime = AnimeCreator.createValidAnime();


    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private AnimeService animeService;

    @Autowired
    private WebTestClient testClient;


    @Test
    @DisplayName("listAll returns a flux of anime")
    public void listAll_ReturnFluxOfAnime_WhenSuccessful() {
        testClient
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(anime.getId())
                .jsonPath("$.[0].name").isEqualTo(anime.getName());
    }
}
