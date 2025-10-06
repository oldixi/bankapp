package ru.yandex.accounts.serv.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsUserByLoginIgnoreCase(String login);

    Optional<User> findUserByLoginIgnoreCase(String login);
}
