package com.pricehub.repository;

import com.pricehub.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    Optional<Product> findByNameIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name) OR LOWER(p.title) = LOWER(:name)")
    Optional<Product> findByNameOrTitleIgnoreCase(@Param("name") String name);

    List<Product> findByCategoryIgnoreCase(String category);

    @Query("SELECT p FROM Product p WHERE " +
           "(p.name IS NOT NULL AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(p.title IS NOT NULL AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(p.brand IS NOT NULL AND LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(p.category IS NOT NULL AND LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    List<Product> findByBrandIgnoreCase(String brand);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL AND TRIM(p.category) <> '' ORDER BY p.category")
    List<String> findDistinctCategories();
}
