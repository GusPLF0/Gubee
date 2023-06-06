package com.gusplf.springwebfluxessentials.integration;

import com.gusplf.springwebfluxessentials.domain.Anime;
import com.gusplf.springwebfluxessentials.repository.AnimeRepository;
import com.gusplf.springwebfluxessentials.util.AnimeCreator;
import com.gusplf.springwebfluxessentials.util.WebTestClientUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class AnimeControllerIT {

    private final Anime anime = AnimeCreator.createValidAnime();

    @Autowired
    private WebTestClientUtil webTestClientUtil;


    @MockBean
    private AnimeRepository animeRepositoryMock;

    private WebTestClient testClientUser;

    private WebTestClient testClientAdmin;

    private WebTestClient testClientInvalid;

    @BeforeEach
    public void setUp() {
        // https://www.youtube.com/watch?v=nyKfIsKpSs0 -- > Outra forma de fazer, sem precisar de 3 clientUtils
        testClientUser = webTestClientUtil.authneticateClient("Lima", "admin");
        testClientAdmin = webTestClientUtil.authneticateClient("GusPLF", "admin");
        testClientInvalid = webTestClientUtil.authneticateClient("aaa", "aaaa");

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(anime));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
                .thenReturn(Flux.just(anime, anime));

        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createValidAnime()))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("listAll returns Unauthorized  when user doesn't successfully authenticated ")
    public void listAll_ReturnsUnauthorized_WhenSuccessful() {
        testClientInvalid
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("listAll returns FORBIDDEN  when user authenticated but doesn't have role admin")
    public void listAll_ReturnsForbidden_WhenSuccessful() {
        testClientUser
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("listAll returns a flux of anime when user has the role admin")
    public void listAll_ReturnFluxOfAnime_WhenSuccessful() {
        testClientAdmin
                .get()
                .uri("/animes")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(anime.getId())
                .jsonPath("$.[0].name").isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("findById returns Mono with anime when it exists and when user has the role admin")
    public void findById_ReturnMonoAnime_WhenSuccessful() {
        testClientAdmin
                .get()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(anime.getId())
                .jsonPath("$.name").isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("findById returns Mono error when anime doesn't exist and when user has the role admin")
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        testClientAdmin
                .get()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("save creates an anime when successful and when user has the role admin")
    public void save_CreatesMonoAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        testClientAdmin
                .post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeToBeSaved))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("save returns mono error with bad request when name is empty and when user has the role admin")
    public void save_ReturnsError_WhenNameIsEmpty() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");

        testClientAdmin
                .post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(animeToBeSaved))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

//    @Test
//    @DisplayName("saveBatch creates a list of anime when successful and when user has the role admin")
//    public void saveBatch_CreatesListOfAnime_WhenSuccessful() {
//        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
//
//        testClientAdmin
//                .post()
//                .uri("/animes/batch")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBodyList(Anime.class)
//                .hasSize(2)
//                .contains(anime);
//    }

//    @Test
//    @DisplayName("saveBatch returns Mono error when one of the objects in the list contains invalid name and when user has the role admin")
//    public void saveBatch_ReturnMonoError_WhenContainsInvalidName() {
//        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
//
//        BDDMockito.when(animeRepositoryMock.saveAll(ArgumentMatchers.anyIterable()))
//                .thenReturn(Flux.just(anime, anime.withName("")));
//
//        testClientAdmin
//                .post()
//                .uri("/animes/batch")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(400);
//    }

    @Test
    @DisplayName("delete removes the anime when successful and when user has the role admin")
    public void delete_RemovesAnime_WhenSuccessful() {

        testClientAdmin
                .delete()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("delete returns Mono error when the anime doesn't exist and when user has the role admin")
    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        testClientAdmin
                .delete()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("update save updated anime and returns empty mono when successful and when user has the role admin")
    public void update_SaveUpdatedAnime_WhenSuccessful() {
        testClientAdmin
                .put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("update returns Mono error when anime does not exist and when user has the role admin")
    public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        testClientAdmin
                .put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }
}
