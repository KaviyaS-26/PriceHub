package com.pricehub.repository;

import com.pricehub.entity.Marketplace;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketplaceListingRepository extends JpaRepository<MarketplaceListing, Long> {

    List<MarketplaceListing> findByProductId(Long productId);

    List<MarketplaceListing> findByProduct(Product product);

    List<MarketplaceListing> findByProductIdIn(List<Long> productIds);

    List<MarketplaceListing> findByMarketplaceCode(String marketplaceCode);

    Optional<MarketplaceListing> findByProductAndMarketplace(Product product, Marketplace marketplace);

    Optional<MarketplaceListing> findByProductIdAndMarketplaceId(Long productId, Long marketplaceId);

    Optional<MarketplaceListing> findByMarketplaceCodeAndExternalListingId(String marketplaceCode, String externalListingId);

    @Query("SELECT ml FROM MarketplaceListing ml WHERE (LOWER(ml.product.name) = LOWER(:productName) OR LOWER(ml.product.title) = LOWER(:productName)) AND LOWER(ml.marketplace.code) = LOWER(:marketplaceCode)")
    Optional<MarketplaceListing> findByProductNameAndMarketplaceCode(@Param("productName") String productName, @Param("marketplaceCode") String marketplaceCode);

    @Query("SELECT ml FROM MarketplaceListing ml WHERE (ml.inStock = true OR ml.stockStatus = 'IN_STOCK') ORDER BY COALESCE(ml.price, ml.listedPrice) ASC")
    List<MarketplaceListing> findTopDeals();

    @Query("SELECT ml FROM MarketplaceListing ml JOIN ml.product p WHERE p.category = :category AND (ml.inStock = true OR ml.stockStatus = 'IN_STOCK') ORDER BY COALESCE(ml.price, ml.listedPrice) ASC")
    List<MarketplaceListing> findByCategoryOrderByPriceAsc(@Param("category") String category);

    long countByInStockFalse();

    long countByInStockTrue();
}
