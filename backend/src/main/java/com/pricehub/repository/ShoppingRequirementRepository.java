package com.pricehub.repository;

import com.pricehub.entity.UserShoppingRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingRequirementRepository extends JpaRepository<UserShoppingRequirement, Long> {
    List<UserShoppingRequirement> findByUserIdOrderByCreatedAtDesc(Long userId);
}
