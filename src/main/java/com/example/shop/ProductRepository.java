package com.example.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.category.id = :categoryId")
    Page<Product> findForCategory(@Param("categoryId") Long categoryId, Pageable pageable);
}
