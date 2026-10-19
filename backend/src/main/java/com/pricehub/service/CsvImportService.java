package com.pricehub.service;

import com.pricehub.dto.CsvImportResultDTO;
import com.pricehub.entity.Marketplace;
import com.pricehub.entity.MarketplaceCode;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import com.pricehub.repository.MarketplaceListingRepository;
import com.pricehub.repository.MarketplaceRepository;
import com.pricehub.repository.ProductRepository;
import com.pricehub.util.WarrantyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CsvImportService {

    private static final Logger log = LoggerFactory.getLogger(CsvImportService.class);

    private final ProductRepository productRepository;
    private final MarketplaceRepository marketplaceRepository;
    private final MarketplaceListingRepository listingRepository;

    public CsvImportService(ProductRepository productRepository,
                            MarketplaceRepository marketplaceRepository,
                            MarketplaceListingRepository listingRepository) {
        this.productRepository = productRepository;
        this.marketplaceRepository = marketplaceRepository;
        this.listingRepository = listingRepository;
    }

    /**
     * Resolves and imports prices from the default data/prices.csv file location.
     */
    @Transactional
    public CsvImportResultDTO importFromDefaultLocation() {
        Path csvPath = findDefaultCsvPath();
        if (csvPath == null || !Files.exists(csvPath)) {
            log.warn("Default prices.csv not found in candidate paths.");
            CsvImportResultDTO result = new CsvImportResultDTO();
            result.addSkippedReason("Default prices.csv file not found on filesystem.");
            result.setTotalProductsCount(productRepository.count());
            result.setTotalListingsCount(listingRepository.count());
            result.setOutOfStockCount(listingRepository.countByInStockFalse());
            return result;
        }

        log.info("Importing prices from: {}", csvPath.toAbsolutePath());
        try (InputStream is = Files.newInputStream(csvPath)) {
            return importPricesFromInputStream(is);
        } catch (IOException e) {
            log.error("Failed to read prices.csv: {}", e.getMessage(), e);
            CsvImportResultDTO result = new CsvImportResultDTO();
            result.addSkippedReason("Error reading file: " + e.getMessage());
            return result;
        }
    }

    /**
     * Imports prices from an uploaded MultipartFile.
     */
    @Transactional
    public CsvImportResultDTO importPricesFromMultipartFile(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            return importPricesFromInputStream(is);
        } catch (IOException e) {
            log.error("Failed to process uploaded multipart file: {}", e.getMessage(), e);
            CsvImportResultDTO result = new CsvImportResultDTO();
            result.addSkippedReason("Failed to read uploaded file: " + e.getMessage());
            return result;
        }
    }

    /**
     * Parses an InputStream containing CSV rows and idempotently imports or updates products and listings.
     */
    @Transactional
    public CsvImportResultDTO importPricesFromInputStream(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return importPricesFromReader(reader);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV input stream", e);
        }
    }

    /**
     * Core idempotent CSV parser and importer.
     */
    @Transactional
    public CsvImportResultDTO importPricesFromReader(BufferedReader reader) throws IOException {
        CsvImportResultDTO result = new CsvImportResultDTO();

        // Pre-seed known marketplaces if not already in DB
        ensureDefaultMarketplaces();

        String line;
        int lineNumber = 0;
        boolean headerProcessed = false;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();

            if (line.isEmpty()) {
                result.addSkippedReason("Row " + lineNumber + ": Blank row");
                result.setSkippedCount(result.getSkippedCount() + 1);
                continue;
            }

            List<String> tokens = parseCsvLine(line);

            // Check if this is the header row
            if (!headerProcessed && isHeaderRow(tokens)) {
                headerProcessed = true;
                continue;
            }

            // Check if trailing row of just empty commas (e.g. ,,,,,,,,)
            if (isAllEmpty(tokens)) {
                result.addSkippedReason("Row " + lineNumber + ": Blank trailing row");
                result.setSkippedCount(result.getSkippedCount() + 1);
                continue;
            }

            result.setTotalRowsRead(result.getTotalRowsRead() + 1);

            // Pad tokens to 9 elements if short
            while (tokens.size() < 9) {
                tokens.add("");
            }

            String productName = tokens.get(0).trim();
            String marketplaceCode = tokens.get(1).trim().toUpperCase();
            String priceStr = tokens.get(2).trim();
            String ratingStr = tokens.get(3).trim();
            String inStockStr = tokens.get(4).trim();
            String warrantyStr = tokens.get(5).trim();
            String imageUrl = tokens.get(6).trim();
            String productUrl = tokens.get(7).trim();
            String priceDate = tokens.get(8).trim();

            // Row Validation
            if (productName.isEmpty()) {
                result.addSkippedReason("Row " + lineNumber + ": Missing productName");
                result.setSkippedCount(result.getSkippedCount() + 1);
                continue;
            }

            if (marketplaceCode.isEmpty()) {
                result.addSkippedReason("Row " + lineNumber + ": Missing marketplace for product '" + productName + "'");
                result.setSkippedCount(result.getSkippedCount() + 1);
                continue;
            }

            Double price = parseDouble(priceStr);
            if (price == null || price <= 0) {
                result.addSkippedReason("Row " + lineNumber + ": Invalid price '" + priceStr + "' for product '" + productName + "'");
                result.setSkippedCount(result.getSkippedCount() + 1);
                continue;
            }

            Double rating = parseDouble(ratingStr);
            boolean inStock = "true".equalsIgnoreCase(inStockStr) || "1".equals(inStockStr);
            Integer warrantyMonths = WarrantyParser.parse(warrantyStr);
            String category = getCategoryForProduct(productName);

            // 1. Upsert Product
            Product product = productRepository.findByNameIgnoreCase(productName)
                    .orElseGet(() -> {
                        Product newProduct = new Product(productName, category);
                        newProduct.setBasePrice(price);
                        if (!imageUrl.isEmpty()) {
                            newProduct.setImageUrl(imageUrl);
                        }
                        return productRepository.save(newProduct);
                    });

            if (product.getCategory() == null || product.getCategory().isEmpty()) {
                product.setCategory(category);
                product = productRepository.save(product);
            }
            if ((product.getImageUrl() == null || product.getImageUrl().isEmpty()) && !imageUrl.isEmpty()) {
                product.setImageUrl(imageUrl);
                product = productRepository.save(product);
            }

            // 2. Lookup or create Marketplace
            Marketplace marketplace = getOrCreateMarketplace(marketplaceCode);

            // 3. Upsert MarketplaceListing
            Optional<MarketplaceListing> existingListingOpt = listingRepository.findByProductAndMarketplace(product, marketplace);
            MarketplaceListing listing;
            if (existingListingOpt.isPresent()) {
                listing = existingListingOpt.get();
                result.setUpdatedCount(result.getUpdatedCount() + 1);
            } else {
                listing = new MarketplaceListing();
                listing.setProduct(product);
                listing.setMarketplace(marketplace);
                result.setImportedCount(result.getImportedCount() + 1);
            }

            listing.setTitle(productName);
            listing.setPrice(price);
            listing.setListedPrice(price);
            listing.setRating(rating);
            listing.setInStock(inStock);
            listing.setStockStatus(inStock ? "IN_STOCK" : "OUT_OF_STOCK");
            listing.setWarrantyText(warrantyStr);
            listing.setWarrantyMonths(warrantyMonths);
            if (!imageUrl.isEmpty()) {
                listing.setImageUrl(imageUrl);
            }
            if (!productUrl.isEmpty()) {
                listing.setProductUrl(productUrl);
            }
            listing.setPriceDate(priceDate);
            listing.setDataSource("COLLECTED");
            listing.setLastUpdated(LocalDateTime.now());

            listingRepository.save(listing);
        }

        result.setTotalProductsCount(productRepository.count());
        result.setTotalListingsCount(listingRepository.count());
        result.setOutOfStockCount(listingRepository.countByInStockFalse());

        log.info("CSV import finished: read={}, imported={}, updated={}, skipped={}, products={}, listings={}, outOfStock={}",
                result.getTotalRowsRead(), result.getImportedCount(), result.getUpdatedCount(),
                result.getSkippedCount(), result.getTotalProductsCount(), result.getTotalListingsCount(),
                result.getOutOfStockCount());

        return result;
    }

    /**
     * Pre-seeds standard marketplaces if they don't already exist.
     */
    private void ensureDefaultMarketplaces() {
        for (MarketplaceCode code : MarketplaceCode.values()) {
            getOrCreateMarketplace(code.name());
        }
    }

    private Marketplace getOrCreateMarketplace(String code) {
        return marketplaceRepository.findByCodeIgnoreCase(code)
                .orElseGet(() -> {
                    MarketplaceCode mCode = MarketplaceCode.fromString(code);
                    String displayName = mCode != null ? mCode.getDisplayName() : code;
                    String baseUrl = mCode != null ? mCode.getBaseUrl() : "https://" + code.toLowerCase() + ".com";
                    String logoUrl = mCode != null ? mCode.getLogoUrl() : "";
                    double trust = mCode != null ? mCode.getDefaultTrustScore() : 4.5;
                    Marketplace m = new Marketplace(displayName, code.toUpperCase(), baseUrl, logoUrl, trust);
                    return marketplaceRepository.save(m);
                });
    }

    /**
     * Resolves the Category for a product name as specified in the resume prompt.
     */
    public static String getCategoryForProduct(String productName) {
        if (productName == null) return "General";
        String trimmed = productName.trim();

        // Exact prompt mapping
        switch (trimmed) {
            case "Wireless Bluetooth Neckband Earphones":
            case "True Wireless Earbuds with Charging Case":
            case "Wired In-Ear Earphones with Mic":
                return "Audio";

            case "Women's Cotton Churidar Set":
            case "Women's Anarkali Kurti Set":
            case "Women's Floral Print Maxi Dress":
            case "Girls Cotton Party Frock":
            case "Men's Cotton Round Neck T-Shirt":
                return "Clothing";

            case "Men's Running Sports Shoes":
            case "Women's Flat Sandals":
                return "Footwear";

            case "Women's Analog Wrist Watch Metal Strap":
            case "Men's Digital Sports Watch":
                return "Watches";

            case "Women's Sling Bag":
                return "Bags";

            case "Home Decor Item":
                return "Home Decor";

            case "Kids Cycle":
                return "Kids";

            default:
                String lower = trimmed.toLowerCase();
                if (lower.contains("earphone") || lower.contains("earbud") || lower.contains("neckband") || lower.contains("audio")) return "Audio";
                if (lower.contains("churidar") || lower.contains("kurti") || lower.contains("dress") || lower.contains("frock") || lower.contains("t-shirt")) return "Clothing";
                if (lower.contains("sports shoes") || lower.contains("sandals") || lower.contains("shoe") || lower.contains("footwear")) return "Footwear";
                if (lower.contains("wrist watch") || lower.contains("sports watch") || lower.contains("watch")) return "Watches";
                if (lower.contains("sling bag") || lower.contains("bag")) return "Bags";
                if (lower.contains("decor")) return "Home Decor";
                if (lower.contains("cycle")) return "Kids";
                return "General";
        }
    }

    /**
     * Tokenizes a CSV line respecting quoted values with commas.
     */
    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        if (line == null) return tokens;

        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(cleanToken(sb.toString()));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(cleanToken(sb.toString()));
        return tokens;
    }

    private static String cleanToken(String token) {
        if (token == null) return "";
        String trimmed = token.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private boolean isHeaderRow(List<String> tokens) {
        if (tokens.isEmpty()) return false;
        String first = tokens.get(0).toLowerCase();
        return first.contains("productname") || first.contains("product_name") || first.contains("product");
    }

    private boolean isAllEmpty(List<String> tokens) {
        for (String token : tokens) {
            if (!token.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Path findDefaultCsvPath() {
        List<Path> candidates = Arrays.asList(
                Paths.get("data", "prices.csv"),
                Paths.get("..", "data", "prices.csv"),
                Paths.get("prices.csv"),
                Paths.get(System.getProperty("user.dir"), "data", "prices.csv"),
                Paths.get(System.getProperty("user.dir"), "..", "data", "prices.csv")
        );

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
