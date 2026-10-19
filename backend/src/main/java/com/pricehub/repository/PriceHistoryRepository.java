package com.pricehub.repository;

import com.pricehub.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByMarketplaceListingIdOrderByRecordedAtAsc(Long listingId);
    List<PriceHistory> findByMarketplaceListingIdInOrderByRecordedAtAsc(List<Long> listingIds);
}
