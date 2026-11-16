package com.example.shop;

import static com.example.shop.ApiDtos.*;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final CatalogService service;

    public OrderController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN','MANAGER')")
    Page<OrderDto> list(@PageableDefault(sort = "id") Pageable page, Authentication auth) {
        return service.orders(owner(auth), page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','MANAGER')")
    OrderDto get(@PathVariable Long id, Authentication auth) {
        return service.getOrder(id, owner(auth));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<OrderDto> create(@Valid @RequestBody OrderRequest request, Authentication auth) {
        OrderDto order = service.createOrder(auth.getName(), request);
        return ResponseEntity.created(URI.create("/api/orders/" + order.id())).body(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private String owner(Authentication auth) {
        boolean staff = auth.getAuthorities().stream().anyMatch(authority ->
                List.of("ROLE_ADMIN", "ROLE_MANAGER").contains(authority.getAuthority()));
        return staff ? null : auth.getName();
    }
}
