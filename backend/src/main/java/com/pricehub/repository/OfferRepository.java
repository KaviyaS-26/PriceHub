package com.pricehub.repository;

import com.pricehub.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByMarketplaceListingId(Long listingId);
    List<Offer> findByMarketplaceListingIdIn(List<Long> listingIds);
}
