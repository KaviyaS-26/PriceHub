export interface Marketplace {
  id: number;
  name: string;
  code: string;
  baseUrl: string;
  logoUrl: string;
  trustScore: number;
}

export interface ProductSpecification {
  id: number;
  specKey: string;
  specValue: string;
  specGroup: string;
}

export interface Offer {
  id: number;
  offerType: 'PUBLIC' | 'BANK_CARD' | 'COUPON' | 'EXCHANGE' | 'MEMBERSHIP';
  title: string;
  description: string;
  discountAmount: number;
  discountPercentage: number;
  minimumPurchaseAmount: number;
  bankName?: string;
  cardType?: string;
  couponCode?: string;
  termsConditions?: string;
  isVerified: boolean;
  validUntil?: string;
}

export interface CostBreakdown {
  listedPrice: number;
  deliveryCharge: number;
  mandatoryCharges: number;
  grossPrice: number;
  totalDiscountAmount: number;
  effectiveCost: number;
  savingsPercentage: number;
  appliedOffers: Offer[];
  calculationExplanation: string;
  isDemoData: boolean;
  priceFreshness: string;
}

export interface MarketplaceListing {
  id: number;
  productId: number;
  productTitle: string;
  marketplace: Marketplace;
  externalListingId: string;
  title: string;
  listedPrice: number;
  deliveryCharge: number;
  mandatoryCharges: number;
  rating: number;
  reviewCount: number;
  warrantyMonths: number;
  deliveryEstimateDays: number;
  stockStatus: string;
  isVerified: boolean;
  productUrl: string;
  dataSource: string;
  priceDate?: string;
  lastUpdated: string;
  offers: Offer[];
  costBreakdown: CostBreakdown;
  valueScore?: number;
  badges?: string[];
  allMarketplaceListings?: MarketplaceListing[];
}

export interface Product {
  id: number;
  title: string;
  brand: string;
  modelNumber: string;
  category: string;
  basePrice: number;
  imageUrl: string;
  description: string;
  ratingAverage: number;
  lowestEffectivePrice: number;
  highestEffectivePrice: number;
  availableListingsCount: number;
  bestValueMarketplace: string;
  createdAt: string;
  specifications: ProductSpecification[];
  listings: MarketplaceListing[];
}

export interface MatchResult {
  confidence: 'EXACT_MATCH' | 'LIKELY_MATCH' | 'DIFFERENT_PRODUCT';
  matchScore: number;
  reason: string;
  matchingAttributes: string[];
  differingAttributes: string[];
  isCriticalMismatch: boolean;
}

export interface ComparisonData {
  products: Product[];
  matchResult?: MatchResult;
  lowestEffectiveCostListing?: MarketplaceListing;
  bestRatedListing?: MarketplaceListing;
  bestValueListing?: MarketplaceListing;
  maxPriceDifference?: number;
  maxSavingsPercentage?: number;
  specificationMatrix: Record<string, string[]>;
  keyDifferences: string[];
  smartSummary: string;
}

export interface RecommendedItem {
  product: Product;
  bestListing: MarketplaceListing;
  valueScore: number;
  recommendationLabel: string;
  plainLanguageExplanation: string;
  highlightPros: string[];
  highlightCons: string[];
}

export interface RecommendationResponse {
  category: string;
  userPriority: string;
  lowestCostRecommendation: RecommendedItem;
  bestValueRecommendation: RecommendedItem;
  bestSpecRecommendation: RecommendedItem;
  allRankedItems: RecommendedItem[];
  algorithmSummary: string;
}

export interface DealLabCriteria {
  category?: string;
  maxBudget?: number;
  minRating?: number;
  minWarrantyMonths?: number;
  marketplace?: string;
  // Deprecated compatibility fields
  requiredRam?: string;
  requiredStorage?: string;
  preferredBrand?: string;
  maxDeliveryDays?: number;
  preferredMarketplace?: string;
  paymentCardType?: string;
}

export interface DealItem {
  id: number;
  productId: number;
  productTitle: string;
  category: string;
  marketplace: string;
  marketplaceName: string;
  price: number;
  rating?: number;
  warrantyMonths?: number;
  warrantyText?: string;
  valueScore: number;
  imageUrl?: string;
  productUrl?: string;
  inStock: boolean;
  priceDate?: string;
  failureReasons?: string[];
}

