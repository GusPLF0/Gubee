package com.gusplf.springwebfluxessentials.repository;

import com.gusplf.springwebfluxessentials.domain.DojoUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface DojoUserRepository extends ReactiveCrudRepository<DojoUser, Integer> {

    Mono<DojoUser> findByUsername(String username);
}
