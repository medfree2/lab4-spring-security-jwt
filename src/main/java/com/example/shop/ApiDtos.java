package com.example.shop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}

    public record CategoryRequest(@NotBlank @Size(max = 255) String name) {}
    public record CategoryDto(Long id, String name) {}
    public record ProductRequest(@NotBlank @Size(max = 255) String name,
            @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal price,
            @NotNull @Positive Long categoryId) {}
    public record ProductDto(Long id, String name, BigDecimal price, Long categoryId) {}
    public record OrderRequest(@NotEmpty List<@NotNull @Valid OrderItemRequest> items) {}
    public record OrderItemRequest(@NotNull @Positive Long productId, @NotNull @Positive Integer quantity) {}
    public record OrderItemDto(Long id, Long productId, int quantity, BigDecimal unitPrice) {}
    public record OrderDto(Long id, Instant createdAt, List<OrderItemDto> items) {}
}
