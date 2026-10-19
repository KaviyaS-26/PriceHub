package com.pricehub.config;

import com.pricehub.entity.*;
import com.pricehub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final MarketplaceRepository marketplaceRepository;
    private final ProductRepository productRepository;
    private final ProductSpecificationRepository specRepository;
    private final MarketplaceListingRepository listingRepository;
    private final OfferRepository offerRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.pricehub.service.CsvImportService csvImportService;

    public DatabaseSeeder(MarketplaceRepository marketplaceRepository,
                          ProductRepository productRepository,
                          ProductSpecificationRepository specRepository,
                          MarketplaceListingRepository listingRepository,
                          OfferRepository offerRepository,
                          PriceHistoryRepository priceHistoryRepository,
                          UserRepository userRepository,
                          UserPreferenceRepository userPreferenceRepository,
                          PasswordEncoder passwordEncoder,
                          com.pricehub.service.CsvImportService csvImportService) {
        this.marketplaceRepository = marketplaceRepository;
        this.productRepository = productRepository;
        this.specRepository = specRepository;
        this.listingRepository = listingRepository;
        this.offerRepository = offerRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.passwordEncoder = passwordEncoder;
        this.csvImportService = csvImportService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Seed Marketplaces
        Marketplace amazon = marketplaceRepository.findByCodeIgnoreCase("AMAZON")
                .orElseGet(() -> marketplaceRepository.save(new Marketplace(
                        "Amazon India", "AMAZON", "https://www.amazon.in",
                        "https://upload.wikimedia.org/wikipedia/commons/a/a9/Amazon_logo.svg", 4.9)));

        Marketplace flipkart = marketplaceRepository.findByCodeIgnoreCase("FLIPKART")
                .orElseGet(() -> marketplaceRepository.save(new Marketplace(
                        "Flipkart", "FLIPKART", "https://www.flipkart.com",
                        "https://static-assets-web.flixcart.com/batman-returns/batman-returns/p/images/fkheaderlogo_exploreplus-44005d.svg", 4.8)));

        Marketplace meesho = marketplaceRepository.findByCodeIgnoreCase("MEESHO")
                .orElseGet(() -> marketplaceRepository.save(new Marketplace(
                        "Meesho", "MEESHO", "https://www.meesho.com",
                        "https://images.meesho.com/images/meesho_logo.svg", 4.6)));

        Marketplace reliance = marketplaceRepository.findByCodeIgnoreCase("RELIANCE_DIGITAL")
                .orElseGet(() -> marketplaceRepository.save(new Marketplace(
                        "Reliance Digital", "RELIANCE_DIGITAL", "https://www.reliancedigital.in",
                        "https://www.reliancedigital.in/build/client/images/loaders/rd_logo.svg", 4.7)));

        // 2. Seed Users
        if (userRepository.count() == 0) {
            User demoUser = new User("Kaviya Sekar", "demo@pricehub.com", passwordEncoder.encode("Password123"), "+91 9876543210");
            demoUser.setRole("ROLE_USER");
            User savedDemo = userRepository.save(demoUser);

            User adminUser = new User("PriceHub Admin", "admin@pricehub.com", passwordEncoder.encode("Admin123"), "+91 9876543211");
            adminUser.setRole("ROLE_ADMIN");
            userRepository.save(adminUser);

            UserPreference demoPref = new UserPreference(savedDemo);
            demoPref.setPreferredCategory("Audio");
            demoPref.setMaxBudget(75000.0);
            userPreferenceRepository.save(demoPref);
        }

        // 3. Import prices.csv at startup (idempotent)
        com.pricehub.dto.CsvImportResultDTO importResult = csvImportService.importFromDefaultLocation();
        if (importResult.getTotalProductsCount() == 0 && productRepository.count() == 0) {
            seedCatalog(amazon, flipkart, reliance);
        }
    }

    private void seedCatalog(Marketplace amazon, Marketplace flipkart, Marketplace reliance) {
        // --- Product 1: Apple MacBook Air M2 ---
        Product macbook = productRepository.save(new Product(
                "Apple MacBook Air M2 (13.6-inch, 16GB RAM, 512GB SSD, Midnight)",
                "Apple", "MC7X4HN/A", "Laptops", 99990.0,
                "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80",
                "Supercharged by the next-generation M2 chip, the redesigned MacBook Air combines incredible performance and up to 18 hours of battery life into its strikingly thin aluminum enclosure."
        ));
        macbook.setRatingAverage(4.8);
        productRepository.save(macbook);

        addSpecs(macbook, Arrays.asList(
                new SpecItem("ram", "16 GB", "Performance"),
                new SpecItem("storage", "512 GB SSD", "Performance"),
                new SpecItem("processor", "Apple M2 (8-core CPU, 10-core GPU)", "Performance"),
                new SpecItem("screen_size", "13.6 inch Liquid Retina (2560x1664)", "Display"),
                new SpecItem("battery", "Up to 18 hours", "Battery & Power"),
                new SpecItem("weight", "1.24 kg", "General"),
                new SpecItem("os", "macOS Sonoma", "General")
        ));

        createListingsAndOffers(macbook, amazon, flipkart, reliance,
                94990.0, 0.0, 0.0, 4.8, 1240, 12, 1, // Amazon
                96990.0, 0.0, 49.0, 4.7, 980, 12, 2,  // Flipkart
                93990.0, 99.0, 0.0, 4.6, 320, 24, 3,  // Reliance
                "HDFC", 4000.0, "HDFCLAPTOP4K", "ICICI", 3500.0, "ICICISPECIAL"
        );

        // --- Product 2: Dell Inspiron 15 3530 ---
        Product dell = productRepository.save(new Product(
                "Dell Inspiron 15 (Core i7 13th Gen, 16GB RAM, 512GB SSD, FHD 120Hz)",
                "Dell", "IN3530-I716512", "Laptops", 64990.0,
                "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?auto=format&fit=crop&w=800&q=80",
                "Powerful Intel 13th Gen Core i7 laptop featuring 16GB high-speed DDR4 memory, fast NVMe SSD, and 120Hz anti-glare display ideal for office productivity, multitasking, and college coursework."
        ));
        dell.setRatingAverage(4.5);
        productRepository.save(dell);

        addSpecs(dell, Arrays.asList(
                new SpecItem("ram", "16 GB", "Performance"),
                new SpecItem("storage", "512 GB SSD", "Performance"),
                new SpecItem("processor", "Intel Core i7-1355U (10 Cores, up to 5.0 GHz)", "Performance"),
                new SpecItem("screen_size", "15.6 inch FHD 120Hz Anti-Glare", "Display"),
                new SpecItem("battery", "Up to 8 hours (ExpressCharge)", "Battery & Power"),
                new SpecItem("weight", "1.65 kg", "General"),
                new SpecItem("os", "Windows 11 Home + MS Office 2021", "General")
        ));

        createListingsAndOffers(dell, amazon, flipkart, reliance,
                58990.0, 0.0, 0.0, 4.5, 850, 12, 2,
                57490.0, 0.0, 0.0, 4.4, 620, 12, 2,
                56990.0, 0.0, 0.0, 4.5, 410, 24, 3,
                "SBI", 2500.0, "SBISAVE25", "HDFC", 2000.0, "HDFCDELL"
        );

        // --- Product 3: ASUS TUF Gaming F15 ---
        Product asus = productRepository.save(new Product(
                "ASUS TUF Gaming F15 (Core i7-12700H, 16GB RAM, 512GB SSD, RTX 3050 4GB)",
                "ASUS", "FX506HE-HN382W", "Laptops", 72990.0,
                "https://images.unsplash.com/photo-1603302576837-37561b2e2302?auto=format&fit=crop&w=800&q=80",
                "Geared for serious gaming and real-world durability, TUF Gaming F15 features a 144Hz IPS display, dedicated GeForce RTX 3050 GPU, and military-grade MIL-STD-810H construction."
        ));
        asus.setRatingAverage(4.6);
        productRepository.save(asus);

        addSpecs(asus, Arrays.asList(
                new SpecItem("ram", "16 GB", "Performance"),
                new SpecItem("storage", "512 GB SSD", "Performance"),
                new SpecItem("processor", "Intel Core i7-12700H (14 Cores, 20 Threads)", "Performance"),
                new SpecItem("screen_size", "15.6 inch FHD 144Hz IPS", "Display"),
                new SpecItem("graphics", "NVIDIA GeForce RTX 3050 4GB GDDR6", "Performance"),
                new SpecItem("battery", "Up to 6 hours (90Whr)", "Battery & Power"),
                new SpecItem("os", "Windows 11 Home", "General")
        ));

        createListingsAndOffers(asus, amazon, flipkart, reliance,
                68990.0, 0.0, 0.0, 4.6, 1420, 12, 2,
                69490.0, 0.0, 0.0, 4.6, 1100, 12, 3,
                67990.0, 0.0, 0.0, 4.5, 390, 12, 2,
                "ICICI", 3000.0, "ICICIGAMING", "AXIS", 2500.0, "AXISTUF"
        );

        // --- Product 4: HP Pavilion 14 ---
        Product hp = productRepository.save(new Product(
                "HP Pavilion 14 (AMD Ryzen 5 7530U, 16GB RAM, 512GB SSD, Backlit KB)",
                "HP", "14-ec1019AU", "Laptops", 54990.0,
                "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?auto=format&fit=crop&w=800&q=80",
                "Compact and stylish laptop packed with AMD Ryzen 7000 series processing power, B&O Audio, fingerprint reader, and full-day battery life for college and travel."
        ));
        hp.setRatingAverage(4.4);
        productRepository.save(hp);

        addSpecs(hp, Arrays.asList(
                new SpecItem("ram", "16 GB", "Performance"),
                new SpecItem("storage", "512 GB SSD", "Performance"),
                new SpecItem("processor", "AMD Ryzen 5 7530U (6 Cores, 12 Threads)", "Performance"),
                new SpecItem("screen_size", "14 inch FHD IPS Micro-edge", "Display"),
                new SpecItem("weight", "1.41 kg", "General"),
                new SpecItem("battery", "Up to 9 hours", "Battery & Power"),
                new SpecItem("os", "Windows 11 Home", "General")
        ));

        createListingsAndOffers(hp, amazon, flipkart, reliance,
                49990.0, 0.0, 0.0, 4.4, 760, 12, 2,
                48990.0, 0.0, 0.0, 4.5, 920, 12, 1,
                49490.0, 0.0, 0.0, 4.3, 210, 24, 3,
                "HDFC", 2000.0, "HDFCHP20", "SBI", 1500.0, "SBISAVE"
        );

        // --- Product 5: Apple iPhone 15 (128GB, Black) ---
        Product iphone15 = productRepository.save(new Product(
                "Apple iPhone 15 (128GB, Black - Dynamic Island, 48MP Camera)",
                "Apple", "MTP03HN/A", "Smartphones", 79900.0,
                "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=800&q=80",
                "iPhone 15 brings you Dynamic Island, a 48MP Main camera, and USB-C — all in a durable color-infused glass and aluminum design."
        ));
        iphone15.setRatingAverage(4.7);
        productRepository.save(iphone15);

        addSpecs(iphone15, Arrays.asList(
                new SpecItem("ram", "6 GB", "Performance"),
                new SpecItem("storage", "128 GB", "Performance"),
                new SpecItem("processor", "Apple A16 Bionic (6-core CPU)", "Performance"),
                new SpecItem("screen_size", "6.1 inch Super Retina XDR OLED", "Display"),
                new SpecItem("camera", "48MP Main + 12MP Ultra Wide | 12MP Front", "Camera"),
                new SpecItem("battery", "Up to 20 hours video playback", "Battery & Power"),
                new SpecItem("os", "iOS 17", "General")
        ));

        createListingsAndOffers(iphone15, amazon, flipkart, reliance,
                70990.0, 0.0, 0.0, 4.7, 3420, 12, 1,
                69999.0, 0.0, 99.0, 4.7, 4120, 12, 1,
                71490.0, 0.0, 0.0, 4.6, 850, 12, 2,
                "HDFC", 4000.0, "HDFCIPHONE", "ICICI", 3500.0, "ICICIIPHONE"
        );

        // --- Product 6: Samsung Galaxy S24 Ultra (256GB, Titanium Gray) ---
        Product s24 = productRepository.save(new Product(
                "Samsung Galaxy S24 Ultra 5G (12GB RAM, 256GB Storage, Galaxy AI, Titanium Gray)",
                "Samsung", "SM-S928B", "Smartphones", 129999.0,
                "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?auto=format&fit=crop&w=800&q=80",
                "Unleash new levels of creativity and productivity with Galaxy AI, Titanium armor frame, built-in S Pen, and 200MP Quad Telephoto Camera system."
        ));
        s24.setRatingAverage(4.8);
        productRepository.save(s24);

        addSpecs(s24, Arrays.asList(
                new SpecItem("ram", "12 GB", "Performance"),
                new SpecItem("storage", "256 GB UFS 4.0", "Performance"),
                new SpecItem("processor", "Snapdragon 8 Gen 3 for Galaxy (4nm)", "Performance"),
                new SpecItem("screen_size", "6.8 inch Dynamic AMOLED 2X 120Hz Flat (2600 nits)", "Display"),
                new SpecItem("camera", "200MP + 50MP + 12MP + 10MP Quad Camera", "Camera"),
                new SpecItem("battery", "5000 mAh (45W Fast Charging)", "Battery & Power"),
                new SpecItem("os", "Android 14 with One UI 6.1 (7 Years OS Updates)", "General")
        ));

        createListingsAndOffers(s24, amazon, flipkart, reliance,
                119999.0, 0.0, 0.0, 4.8, 1850, 12, 1,
                121999.0, 0.0, 0.0, 4.8, 1420, 12, 2,
                118499.0, 0.0, 0.0, 4.7, 520, 24, 2,
                "HDFC", 6000.0, "HDFCGALAXY", "SBI", 5000.0, "SBIS24"
        );

        // --- Product 7: OnePlus 12 (16GB RAM, 512GB, Silky Black) ---
        Product oneplus = productRepository.save(new Product(
                "OnePlus 12 5G (16GB RAM, 512GB Storage, 50MP Hasselblad, 100W SUPERVOOC)",
                "OnePlus", "CPH2581", "Smartphones", 69999.0,
                "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=800&q=80",
                "Powered by Snapdragon 8 Gen 3 with 4th Gen Hasselblad Camera system, 2K 120Hz ProXDR Display, and massive 5400mAh battery with 100W ultra-fast charging."
        ));
        oneplus.setRatingAverage(4.6);
        productRepository.save(oneplus);

        addSpecs(oneplus, Arrays.asList(
                new SpecItem("ram", "16 GB LPDDR5X", "Performance"),
                new SpecItem("storage", "512 GB UFS 4.0", "Performance"),
                new SpecItem("processor", "Snapdragon 8 Gen 3", "Performance"),
                new SpecItem("screen_size", "6.82 inch 2K 120Hz LTPO AMOLED (4500 nits)", "Display"),
                new SpecItem("camera", "50MP Sony LYT-808 + 64MP 3x Periscope + 48MP Ultra-Wide", "Camera"),
                new SpecItem("battery", "5400 mAh (100W Wired + 50W Wireless AIRVOOC)", "Battery & Power"),
                new SpecItem("os", "OxygenOS 14 (Android 14)", "General")
        ));

        createListingsAndOffers(oneplus, amazon, flipkart, reliance,
                64999.0, 0.0, 0.0, 4.6, 2100, 12, 1,
                65999.0, 0.0, 0.0, 4.5, 1340, 12, 2,
                63999.0, 0.0, 0.0, 4.6, 480, 12, 2,
                "ICICI", 3000.0, "ICICIOP12", "HDFC", 2500.0, "HDFCONEPLUS"
        );

        // --- Product 8: Sony WH-1000XM5 Wireless Noise Cancelling Headphones ---
        Product sony = productRepository.save(new Product(
                "Sony WH-1000XM5 Wireless Industry Leading Noise Canceling Headphones (Black)",
                "Sony", "WH1000XM5/B", "Headphones", 34990.0,
                "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=800&q=80",
                "Two processors and 8 microphones for unprecedented noise cancellation, exceptional sound quality with High-Resolution Audio, crystal clear hands-free calling, and up to 30-hour battery life."
        ));
        sony.setRatingAverage(4.7);
        productRepository.save(sony);

        addSpecs(sony, Arrays.asList(
                new SpecItem("battery", "Up to 30 Hours (3 min quick charge for 3 hrs)", "Battery & Power"),
                new SpecItem("noise_cancellation", "Industry Leading Auto NC Optimizer", "Performance"),
                new SpecItem("bluetooth", "Bluetooth 5.2 with LDAC & Multipoint Connection", "Connectivity"),
                new SpecItem("weight", "250 grams (Ultra comfortable lightweight design)", "General")
        ));

        createListingsAndOffers(sony, amazon, flipkart, reliance,
                28990.0, 0.0, 0.0, 4.7, 4800, 12, 1,
                29490.0, 0.0, 0.0, 4.7, 3200, 12, 2,
                27990.0, 0.0, 0.0, 4.6, 910, 12, 2,
                "SBI", 2000.0, "SBISONY2K", "HDFC", 1500.0, "HDFCSOUND"
        );

        // --- Product 9: Apple Watch Series 9 (GPS 45mm, Midnight) ---
        Product appleWatch = productRepository.save(new Product(
                "Apple Watch Series 9 GPS 45mm (Midnight Aluminum Case with Sport Band)",
                "Apple", "MR9A3HN/A", "Smartwatches", 44900.0,
                "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&w=800&q=80",
                "Smarter, brighter, and mightier with the S9 SiP chip, Double Tap gesture control, precision finding for iPhone, and advanced health metrics including ECG and blood oxygen."
        ));
        appleWatch.setRatingAverage(4.7);
        productRepository.save(appleWatch);

        addSpecs(appleWatch, Arrays.asList(
                new SpecItem("screen_size", "45mm Always-On Retina OLED (2000 nits)", "Display"),
                new SpecItem("processor", "Apple S9 SiP (Dual-Core)", "Performance"),
                new SpecItem("sensors", "ECG, Blood Oxygen, Heart Rate, Temperature, Fall Detection", "Features"),
                new SpecItem("battery", "Up to 18 hours (36 hours in Low Power Mode)", "Battery & Power"),
                new SpecItem("water_resistance", "50m Water resistant (Swimproof)", "Durability")
        ));

        createListingsAndOffers(appleWatch, amazon, flipkart, reliance,
                38990.0, 0.0, 0.0, 4.7, 1920, 12, 1,
                39490.0, 0.0, 0.0, 4.7, 1450, 12, 2,
                37990.0, 0.0, 0.0, 4.6, 420, 12, 3,
                "HDFC", 2500.0, "HDFCWATCH", "ICICI", 2000.0, "ICICIWATCH"
        );

        // --- Product 10: Sony Bravia 55-inch 4K Google TV ---
        Product sonyTv = productRepository.save(new Product(
                "Sony Bravia 55 inch 4K Ultra HD Smart LED Google TV (KD-55X74L, Black)",
                "Sony", "KD-55X74L", "TVs", 69900.0,
                "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?auto=format&fit=crop&w=800&q=80",
                "Breathtaking colors and detail with 4K Processor X1, Live Color technology, Dolby Audio, and Google TV OS with voice assistance."
        ));
        sonyTv.setRatingAverage(4.8);
        productRepository.save(sonyTv);

        addSpecs(sonyTv, Arrays.asList(
                new SpecItem("screen_size", "55 inch 4K Ultra HD (3840 x 2160)", "Display"),
                new SpecItem("display_type", "LED with 4K X-Reality PRO & Motionflow XR 100", "Display"),
                new SpecItem("sound", "20 Watts Output with Dolby Audio & Open Baffle Speaker", "Audio"),
                new SpecItem("smart_features", "Google TV, Watchlist, Voice Search, Chromecast built-in", "Smart Features"),
                new SpecItem("ports", "3 HDMI ports, 2 USB ports", "Connectivity")
        ));

        createListingsAndOffers(sonyTv, amazon, flipkart, reliance,
                54990.0, 0.0, 0.0, 4.8, 3800, 24, 2,
                55990.0, 0.0, 0.0, 4.7, 2900, 12, 2,
                53490.0, 0.0, 0.0, 4.8, 1200, 24, 1,
                "HDFC", 3000.0, "HDFCSNTV", "SBI", 2500.0, "SBITV"
        );
    }

    private void addSpecs(Product product, List<SpecItem> items) {
        for (SpecItem item : items) {
            ProductSpecification spec = new ProductSpecification(product, item.key, item.value, item.group);
            specRepository.save(spec);
        }
    }

    private void createListingsAndOffers(Product product,
                                         Marketplace amazon, Marketplace flipkart, Marketplace reliance,
                                         double amzPrice, double amzDelivery, double amzMandatory, double amzRating, int amzReviews, int amzWarranty, int amzDays,
                                         double fkPrice, double fkDelivery, double fkMandatory, double fkRating, int fkReviews, int fkWarranty, int fkDays,
                                         double rdPrice, double rdDelivery, double rdMandatory, double rdRating, int rdReviews, int rdWarranty, int rdDays,
                                         String bank1, double bank1Disc, String bank1Code,
                                         String bank2, double bank2Disc, String bank2Code) {

        // 1. Amazon Listing
        MarketplaceListing lAmz = listingRepository.save(new MarketplaceListing(
                product, amazon, product.getTitle(),
                amzPrice, amzDelivery, amzMandatory, amzRating, amzReviews, amzWarranty, amzDays,
                "IN_STOCK", "https://www.amazon.in/dp/" + product.getModelNumber(), "DEMO_DATA"
        ));
        lAmz.setExternalListingId("AMZ-" + product.getId() + "-01");
        listingRepository.save(lAmz);

        offerRepository.save(new Offer(lAmz, "BANK_CARD", String.format("Flat ₹%,.0f Instant Discount with %s Credit Cards", bank1Disc, bank1),
                String.format("Get ₹%,.0f off on %s bank credit cards on minimum purchase of ₹20,000", bank1Disc, bank1),
                bank1Disc, 0.0, 20000.0, bank1, "CREDIT", bank1Code));

        offerRepository.save(new Offer(lAmz, "COUPON", "₹500 Instant Cart Coupon",
                "Apply coupon code at checkout for immediate ₹500 discount",
                500.0, 0.0, 10000.0, null, "ANY", "SAVE500"));

        seedPriceHistory(lAmz, amzPrice);

        // 2. Flipkart Listing
        MarketplaceListing lFk = listingRepository.save(new MarketplaceListing(
                product, flipkart, product.getTitle(),
                fkPrice, fkDelivery, fkMandatory, fkRating, fkReviews, fkWarranty, fkDays,
                "IN_STOCK", "https://www.flipkart.com/item/" + product.getModelNumber(), "DEMO_DATA"
        ));
        lFk.setExternalListingId("FK-" + product.getId() + "-02");
        listingRepository.save(lFk);

        offerRepository.save(new Offer(lFk, "BANK_CARD", String.format("10%% Instant Discount up to ₹%,.0f on %s Cards", bank2Disc, bank2),
                String.format("10%% off with %s Bank cards on orders above ₹15,000", bank2),
                bank2Disc, 10.0, 15000.0, bank2, "ANY", bank2Code));

        offerRepository.save(new Offer(lFk, "PUBLIC", "Special SuperCoin Cashback & Deal Bonus",
                "Extra ₹750 instant discount applied at billing for all registered buyers",
                750.0, 0.0, 10000.0, null, "ANY", null));

        seedPriceHistory(lFk, fkPrice);

        // 3. Reliance Digital Listing
        MarketplaceListing lRd = listingRepository.save(new MarketplaceListing(
                product, reliance, product.getTitle(),
                rdPrice, rdDelivery, rdMandatory, rdRating, rdReviews, rdWarranty, rdDays,
                "IN_STOCK", "https://www.reliancedigital.in/product/" + product.getModelNumber(), "DEMO_DATA"
        ));
        lRd.setExternalListingId("RD-" + product.getId() + "-03");
        listingRepository.save(lRd);

        offerRepository.save(new Offer(lRd, "BANK_CARD", String.format("Instant ₹%,.0f off on %s / %s Cards", bank1Disc + 500, bank1, bank2),
                "Reliance Digital exclusive store card discount applied at payment gateway",
                bank1Disc + 500, 0.0, 25000.0, bank1, "ANY", "RDEXCL"));

        seedPriceHistory(lRd, rdPrice);
    }

    private void seedPriceHistory(MarketplaceListing listing, double currentPrice) {
        LocalDateTime now = LocalDateTime.now();
        priceHistoryRepository.save(new PriceHistory(listing, currentPrice * 1.08, now.minusDays(45), true));
        priceHistoryRepository.save(new PriceHistory(listing, currentPrice * 1.05, now.minusDays(30), true));
        priceHistoryRepository.save(new PriceHistory(listing, currentPrice * 1.02, now.minusDays(15), true));
        priceHistoryRepository.save(new PriceHistory(listing, currentPrice * 0.98, now.minusDays(7), true));
        priceHistoryRepository.save(new PriceHistory(listing, currentPrice, now, true));
    }

    private static class SpecItem {
        String key;
        String value;
        String group;

        SpecItem(String key, String value, String group) {
            this.key = key;
            this.value = value;
            this.group = group;
        }
    }
}
