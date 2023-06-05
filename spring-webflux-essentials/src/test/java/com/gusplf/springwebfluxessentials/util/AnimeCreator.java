package com.gusplf.springwebfluxessentials.util;

import com.gusplf.springwebfluxessentials.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToBeSaved() {
        return Anime.builder()
                .name("Dragon Ball GT")
                .build();
    }

    public static Anime createValidAnime() {
        return Anime.builder()
                .id(1)
                .name("Dragon Ball GT")
                .build();
    }

    public static Anime createUpdateValidAnime() {
        return Anime.builder()
                .id(1)
                .name("Dragon Ball Z")
                .build();
    }
}