export interface QualifyingProduct {
  product: Product;
  bestListing: MarketplaceListing;
  effectiveCost: number;
  valueScore: number;
  qualificationSummary: string;
  matchedCriteria: string[];
  tradeOffAnalysis: string;
}

export interface NonQualifyingProduct {
  product: Product;
  currentEffectiveCost: number;
  failureReasons: string[];
  primaryLimitingFactor: string;
  howToQualify: string;
}

export interface RelaxationSuggestion {
  constraint?: string;
  constraintType: string;
  suggestionText: string;
  additionalProductsUnlocked: number;
  actionLabel?: string;
  simulatedCriteriaChange?: string;
}

export interface DealLabResult {
  criteria: DealLabCriteria;
  totalEvaluated: number;
  totalCandidates?: number;
  totalQualifying: number;
  totalNonQualifying: number;
  totalFailing?: number;
  qualifying: DealItem[];
  failing: DealItem[];
  bestMatchProduct?: QualifyingProduct;
  cheapestQualifyingProduct?: QualifyingProduct;
  bestSpecQualifyingProduct?: QualifyingProduct;
  qualifyingProducts?: QualifyingProduct[];
  nonQualifyingProducts?: NonQualifyingProduct[];
  relaxationSuggestions: RelaxationSuggestion[];
  synthesisSummary: string;
}

export interface UserPreferences {
  preferredCategory: string;
  minBudget: number;
  maxBudget: number;
  preferredBrand: string;
  preferredMarketplace: string;
  maxDeliveryDays: number;
  defaultSort: string;
}

export interface UserProfile {
  id: number;
  fullName: string;
  email: string;
  phoneNumber?: string;
  role: string;
  accountStatus: string;
  createdAt: string;
  preferences?: UserPreferences;
}

export interface PriceAlert {
  id: number;
  productId?: number;
  listingId?: number;
  productTitle: string;
  productImageUrl?: string;
  marketplace?: string;
  marketplaceName: string;
  targetPrice: number;
  currentPrice: number;
  priceAtCreation?: number;
  status?: string;
  isTriggered: boolean;
  isActive: boolean;
  createdAt: string;
  triggeredAt?: string;
}

export interface SavedProduct {
  id: number;
  product: Product;
  notes?: string;
  savedAt: string;
}

export interface PriceHistoryPoint {
  id: number;
  listingId: number;
  marketplaceName: string;
  marketplaceCode: string;
  observedPrice: number;
  recordedAt: string;
  isDemoData: boolean;
}

export interface PriceAnalytics {
  productId: number;
  productTitle: string;
  category: string;
  isDemoData: boolean;
  currentObservedPrice: number;
  previousObservedPrice: number;
  highestRecordedPrice: number;
  lowestRecordedPrice: number;
  priceChangePercentage: number;
  lastUpdated: string;
  historySeries: Record<string, PriceHistoryPoint[]>;
}

export interface CompareListing {
  marketplace: string;
  marketplaceName: string;
  price: number;
  warrantyText?: string;
  warrantyMonths?: number;
  rating?: number;
  inStock: boolean;
  productUrl?: string;
  imageUrl?: string;
  priceDate?: string;
  dataSource?: string;
  priceScore?: number;
  ratingScore?: number;
  warrantyScore?: number;
  perFactorScores?: Record<string, number>;
  valueScore?: number;
  rank?: number;
  excludedFromRanking?: boolean;
}

export interface BestMarketplace {
  marketplace: string;
  marketplaceName?: string;
  score?: number;
  valueScore?: number;
  price: number;
  warrantyMonths?: number;
  rating?: number;
  reason?: string;
}

export interface LowestPrice {
  marketplace: string;
  marketplaceName?: string;
  price: number;
  differenceFromBest?: number;
}

export interface ProductCompareResponseData {
  productId: number;
  productName: string;
  category: string;
  imageUrl?: string;
  listings: CompareListing[];
  best?: BestMarketplace;
  lowestPrice?: LowestPrice;
  extraCost?: number;
  explanation?: string;
  weightsUsed?: {
    wPrice?: number;
    wRating?: number;
    wWarranty?: number;
    ratingMethod?: string;
    [key: string]: any;
  };
  allOutOfStock?: boolean;
}

