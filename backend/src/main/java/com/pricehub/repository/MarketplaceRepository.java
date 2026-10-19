package com.pricehub.repository;

import com.pricehub.entity.Marketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketplaceRepository extends JpaRepository<Marketplace, Long> {
    Optional<Marketplace> findByCode(String code);
    Optional<Marketplace> findByCodeIgnoreCase(String code);
}
