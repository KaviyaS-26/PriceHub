# PriceHub — Multi-Marketplace Best-Value Price Comparison Platform
> *"Shop Smarter. Choose Better."*

---

## 📌 Project Overview
**PriceHub** is an intelligent shopping platform that compares products across **Amazon**, **Flipkart**, and **Meesho**, calculates true effective purchase costs with bank/coupon discounts, and dynamically calculates best-value options on every request.

---

## 🏛️ Java OOP Architecture & Design Principles

The backend is built in **Java Spring Boot** following enterprise-grade Object-Oriented principles:

| Principle | Implementation in PriceHub |
|---|---|
| **Abstraction** | `MarketplaceAdapter` interface abstracts pricing & listing retrieval across diverse marketplaces. |
| **Polymorphism** | `AmazonAdapter`, `FlipkartAdapter`, and `MeeshoAdapter` implement specific marketplace logic dynamically resolved via `MarketplaceAdapterRegistry`. |
| **Encapsulation** | Business rules, discount computations, and user state are safely encapsulated inside domain entities and services (`EffectiveCostCalculator`, `ProductComparisonService`, `ValueScoreCalculator`). |
| **Composition** | `Product` entities compose `ProductSpecification` items and `MarketplaceListing` records. |
| **Dependency Injection** | Inversion of Control (IoC) with Spring `@Service`, `@Component`, and constructor injection. |
| **Separation of Concerns** | Thin REST Controllers (`ProductController`, `CompareController`, `DealLabController`), rich domain Services, and decoupled JPA Repositories. |

---

## 🚀 Key Features

1. **Three-Marketplace Aggregation**: Comparison across Amazon, Flipkart, and Meesho with clear `"Collected data, 15-17 Oct 2026"` badges.
2. **Algorithmic Best-Value Compare Engine**: Dynamic multi-factor value scoring across price, warranty, and rating with user-adjustable priority sliders via `GET /api/compare`.
3. **Effective Cost Calculator**:
   $$\text{Effective Cost} = \text{Listed Price} + \text{Delivery Charges} + \text{Mandatory Charges} - \text{Applicable Discounts}$$
4. **Best-Value Recommendation Engine**: Multi-factor weighted normalization (Effective Cost, Rating, Warranty).
5. **Personalized Deal Lab (Signature Feature)**: Constraint solver evaluating user constraints, explaining qualifying/non-qualifying listings, and suggesting constraint relaxation.
6. **Price History & Price Alerts**: Historical price tracking charts with simulated notifications when target price is met.
7. **Authentication & Profile**: Spring Security with BCrypt hashing, JWT token authentication, profile management, and saved wishlists.

---

## 📂 Project Structure

```
oopsjava-PRICEHUB/
├── backend/
│   ├── src/main/java/com/pricehub/
│   │   ├── adapter/          # MarketplaceAdapter, AmazonAdapter, FlipkartAdapter, MeeshoAdapter
│   │   ├── config/           # DatabaseSeeder, SecurityConfig, CorsConfig
│   │   ├── controller/       # REST Controllers (Auth, Products, Comparison, DealLab, Account)
│   │   ├── dto/              # DTOs for requests and responses
│   │   ├── engine/           # EffectiveCostCalculator, ProductMatcher, ValueScoreCalculator
│   │   ├── entity/           # JPA Entities (Product, Listing, Offer, User)
│   │   ├── repository/       # Spring Data JPA Repositories
│   │   ├── security/         # JWT Token Provider, Auth Filter, UserDetailsService
│   │   └── service/          # Business logic services
│   ├── src/main/resources/   # application.properties, application-mysql.properties
│   └── pom.xml               # Maven configuration
├── frontend/
│   ├── src/
│   │   ├── components/       # Logo, Navbar, Footer, ProductCard, PriceChart
│   │   ├── context/          # AuthContext, ComparisonContext
│   │   ├── pages/            # Connected pages
│   │   ├── services/         # REST API client
│   │   └── types/            # TypeScript interfaces
│   ├── package.json
│   └── vite.config.ts
└── database/
    └── schema.sql            # MySQL schema definition
```

---

## 🛠️ Quick Start & Running Locally

### 1. Run Backend (Spring Boot)
Open a terminal in the `backend` directory:
```bash
# Using bundled Maven
..\maven\apache-maven-3.9.6\bin\mvn spring-boot:run
```
*Backend runs on `http://localhost:8080` (with in-memory H2 database auto-seeded by default).*

To run with **MySQL**:
1. Create database: `CREATE DATABASE pricehub_db;`
2. Run with MySQL profile:
   ```bash
   ..\maven\apache-maven-3.9.6\bin\mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

### 2. Run Frontend (React + Vite)
Open a terminal in the `frontend` directory:
```bash
npm install
npm run dev
```
*Frontend runs on `http://localhost:5173`.*

---

## 🔑 Demo Credentials

| Role | Email | Password |
|---|---|---|
| **Demo User** | `demo@pricehub.com` | `Password123` |
| **Administrator** | `admin@pricehub.com` | `Admin123` |

---

## 🧪 Running Unit & Integration Tests
```bash
cd backend
..\maven\apache-maven-3.9.6\bin\mvn test
```
All 9 unit tests for **Effective Cost Calculator**, **Product Matcher**, **Value Score Calculator**, **Deal Lab**, and **Auth Service** execute with 100% success.
