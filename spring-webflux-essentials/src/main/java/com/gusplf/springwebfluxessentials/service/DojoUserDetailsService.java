package com.gusplf.springwebfluxessentials.service;

import com.gusplf.springwebfluxessentials.repository.DojoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class DojoUserDetailsService implements ReactiveUserDetailsService {

    private final DojoUserRepository dojoUserRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return dojoUserRepository.findByUsername(username)
                .cast(UserDetails.class);
    }
}
