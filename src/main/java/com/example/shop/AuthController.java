package com.example.shop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository repository;
    private final UserDetailsService users;
    private final PasswordEncoder passwords;
    private final JwtUtil tokens;
    private final String dummyHash;

    AuthController(UserRepository repository, UserDetailsService users, PasswordEncoder passwords, JwtUtil tokens) {
        this.repository = repository;
        this.users = users;
        this.passwords = passwords;
        this.tokens = tokens;
        this.dummyHash = passwords.encode(java.util.UUID.randomUUID().toString());
    }

    public record Credentials(@NotBlank @Pattern(regexp = "[a-z0-9_.-]{3,40}") String username,
                              @NotBlank @Size(min = 8, max = 72) String password) {}
    public record Account(String username, String role) {}
    public record Token(String token, String tokenType, long expiresIn) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    Account register(@Valid @RequestBody Credentials request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe trop long en UTF-8");
        }
        if (repository.existsById(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Identifiant déjà utilisé");
        }
        repository.insert(request.username(), passwords.encode(request.password()), Role.USER.name());
        return new Account(request.username(), Role.USER.name());
    }

    @PostMapping("/login")
    Token login(@Valid @RequestBody Credentials request) {
        UserDetails user = null;
        try {
            user = users.loadUserByUsername(request.username());
        } catch (UsernameNotFoundException ignored) {
            // Effectuer aussi une comparaison BCrypt pour un compte inconnu.
        }
        boolean valid = request.password().getBytes(StandardCharsets.UTF_8).length <= 72
                && passwords.matches(request.password(), user == null ? dummyHash : user.getPassword());
        if (!valid || user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
        return token(user);
    }

    @PostMapping("/refresh")
    Token refresh(@AuthenticationPrincipal Jwt jwt) {
        Instant now = Instant.now();
        if (!jwt.getExpiresAt().isAfter(now) || jwt.getExpiresAt().isAfter(now.plusSeconds(300))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Renouvellement possible dans les 5 dernières minutes");
        }
        return token(users.loadUserByUsername(jwt.getSubject()));
    }

    private Token token(UserDetails user) {
        return new Token(tokens.issue(user), "Bearer", 900);
    }
}
