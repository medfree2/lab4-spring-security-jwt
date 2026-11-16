package com.example.shop;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {
    @Id
    String username;
    @Column(nullable = false)
    String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    protected User() {}

    User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
