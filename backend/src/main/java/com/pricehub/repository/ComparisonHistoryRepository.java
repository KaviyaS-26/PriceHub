package com.pricehub.repository;

import com.pricehub.entity.ComparisonHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComparisonHistoryRepository extends JpaRepository<ComparisonHistory, Long> {
    List<ComparisonHistory> findByUserIdOrderByComparedAtDesc(Long userId);
}
