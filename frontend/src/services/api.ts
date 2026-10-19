import {
  Product,
  MarketplaceListing,
  ComparisonData,
  RecommendationResponse,
  DealLabCriteria,
  DealLabResult,
  UserProfile,
  PriceAlert,
  SavedProduct,
  PriceAnalytics,
} from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const getAuthHeaders = (): HeadersInit => {
  const token = localStorage.getItem('pricehub_token');
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
};

export const api = {
  // Auth
  register: async (data: any) => {
    const res = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },

  login: async (data: any) => {
    const res = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },

  forgotPassword: async (email: string) => {
    const res = await fetch(`${API_BASE_URL}/auth/forgot-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email }),
    });
    return res.json();
  },

  resetPassword: async (data: any) => {
    const res = await fetch(`${API_BASE_URL}/auth/reset-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },

  // Products
  getProducts: async (params?: {
    category?: string;
    keyword?: string;
    marketplace?: string;
    minPrice?: number;
    maxPrice?: number;
    minRating?: number;
    sortBy?: string;
  }) => {
    const query = new URLSearchParams();
    if (params?.category) query.append('category', params.category);
    if (params?.keyword) query.append('keyword', params.keyword);
    if (params?.marketplace) query.append('marketplace', params.marketplace);
    if (params?.minPrice) query.append('minPrice', params.minPrice.toString());
    if (params?.maxPrice) query.append('maxPrice', params.maxPrice.toString());
    if (params?.minRating) query.append('minRating', params.minRating.toString());
    if (params?.sortBy) query.append('sortBy', params.sortBy);

    const res = await fetch(`${API_BASE_URL}/products?${query.toString()}`);
    return res.json();
  },

  getProductById: async (id: number, cardType?: string) => {
    const query = cardType ? `?cardType=${cardType}` : '';
    const res = await fetch(`${API_BASE_URL}/products/${id}${query}`);
    return res.json();
  },

  getTopDeals: async (category?: string, limit?: number) => {
    const query = new URLSearchParams();
    if (category) query.append('category', category);
    if (limit) query.append('limit', limit.toString());

    const res = await fetch(`${API_BASE_URL}/top-deals?${query.toString()}`);
    return res.json();
  },

  getDeals: async (params?: { category?: string; maxBudget?: number; minRating?: number; minWarrantyMonths?: number; marketplace?: string }) => {
    const query = new URLSearchParams();
    if (params?.category && params.category !== 'All') query.append('category', params.category);
    if (params?.maxBudget !== undefined && params.maxBudget > 0) query.append('maxBudget', params.maxBudget.toString());
    if (params?.minRating !== undefined && params.minRating > 0) query.append('minRating', params.minRating.toString());
    if (params?.minWarrantyMonths !== undefined && params.minWarrantyMonths > 0) query.append('minWarrantyMonths', params.minWarrantyMonths.toString());
    if (params?.marketplace && params.marketplace !== 'ALL') query.append('marketplace', params.marketplace);

    const res = await fetch(`${API_BASE_URL}/deals?${query.toString()}`);
    return res.json();
  },

  getCategories: async () => {
    const res = await fetch(`${API_BASE_URL}/products/categories`);
    return res.json();
  },

  // Comparison
  compareProduct: async (productId: number, wPrice?: number, wRating?: number, wWarranty?: number, ratingMethod?: string) => {
    const query = new URLSearchParams();
    query.append('product', productId.toString());
    if (wPrice !== undefined) query.append('wPrice', wPrice.toString());
    if (wRating !== undefined) query.append('wRating', wRating.toString());
    if (wWarranty !== undefined) query.append('wWarranty', wWarranty.toString());
    if (ratingMethod) query.append('ratingMethod', ratingMethod);

    const res = await fetch(`${API_BASE_URL}/compare?${query.toString()}`);
    return res.json();
  },

  compareProducts: async (productIds: number[], cardType?: string) => {
    const res = await fetch(`${API_BASE_URL}/comparison/compare`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productIds, cardType: cardType || 'ANY' }),
    });
    return res.json();
  },

  getSampleComparison: async () => {
    const res = await fetch(`${API_BASE_URL}/comparison/sample`);
    return res.json();
  },

  // Recommendations
  getRecommendations: async (data: {
    category?: string;
    maxBudget?: number;
    userPriority?: string;
    preferredMarketplace?: string;
    preferredBrand?: string;
    cardType?: string;
  }) => {
    const res = await fetch(`${API_BASE_URL}/recommendations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },

  // Personalized Deal Lab
  solveDealLab: async (criteria: DealLabCriteria) => {
    const res = await fetch(`${API_BASE_URL}/deallab/solve`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(criteria),
    });
    return res.json();
  },

  getDefaultDealLabCriteria: async () => {
    const res = await fetch(`${API_BASE_URL}/deallab/default-criteria`);
    return res.json();
  },

  // Price History & Alerts
  getProductPriceHistory: async (productId: number) => {
    const res = await fetch(`${API_BASE_URL}/products/${productId}/price-history`);
    return res.json();
  },

  createPriceAlert: async (data: { listingId?: number; productId?: number; targetPrice: number; marketplace?: string }) => {
    const res = await fetch(`${API_BASE_URL}/alerts`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    return res.json();
  },

  getUserPriceAlerts: async () => {
    const res = await fetch(`${API_BASE_URL}/alerts`, {
      headers: getAuthHeaders(),
    });
    return res.json();
  },

  deletePriceAlert: async (id: number) => {
    const res = await fetch(`${API_BASE_URL}/alerts/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    return res.json();
  },

  // Account & Wishlist
  getProfile: async () => {
    const res = await fetch(`${API_BASE_URL}/account/profile`, {
      headers: getAuthHeaders(),
    });
    return res.json();
  },

  updateProfile: async (data: any) => {
    const res = await fetch(`${API_BASE_URL}/account/profile`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    return res.json();
  },

  updatePreferences: async (data: any) => {
    const res = await fetch(`${API_BASE_URL}/account/preferences`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    return res.json();
  },

  getSavedProducts: async () => {
    const res = await fetch(`${API_BASE_URL}/saved`, {
      headers: getAuthHeaders(),
    });
    return res.json();
  },

  saveProduct: async (productId: number, notes?: string) => {
    const res = await fetch(`${API_BASE_URL}/saved/${productId}`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ notes }),
    });
    return res.json();
  },

  removeSavedProduct: async (productId: number) => {
    const res = await fetch(`${API_BASE_URL}/saved/${productId}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    return res.json();
  },
};
