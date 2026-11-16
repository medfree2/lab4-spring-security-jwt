package com.example.shop;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class StaffBootstrap implements ApplicationRunner {
    private final Environment environment;
    private final UserRepository users;
    private final PasswordEncoder passwords;

    StaffBootstrap(Environment environment, UserRepository users, PasswordEncoder passwords) {
        this.environment = environment;
        this.users = users;
        this.passwords = passwords;
    }

    @Override
    public void run(ApplicationArguments args) {
        create(Role.ADMIN);
        create(Role.MANAGER);
    }

    private void create(Role role) {
        String username = environment.getProperty("LAB_" + role + "_USERNAME");
        String password = environment.getProperty("LAB_" + role + "_PASSWORD");
        if (username == null && password == null) return;
        if (username == null || !username.matches("[a-z0-9_.-]{3,40}") || password == null
                || password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Identifiants d’amorçage invalides pour " + role);
        }
        users.findById(username).ifPresentOrElse(existing -> {
            if (existing.role != role) {
                throw new IllegalStateException("Le compte d’amorçage existe avec un autre rôle");
            }
        }, () -> users.insert(username, passwords.encode(password), role.name()));
    }
}
