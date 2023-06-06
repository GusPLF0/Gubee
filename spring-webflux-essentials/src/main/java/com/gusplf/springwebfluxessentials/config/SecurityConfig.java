package com.gusplf.springwebfluxessentials.config;

import com.gusplf.springwebfluxessentials.service.DojoUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/animes/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/animes/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/animes/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/animes/**").hasRole("USER")
                        .pathMatchers( "/webjars/**", "v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager(DojoUserDetailsService dojoUserDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(dojoUserDetailsService);
    }
}
