package com.example.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

interface UserRepository extends JpaRepository<User, String> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO app_users(username, password_hash, role) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void insert(String username, String hash, String role);
}
