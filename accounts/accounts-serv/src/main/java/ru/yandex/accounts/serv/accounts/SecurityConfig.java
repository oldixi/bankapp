package ru.yandex.accounts.serv.accounts;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Client(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/actuator/**", "/fallback/**").permitAll()
                        .requestMatchers("/api/accounts/signup").permitAll()
                        .anyRequest().authenticated())
                /*.oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.)
                )*/
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
