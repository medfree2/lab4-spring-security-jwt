package com.example.shop;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String ownerUsername;
    @Column(nullable = false)
    Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<OrderItem> items = new ArrayList<>();

    void addItem(OrderItem item) {
        item.order = this;
        items.add(item);
    }
}
