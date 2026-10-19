package com.pricehub.service;

import com.pricehub.adapter.MarketplaceAdapterRegistry;
import com.pricehub.dto.*;
import com.pricehub.entity.*;
import com.pricehub.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final ShoppingRequirementRepository shoppingRequirementRepository;
    private final SavedProductRepository savedProductRepository;
    private final PriceAlertRepository priceAlertRepository;
    private final ComparisonHistoryRepository comparisonHistoryRepository;
    private final ProductRepository productRepository;
    private final MarketplaceListingRepository listingRepository;
    private final PasswordEncoder passwordEncoder;
    private final MarketplaceAdapterRegistry adapterRegistry;

    public UserService(UserRepository userRepository,
                       UserPreferenceRepository userPreferenceRepository,
                       ShoppingRequirementRepository shoppingRequirementRepository,
                       SavedProductRepository savedProductRepository,
                       PriceAlertRepository priceAlertRepository,
                       ComparisonHistoryRepository comparisonHistoryRepository,
                       ProductRepository productRepository,
                       MarketplaceListingRepository listingRepository,
                       PasswordEncoder passwordEncoder,
                       MarketplaceAdapterRegistry adapterRegistry) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.shoppingRequirementRepository = shoppingRequirementRepository;
        this.savedProductRepository = savedProductRepository;
        this.priceAlertRepository = priceAlertRepository;
        this.comparisonHistoryRepository = comparisonHistoryRepository;
        this.productRepository = productRepository;
        this.listingRepository = listingRepository;
        this.passwordEncoder = passwordEncoder;
        this.adapterRegistry = adapterRegistry;
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt()
        );

        userPreferenceRepository.findByUserId(userId).ifPresent(pref -> {
            response.setPreferences(new UserPreferenceDTO(
                    pref.getPreferredCategory(),
                    pref.getMinBudget(),
                    pref.getMaxBudget(),
                    pref.getPreferredBrand(),
                    pref.getPreferredMarketplace(),
                    pref.getMaxDeliveryDays(),
                    pref.getDefaultSort()
            ));
        });

        return response;
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect.");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        return getProfile(userId);
    }

    @Transactional
    public UserPreferenceDTO updateUserPreferences(Long userId, UserPreferenceDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserPreference pref = userPreferenceRepository.findByUserId(userId)
                .orElse(new UserPreference(user));

        if (dto.getPreferredCategory() != null) pref.setPreferredCategory(dto.getPreferredCategory());
        if (dto.getMinBudget() != null) pref.setMinBudget(dto.getMinBudget());
        if (dto.getMaxBudget() != null) pref.setMaxBudget(dto.getMaxBudget());
        if (dto.getPreferredBrand() != null) pref.setPreferredBrand(dto.getPreferredBrand());
        if (dto.getPreferredMarketplace() != null) pref.setPreferredMarketplace(dto.getPreferredMarketplace());
        if (dto.getMaxDeliveryDays() != null) pref.setMaxDeliveryDays(dto.getMaxDeliveryDays());
        if (dto.getDefaultSort() != null) pref.setDefaultSort(dto.getDefaultSort());

        UserPreference saved = userPreferenceRepository.save(pref);
        return new UserPreferenceDTO(
                saved.getPreferredCategory(),
                saved.getMinBudget(),
                saved.getMaxBudget(),
                saved.getPreferredBrand(),
                saved.getPreferredMarketplace(),
                saved.getMaxDeliveryDays(),
                saved.getDefaultSort()
        );
    }

    // Saved Products
    @Transactional
    public void saveProduct(Long userId, Long productId, String notes) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        if (!savedProductRepository.existsByUserIdAndProductId(userId, productId)) {
            SavedProduct saved = new SavedProduct(user, product, notes);
            savedProductRepository.save(saved);
        }
    }

    @Transactional
    public void removeSavedProduct(Long userId, Long productId) {
        savedProductRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public List<SavedProductDTO> getSavedProducts(Long userId) {
        return savedProductRepository.findByUserIdOrderBySavedAtDesc(userId).stream()
                .map(sp -> {
                    SavedProductDTO dto = new SavedProductDTO();
                    dto.setId(sp.getId());
                    dto.setNotes(sp.getNotes());
                    dto.setSavedAt(sp.getSavedAt());

                    Product p = sp.getProduct();
                    ProductDTO pDto = new ProductDTO();
                    pDto.setId(p.getId());
                    pDto.setTitle(p.getTitle());
                    pDto.setBrand(p.getBrand());
                    pDto.setCategory(p.getCategory());
                    pDto.setBasePrice(p.getBasePrice());
                    pDto.setImageUrl(p.getImageUrl());
                    pDto.setRatingAverage(p.getRatingAverage());
                    dto.setProduct(pDto);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Price Alerts
    @Transactional
    public PriceAlertDTO createPriceAlert(Long userId, PriceAlertRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = null;
        MarketplaceListing listing = null;

        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId()).orElse(null);
        }
        if (request.getListingId() != null) {
            listing = listingRepository.findById(request.getListingId()).orElse(null);
            if (product == null && listing != null) {
                product = listing.getProduct();
            }
        }
        if (product == null && listing == null) {
            throw new IllegalArgumentException("Valid Product ID or Listing ID is required");
        }

        String mkt = (request.getMarketplace() != null && !request.getMarketplace().isBlank() && !"ALL".equalsIgnoreCase(request.getMarketplace()))
                ? request.getMarketplace().trim().toUpperCase() : null;

        double currentPrice = 0.0;
        if (listing != null) {
            currentPrice = listing.getPrice();
        } else if (product != null) {
            List<MarketplaceListing> listings = listingRepository.findByProductId(product.getId());
            currentPrice = listings.stream().filter(MarketplaceListing::isInStock).mapToDouble(MarketplaceListing::getPrice).min().orElse(0.0);
        }

        PriceAlert alert = new PriceAlert();
        alert.setUser(user);
        alert.setProduct(product);
        alert.setMarketplaceListing(listing);
        alert.setMarketplace(mkt);
        alert.setTargetPrice(request.getTargetPrice());
        alert.setCurrentPriceAtCreation(currentPrice);
        alert.setIsActive(true);
        alert.setIsTriggered(false);

        PriceAlert saved = priceAlertRepository.save(alert);
        return toPriceAlertDTO(saved);
    }

    public List<PriceAlertDTO> getUserPriceAlerts(Long userId) {
        return priceAlertRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toPriceAlertDTO)
                .collect(Collectors.toList());
    }

    private PriceAlertDTO toPriceAlertDTO(PriceAlert alert) {
        PriceAlertDTO dto = new PriceAlertDTO();
        dto.setId(alert.getId());
        Product prod = alert.getProduct() != null ? alert.getProduct() :
                (alert.getMarketplaceListing() != null ? alert.getMarketplaceListing().getProduct() : null);

        if (prod != null) {
            dto.setProductId(prod.getId());
            dto.setProductTitle(prod.getTitle());
            dto.setProductImageUrl(prod.getImageUrl());
        } else if (alert.getMarketplaceListing() != null) {
            dto.setProductTitle(alert.getMarketplaceListing().getTitle());
        }

        if (alert.getMarketplaceListing() != null) {
            dto.setListingId(alert.getMarketplaceListing().getId());
        }

        dto.setMarketplace(alert.getMarketplace());
        dto.setMarketplaceName(alert.getMarketplace() != null ? formatAlertMarketplaceName(alert.getMarketplace()) : "All Marketplaces");
        dto.setTargetPrice(alert.getTargetPrice());
        dto.setPriceAtCreation(alert.getCurrentPriceAtCreation());
        dto.setIsActive(alert.getIsActive());
        dto.setCreatedAt(alert.getCreatedAt());

        // Calculate status dynamically on every request from current in-stock listings
        // Never store a triggered flag.
        if (prod != null) {
            List<MarketplaceListing> currentListings = listingRepository.findByProductId(prod.getId());
            List<MarketplaceListing> inStock = currentListings.stream()
                    .filter(MarketplaceListing::isInStock)
                    .filter(l -> {
                        if (alert.getMarketplace() == null || alert.getMarketplace().isBlank() || "ALL".equalsIgnoreCase(alert.getMarketplace())) {
                            return true;
                        }
                        String target = alert.getMarketplace().toUpperCase();
                        String code = l.getMarketplace() != null ? l.getMarketplace().getCode() : "";
                        String name = l.getMarketplace() != null ? l.getMarketplace().getName() : "";
                        return code.equalsIgnoreCase(target) || name.toUpperCase().contains(target);
                    })
                    .collect(Collectors.toList());

            if (!inStock.isEmpty()) {
                MarketplaceListing lowest = inStock.stream()
                        .min(Comparator.comparingDouble(MarketplaceListing::getPrice))
                        .orElse(inStock.get(0));

                double lowestPrice = lowest.getPrice();
                dto.setCurrentPrice(lowestPrice);
                String mktName = formatListingMarketplaceName(lowest);
                long roundedLowest = Math.round(lowestPrice);
                long roundedTarget = Math.round(alert.getTargetPrice());

                if (lowestPrice <= alert.getTargetPrice()) {
                    dto.setStatus("Target met on " + mktName + " at ₹" + roundedLowest);
                    dto.setIsTriggered(true);
                } else {
                    long diff = roundedLowest - roundedTarget;
                    dto.setStatus("Not met: lowest in-stock price is ₹" + roundedLowest + ", ₹" + diff + " above your target");
                    dto.setIsTriggered(false);
                }
            } else {
                dto.setStatus("Not met: product is currently out of stock");
                dto.setIsTriggered(false);
            }
        } else {
            dto.setStatus("Not met: product not found");
            dto.setIsTriggered(false);
        }

        return dto;
    }

    private String formatAlertMarketplaceName(String code) {
        if (code == null) return "All Marketplaces";
        String upper = code.trim().toUpperCase();
        if (upper.contains("AMAZON")) return "Amazon";
        if (upper.contains("FLIPKART")) return "Flipkart";
        if (upper.contains("MEESHO")) return "Meesho";
        return code.charAt(0) + code.substring(1).toLowerCase();
    }

    private String formatListingMarketplaceName(MarketplaceListing l) {
        if (l == null) return "Marketplace";
        String name = l.getMarketplace() != null ? l.getMarketplace().getName() : "Marketplace";
        return formatAlertMarketplaceName(name);
    }

    @Transactional
    public void deletePriceAlert(Long userId, Long alertId) {
        priceAlertRepository.findById(alertId).ifPresent(alert -> {
            if (alert.getUser().getId().equals(userId)) {
                priceAlertRepository.delete(alert);
            }
        });
    }

    // Comparison History
    @Transactional
    public void recordComparison(Long userId, String productIds, String summaryTitle) {
        User user = userRepository.findById(userId).orElseThrow();
        ComparisonHistory history = new ComparisonHistory(user, productIds, summaryTitle);
        comparisonHistoryRepository.save(history);
    }

    public List<ComparisonHistoryDTO> getUserComparisonHistory(Long userId) {
        return comparisonHistoryRepository.findByUserIdOrderByComparedAtDesc(userId).stream()
                .map(ch -> {
                    ComparisonHistoryDTO dto = new ComparisonHistoryDTO();
                    dto.setId(ch.getId());
                    dto.setProductIds(ch.getProductIds());
                    dto.setSummaryTitle(ch.getSummaryTitle());
                    dto.setComparedAt(ch.getComparedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setAccountStatus("DELETED");
        userRepository.save(user);
    }
}
