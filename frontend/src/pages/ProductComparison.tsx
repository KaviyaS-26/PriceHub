import React, { useState, useEffect } from 'react';
import { ProductCompareResponseData, CompareListing, Product } from '../types';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import {
  Scale,
  Sparkles,
  ShieldCheck,
  Star,
  ExternalLink,
  Sliders,
  ArrowLeft,
  Search,
  TrendingDown,
  Award,
  AlertTriangle,
  CheckCircle2,
  XCircle,
  Clock,
  ArrowRight,
  Info,
  Bell,
  X,
} from 'lucide-react';

interface ProductComparisonProps {
  onNavigate: (page: string, params?: any) => void;
  initialParams?: { productId?: number };
}

export const ProductComparison: React.FC<ProductComparisonProps> = ({ onNavigate, initialParams }) => {
  // Product selection: null means no product selected (Empty State)
  const [selectedProductId, setSelectedProductId] = useState<number | null>(
    initialParams?.productId || null
  );

  // Catalog products for selection
  const [catalogProducts, setCatalogProducts] = useState<Product[]>([]);
  const [catalogLoading, setCatalogLoading] = useState<boolean>(false);
  const [catalogSearch, setCatalogSearch] = useState<string>('');
  const [catalogCategory, setCatalogCategory] = useState<string>('All');
  const [availableCategories, setAvailableCategories] = useState<string[]>([]);

  // Comparison API data
  const [compareData, setCompareData] = useState<ProductCompareResponseData | null>(null);
  const [compareLoading, setCompareLoading] = useState<boolean>(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Dynamic priority weights (sliders 0-100)
  const [priceWeight, setPriceWeight] = useState<number>(50);
  const [ratingWeight, setRatingWeight] = useState<number>(30);
  const [warrantyWeight, setWarrantyWeight] = useState<number>(20);

  // Price Alert Modal State
  const { isAuthenticated } = useAuth();
  const [showAlertModal, setShowAlertModal] = useState<boolean>(false);
  const [alertTargetPrice, setAlertTargetPrice] = useState<number>(0);
  const [alertMarketplace, setAlertMarketplace] = useState<string>('ALL');
  const [alertLoading, setAlertLoading] = useState<boolean>(false);
  const [alertSuccessMsg, setAlertSuccessMsg] = useState<string | null>(null);
  const [alertErrorMsg, setAlertErrorMsg] = useState<string | null>(null);

  // Update selected product if initialParams changes
  useEffect(() => {
    if (initialParams?.productId) {
      setSelectedProductId(initialParams.productId);
    }
  }, [initialParams?.productId]);

  // Load catalog and categories for product selection
  useEffect(() => {
    const loadCatalog = async () => {
      setCatalogLoading(true);
      try {
        const [prodRes, catRes] = await Promise.all([
          api.getProducts({}),
          api.getCategories(),
        ]);
        if (prodRes.success && prodRes.data) {
          setCatalogProducts(prodRes.data);
        }
        if (catRes.success && catRes.data) {
          setAvailableCategories(catRes.data);
        }
      } catch (err) {
        console.error('Failed to load products catalog', err);
      } finally {
        setCatalogLoading(false);
      }
    };
    loadCatalog();
  }, []);

  // Fetch comparison from GET /api/compare whenever selectedProductId or weights change
  useEffect(() => {
    if (!selectedProductId) {
      setCompareData(null);
      return;
    }

    let isMounted = true;
    const fetchCompare = async () => {
      setCompareLoading(true);
      setErrorMsg(null);
      try {
        const wP = priceWeight / 100;
        const wR = ratingWeight / 100;
        const wW = warrantyWeight / 100;

        const res = await api.compareProduct(selectedProductId, wP, wR, wW, 'ABSOLUTE');
        if (!isMounted) return;

        if (res.success && res.data) {
          setCompareData(res.data);
        } else {
          setErrorMsg(res.message || 'Unable to compare this product across marketplaces.');
        }
      } catch (err) {
        if (!isMounted) return;
        console.error('Error fetching comparison', err);
        setErrorMsg('Failed to connect to comparison engine.');
      } finally {
        if (isMounted) setCompareLoading(false);
      }
    };

    fetchCompare();
    return () => {
      isMounted = false;
    };
  }, [selectedProductId, priceWeight, ratingWeight, warrantyWeight]);

  // Helper to format marketplace code to clean name
  const formatMarketplaceName = (code?: string): string => {
    if (!code) return 'N/A';
    const upper = code.trim().toUpperCase();
    if (upper === 'AMAZON') return 'Amazon';
    if (upper === 'FLIPKART') return 'Flipkart';
    if (upper === 'MEESHO') return 'Meesho';
    return code.charAt(0).toUpperCase() + code.slice(1).toLowerCase();
  };

  // Sliders: ensure weights always sum to exactly 100%
  const handlePriceChange = (val: number) => {
    const newPrice = Math.max(0, Math.min(100, Math.round(val)));
    const remaining = 100 - newPrice;
    const otherSum = ratingWeight + warrantyWeight;
    let newRating: number;
    let newWarranty: number;
    if (otherSum > 0) {
      newRating = Math.round((ratingWeight / otherSum) * remaining);
      newWarranty = remaining - newRating;
    } else {
      newRating = Math.round(remaining / 2);
      newWarranty = remaining - newRating;
    }
    setPriceWeight(newPrice);
    setRatingWeight(newRating);
    setWarrantyWeight(newWarranty);
  };

  const handleRatingChange = (val: number) => {
    const newRating = Math.max(0, Math.min(100, Math.round(val)));
    const remaining = 100 - newRating;
    const otherSum = priceWeight + warrantyWeight;
    let newPrice: number;
    let newWarranty: number;
    if (otherSum > 0) {
      newPrice = Math.round((priceWeight / otherSum) * remaining);
      newWarranty = remaining - newPrice;
    } else {
      newPrice = Math.round(remaining / 2);
      newWarranty = remaining - newPrice;
    }
    setPriceWeight(newPrice);
    setRatingWeight(newRating);
    setWarrantyWeight(newWarranty);
  };

  const handleWarrantyChange = (val: number) => {
    const newWarranty = Math.max(0, Math.min(100, Math.round(val)));
    const remaining = 100 - newWarranty;
    const otherSum = priceWeight + ratingWeight;
    let newPrice: number;
    let newRating: number;
    if (otherSum > 0) {
      newPrice = Math.round((priceWeight / otherSum) * remaining);
      newRating = remaining - newPrice;
    } else {
      newPrice = Math.round(remaining / 2);
      newRating = remaining - newPrice;
    }
    setPriceWeight(newPrice);
    setRatingWeight(newRating);
    setWarrantyWeight(newWarranty);
  };

  // Preset weight handlers
  const applyPreset = (p: number, r: number, w: number) => {
    setPriceWeight(p);
    setRatingWeight(r);
    setWarrantyWeight(w);
  };

  // Filter catalog products for empty state
  const filteredCatalog = catalogProducts.filter((prod) => {
    const matchSearch =
      catalogSearch.trim() === '' ||
      prod.title.toLowerCase().includes(catalogSearch.toLowerCase()) ||
      (prod.brand && prod.brand.toLowerCase().includes(catalogSearch.toLowerCase())) ||
      (prod.category && prod.category.toLowerCase().includes(catalogSearch.toLowerCase()));

    const matchCategory =
      catalogCategory === 'All' ||
      (prod.category && prod.category.toLowerCase() === catalogCategory.toLowerCase());

    return matchSearch && matchCategory;
  });

  // ==========================================
  // VIEW 1: EMPTY STATE (No product selected)
  // ==========================================
  if (!selectedProductId) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8 animate-in fade-in duration-300">
        {/* Header Banner */}
        <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 rounded-3xl p-8 sm:p-10 text-white shadow-xl relative overflow-hidden border border-purple-500/20">
          <div className="absolute top-0 right-0 w-80 h-80 bg-purple-500/10 rounded-full blur-3xl pointer-events-none"></div>
          <div className="relative z-10 max-w-3xl space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs font-semibold text-purple-200">
              <Scale className="w-3.5 h-3.5 text-teal-300" />
              <span>Multi-Marketplace Comparison</span>
            </div>
            <h1 className="text-2xl sm:text-4xl font-extrabold tracking-tight brand-font leading-tight">
              Select a product to compare it on Amazon, Flipkart and Meesho
            </h1>
            <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
              PriceHub compares ONE selected product across all 3 marketplaces using collected data.
              The best marketplace is calculated dynamically on every request from price, warranty and rating.
            </p>
            <div className="flex flex-wrap items-center gap-3 pt-2 text-xs">
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-400/20 text-amber-200 border border-amber-300/30 font-semibold">
                <Info className="w-3.5 h-3.5" />
                Similar products, not identical models
              </span>
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-purple-400/20 text-purple-200 border border-purple-300/30 font-semibold">
                <Clock className="w-3.5 h-3.5" />
                Collected data, 15-17 Oct 2026
              </span>
            </div>
          </div>
        </div>

        {/* Filter & Search Bar */}
        <div className="bg-white rounded-2xl p-4 sm:p-5 shadow-sm border border-slate-200/80 space-y-4">
          <div className="flex flex-col md:flex-row gap-3 items-center justify-between">
            {/* Search Input */}
            <div className="relative flex-1 w-full">
              <Search className="w-4 h-4 text-purple-600 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={catalogSearch}
                onChange={(e) => setCatalogSearch(e.target.value)}
                placeholder="Search products by name or category (e.g., Neckband, Sling Bag, T-Shirt, Cycle)..."
                className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs sm:text-sm focus:outline-none focus:border-purple-500 font-medium text-slate-900 placeholder:text-slate-400"
              />
            </div>

            {/* Category Select for Mobile */}
            <div className="md:hidden w-full">
              <select
                value={catalogCategory}
                onChange={(e) => setCatalogCategory(e.target.value)}
                className="w-full py-2.5 px-3 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold text-slate-800"
              >
                <option value="All">All Categories</option>
                {availableCategories.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Category Chips (Desktop) */}
          <div className="hidden md:flex flex-wrap items-center gap-2 pt-1 border-t border-slate-100">
            <button
              onClick={() => setCatalogCategory('All')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                catalogCategory === 'All'
                  ? 'bg-purple-600 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-purple-50 hover:text-purple-700'
              }`}
            >
              All Categories
            </button>
            {availableCategories.map((cat) => (
              <button
                key={cat}
                onClick={() => setCatalogCategory(cat)}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                  catalogCategory === cat
                    ? 'bg-purple-600 text-white shadow-xs'
                    : 'bg-slate-100 text-slate-600 hover:bg-purple-50 hover:text-purple-700'
                }`}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>

        {/* Product Selection Grid */}
        <div>
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-bold text-slate-700 uppercase tracking-wider">
              Available Products ({filteredCatalog.length})
            </h3>
            <span className="text-xs text-slate-500">
              Click any product to compare on Amazon, Flipkart &amp; Meesho
            </span>
          </div>

          {catalogLoading ? (
            <div className="py-24 flex flex-col items-center justify-center gap-3 text-slate-400">
              <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
              <span className="text-xs font-medium">Loading catalog products...</span>
            </div>
          ) : filteredCatalog.length === 0 ? (
            <div className="bg-white rounded-3xl p-12 text-center border border-slate-200 space-y-3">
              <Scale className="w-10 h-10 text-slate-300 mx-auto" />
              <h4 className="text-sm font-bold text-slate-800">No matching products found</h4>
              <p className="text-xs text-slate-500">
                Try searching for "Neckband", "T-Shirt", "Sling Bag" or change the category filter.
              </p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
              {filteredCatalog.map((product) => (
                <div
                  key={product.id}
                  onClick={() => setSelectedProductId(product.id)}
                  className="group bg-white rounded-2xl border border-slate-200/90 hover:border-purple-400 shadow-xs hover:shadow-xl hover:-translate-y-1 transition-all duration-300 p-4 flex flex-col justify-between cursor-pointer"
                >
                  <div className="space-y-3">
                    <div className="h-44 bg-slate-50 rounded-xl p-3 border border-slate-100 flex items-center justify-center overflow-hidden">
                      <img
                        src={product.imageUrl}
                        alt={product.title}
                        className="max-h-36 max-w-full object-contain group-hover:scale-105 transition-transform duration-300"
                      />
                    </div>

                    <div>
                      <div className="flex items-center justify-between gap-1 mb-1">
                        <span className="text-[10px] font-extrabold uppercase text-purple-700 bg-purple-50 px-2 py-0.5 rounded">
                          {product.category || 'Product'}
                        </span>
                        {product.ratingAverage != null && product.ratingAverage > 0 ? (
                          <span className="text-[11px] font-bold text-amber-700 flex items-center gap-0.5">
                            <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
                            {product.ratingAverage}
                          </span>
                        ) : null}
                      </div>
                      <h4 className="font-bold text-sm text-slate-900 group-hover:text-purple-700 transition-colors line-clamp-2">
                        {product.title}
                      </h4>
                    </div>
                  </div>

                  <div className="pt-3 border-t border-slate-100 mt-3 flex items-center justify-between">
                    <div>
                      <span className="text-[10px] text-slate-400 block">Lowest Price</span>
                      <span className="text-base font-extrabold text-slate-900">
                        ₹{(product.lowestEffectivePrice || product.basePrice || 0).toLocaleString()}
                      </span>
                    </div>

                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        setSelectedProductId(product.id);
                      }}
                      className="px-3 py-1.5 bg-purple-50 hover:bg-purple-600 text-purple-700 hover:text-white rounded-xl text-xs font-bold transition-all border border-purple-200 hover:border-purple-600 flex items-center gap-1"
                    >
                      <span>Compare</span>
                      <ArrowRight className="w-3 h-3" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  }

  // ==========================================
  // VIEW 2: ACTIVE PRODUCT COMPARISON
  // ==========================================
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8 animate-in fade-in duration-300">
      {/* Top Navigation & Product Title Bar */}
      <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-200/80 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div className="space-y-1.5">
          <button
            onClick={() => setSelectedProductId(null)}
            className="inline-flex items-center gap-1.5 text-xs font-bold text-purple-700 hover:text-purple-900 hover:underline mb-1"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Choose a different product</span>
          </button>

          <div className="flex flex-wrap items-center gap-2">
            <span className="text-xs font-extrabold uppercase tracking-wider text-purple-700 bg-purple-50 px-2.5 py-0.5 rounded-full border border-purple-200">
              {compareData?.category || 'Category'}
            </span>
            <span className="text-xs font-semibold text-amber-800 bg-amber-50 px-2.5 py-0.5 rounded-full border border-amber-200">
              Similar products, not identical models
            </span>
          </div>

          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 brand-font">
            {compareData?.productName || 'Comparing Product'}
          </h1>
          <p className="text-xs text-slate-500">
            Real-time algorithmic comparison across Amazon, Flipkart, and Meesho with dynamic priority weighting.
          </p>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <button
            onClick={() => {
              if (!isAuthenticated) {
                alert('Please log in to set a price alert.');
                onNavigate('login');
                return;
              }
              setAlertSuccessMsg(null);
              setAlertErrorMsg(null);
              if (compareData?.lowestPrice) {
                setAlertTargetPrice(Math.round(compareData.lowestPrice.price * 0.95));
              } else {
                setAlertTargetPrice(500);
              }
              setShowAlertModal(true);
            }}
            className="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs flex items-center gap-1.5"
          >
            <Bell className="w-3.5 h-3.5" />
            <span>Set price alert</span>
          </button>

          <button
            onClick={() => setSelectedProductId(null)}
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition-colors shrink-0 flex items-center gap-1.5"
          >
            <Search className="w-3.5 h-3.5" />
            <span>Switch Product</span>
          </button>
        </div>
      </div>

      {/* Priority Weighting Sliders */}
      <div className="bg-gradient-to-r from-purple-50 via-indigo-50 to-pink-50 rounded-3xl p-6 border border-purple-200/80 shadow-xs space-y-5">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-purple-200/60 pb-3">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-purple-600 text-white flex items-center justify-center font-bold">
              <Sliders className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-sm font-extrabold text-purple-950">
                Priority Sliders (Best Value Engine)
              </h3>
              <p className="text-[11px] text-purple-800">
                Adjust sliders to change factor importance. The backend re-evaluates the winner immediately!
              </p>
            </div>
          </div>

          {/* Quick Preset Buttons */}
          <div className="flex flex-wrap items-center gap-1.5">
            <span className="text-[10px] font-bold uppercase text-purple-700 mr-1">Presets:</span>
            <button
              onClick={() => applyPreset(50, 30, 20)}
              className={`px-2.5 py-1 rounded-lg text-[11px] font-bold transition-all ${
                priceWeight === 50 && ratingWeight === 30 && warrantyWeight === 20
                  ? 'bg-purple-700 text-white shadow-xs'
                  : 'bg-white text-purple-900 border border-purple-200 hover:bg-purple-100'
              }`}
            >
              Default (50/30/20)
            </button>
            <button
              onClick={() => applyPreset(80, 10, 10)}
              className={`px-2.5 py-1 rounded-lg text-[11px] font-bold transition-all ${
                priceWeight === 80 && ratingWeight === 10 && warrantyWeight === 10
                  ? 'bg-purple-700 text-white shadow-xs'
                  : 'bg-white text-purple-900 border border-purple-200 hover:bg-purple-100'
              }`}
            >
              Price First (80/10/10)
            </button>
            <button
              onClick={() => applyPreset(20, 60, 20)}
              className={`px-2.5 py-1 rounded-lg text-[11px] font-bold transition-all ${
                priceWeight === 20 && ratingWeight === 60 && warrantyWeight === 20
                  ? 'bg-purple-700 text-white shadow-xs'
                  : 'bg-white text-purple-900 border border-purple-200 hover:bg-purple-100'
              }`}
            >
              Rating First (20/60/20)
            </button>
            <button
              onClick={() => applyPreset(20, 20, 60)}
              className={`px-2.5 py-1 rounded-lg text-[11px] font-bold transition-all ${
                priceWeight === 20 && ratingWeight === 20 && warrantyWeight === 60
                  ? 'bg-purple-700 text-white shadow-xs'
                  : 'bg-white text-purple-900 border border-purple-200 hover:bg-purple-100'
              }`}
            >
              Warranty First (20/20/60)
            </button>
          </div>
        </div>

        {/* 3 Range Sliders */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Price Priority Slider */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-4 border border-purple-200 space-y-2">
            <div className="flex items-center justify-between text-xs font-bold text-slate-800">
              <span className="flex items-center gap-1.5 text-purple-900">
                <TrendingDown className="w-3.5 h-3.5 text-purple-600" /> Price Priority
              </span>
              <span className="bg-purple-100 text-purple-900 px-2 py-0.5 rounded-full font-mono text-xs">
                {priceWeight}%
              </span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              step="5"
              value={priceWeight}
              onChange={(e) => handlePriceChange(Number(e.target.value))}
              className="w-full accent-purple-600 cursor-pointer"
            />
            <div className="flex justify-between text-[10px] text-slate-400 font-semibold">
              <span>Low (0%)</span>
              <span>Balanced (50%)</span>
              <span>Max (100%)</span>
            </div>
          </div>

          {/* Rating Priority Slider */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-4 border border-purple-200 space-y-2">
            <div className="flex items-center justify-between text-xs font-bold text-slate-800">
              <span className="flex items-center gap-1.5 text-amber-900">
                <Star className="w-3.5 h-3.5 text-amber-500 fill-amber-500" /> Rating Priority
              </span>
              <span className="bg-amber-100 text-amber-900 px-2 py-0.5 rounded-full font-mono text-xs">
                {ratingWeight}%
              </span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              step="5"
              value={ratingWeight}
              onChange={(e) => handleRatingChange(Number(e.target.value))}
              className="w-full accent-amber-500 cursor-pointer"
            />
            <div className="flex justify-between text-[10px] text-slate-400 font-semibold">
              <span>Low (0%)</span>
              <span>Balanced (30%)</span>
              <span>Max (100%)</span>
            </div>
          </div>

          {/* Warranty Priority Slider */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-4 border border-purple-200 space-y-2">
            <div className="flex items-center justify-between text-xs font-bold text-slate-800">
              <span className="flex items-center gap-1.5 text-teal-900">
                <ShieldCheck className="w-3.5 h-3.5 text-teal-600" /> Warranty Priority
              </span>
              <span className="bg-teal-100 text-teal-900 px-2 py-0.5 rounded-full font-mono text-xs">
                {warrantyWeight}%
              </span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              step="5"
              value={warrantyWeight}
              onChange={(e) => handleWarrantyChange(Number(e.target.value))}
              className="w-full accent-teal-600 cursor-pointer"
            />
            <div className="flex justify-between text-[10px] text-slate-400 font-semibold">
              <span>Low (0%)</span>
              <span>Balanced (20%)</span>
              <span>Max (100%)</span>
            </div>
          </div>
        </div>
      </div>

      {/* Loading state or Error */}
      {compareLoading && !compareData && (
        <div className="py-20 flex flex-col items-center justify-center gap-3 text-slate-400">
          <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
          <span className="text-xs font-medium">Calculating dynamic multi-marketplace value scores...</span>
        </div>
      )}

      {errorMsg && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-xs text-rose-800 flex items-center gap-2">
          <AlertTriangle className="w-4 h-4 text-rose-600 shrink-0" />
          <span>{errorMsg}</span>
        </div>
      )}

      {/* Comparison Content */}
      {compareData && (
        <div className="space-y-8">
          {/* Best Value Winner & Explanation Callout */}
          <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 text-white rounded-3xl p-6 sm:p-8 shadow-xl border border-purple-500/30 space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-white/10 pb-4">
              <div className="flex items-center gap-3">
                <span className="px-3.5 py-1.5 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-400/40 text-xs font-extrabold flex items-center gap-1.5">
                  <Award className="w-4 h-4 text-emerald-400" />
                  Best Value: {formatMarketplaceName(compareData.best?.marketplace)}
                </span>
                <span className="text-xs text-purple-200 font-mono">
                  Score: {compareData.best?.valueScore != null ? compareData.best.valueScore.toFixed(1) : (compareData.best?.score != null ? compareData.best.score.toFixed(1) : 'N/A')} / 100
                </span>
              </div>

              {compareData.lowestPrice ? (
                <div className="flex items-center gap-2 text-xs">
                  <span className="text-slate-400">Lowest Price:</span>
                  <span className="font-extrabold text-teal-300">
                    {formatMarketplaceName(compareData.lowestPrice.marketplace)} (₹{compareData.lowestPrice.price?.toLocaleString()})
                  </span>
                </div>
              ) : null}
            </div>

            {/* Backend Algorithm Explanation */}
            <p className="text-xs sm:text-sm text-slate-200 leading-relaxed font-medium">
              💡 {compareData.explanation}
            </p>

            {/* Extra cost callout if lowest price differs from best value */}
            {(() => {
              const bestMkt = compareData.best?.marketplace?.toUpperCase();
              const lowestMkt = compareData.lowestPrice?.marketplace?.toUpperCase();
              const extraCost = compareData.extraCost ?? 0;
              const isBestDifferentFromCheapest = Boolean(
                bestMkt && lowestMkt && bestMkt !== lowestMkt && extraCost > 0
              );

              if (!isBestDifferentFromCheapest) return null;

              const bestName = formatMarketplaceName(bestMkt);
              const lowestName = formatMarketplaceName(lowestMkt);

              const bestListing = compareData.listings.find(
                (l) => l.marketplace?.toUpperCase() === bestMkt
              );
              const lowestListing = compareData.listings.find(
                (l) => l.marketplace?.toUpperCase() === lowestMkt
              );

              const hasBetterWarranty =
                (bestListing?.warrantyMonths ?? 0) > (lowestListing?.warrantyMonths ?? 0);
              const hasBetterRating =
                (bestListing?.rating ?? 0) > (lowestListing?.rating ?? 0);

              let advantage = 'superior warranty and customer ratings';
              if (hasBetterWarranty && hasBetterRating) {
                advantage = 'a longer warranty and a better rating';
              } else if (hasBetterWarranty) {
                advantage = 'a longer warranty';
              } else if (hasBetterRating) {
                advantage = 'a better customer rating';
              }

              return (
                <div className="p-3 bg-white/10 rounded-xl border border-white/15 text-xs text-slate-200 flex items-center justify-between">
                  <span>
                    <b>Tradeoff Analysis:</b> Paying ₹{Math.round(extraCost).toLocaleString()} extra on {bestName} provides {advantage} than {lowestName}.
                  </span>
                </div>
              );
            })()}
          </div>

          {/* Marketplace Listing Cards (3 Marketplaces) */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
                Marketplace Offerings (Amazon, Flipkart &amp; Meesho)
              </h3>
              <span className="text-xs text-slate-500">
                Sorted by dynamic value score (out-of-stock excluded from ranking)
              </span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {compareData.listings.map((listing) => {
                const isBest = compareData.best?.marketplace?.toUpperCase() === listing.marketplace?.toUpperCase();
                const isLowest = compareData.lowestPrice?.marketplace?.toUpperCase() === listing.marketplace?.toUpperCase();
                const isOOS = !listing.inStock;

                return (
                  <div
                    key={listing.marketplace}
                    className={`bg-white rounded-3xl p-6 border shadow-sm relative flex flex-col justify-between transition-all duration-300 ${
                      isBest
                        ? 'border-purple-500 ring-2 ring-purple-400/30 shadow-lg'
                        : isOOS
                        ? 'border-slate-200 opacity-70 bg-slate-50/50'
                        : 'border-slate-200 hover:border-slate-300'
                    }`}
                  >
                    {/* Header Badges */}
                    <div className="space-y-3">
                      <div className="flex items-center justify-between gap-2">
                        <div className="flex items-center gap-2">
                          <span
                            className={`w-3 h-3 rounded-full ${
                              listing.marketplace.toUpperCase() === 'AMAZON'
                                ? 'bg-amber-400'
                                : listing.marketplace.toUpperCase() === 'FLIPKART'
                                ? 'bg-blue-400'
                                : 'bg-pink-500'
                            }`}
                          />
                          <h4 className="font-extrabold text-base text-slate-900">
                            {listing.marketplaceName}
                          </h4>
                        </div>

                        {/* Rank Badge */}
                        {listing.rank ? (
                          <span className={`text-xs font-extrabold px-2.5 py-0.5 rounded-full ${
                            listing.rank === 1
                              ? 'bg-purple-100 text-purple-900 border border-purple-300'
                              : 'bg-slate-100 text-slate-700'
                          }`}>
                            Rank #{listing.rank}
                          </span>
                        ) : (
                          <span className="text-[10px] font-bold text-rose-700 bg-rose-50 border border-rose-200 px-2 py-0.5 rounded-full">
                            Out of Stock
                          </span>
                        )}
                      </div>

                      {/* Product Image */}
                      <div className="h-44 bg-slate-50 rounded-2xl p-3 border border-slate-100 flex items-center justify-center overflow-hidden">
                        <img
                          src={listing.imageUrl || compareData.imageUrl || ''}
                          alt={`${listing.marketplaceName} listing`}
                          className="max-h-36 max-w-full object-contain"
                        />
                      </div>

                      {/* Badges Container */}
                      <div className="flex flex-wrap items-center gap-1.5 min-h-[26px]">
                        {isBest && (
                          <span className="px-2.5 py-0.5 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-md text-[10px] font-extrabold shadow-2xs flex items-center gap-1">
                            <Award className="w-3 h-3" /> Best Value
                          </span>
                        )}
                        {isLowest && (
                          <span className="px-2.5 py-0.5 bg-teal-50 text-teal-800 border border-teal-300 rounded-md text-[10px] font-extrabold flex items-center gap-1">
                            <TrendingDown className="w-3 h-3 text-teal-600" /> Lowest Price
                          </span>
                        )}
                        {isOOS && (
                          <span className="px-2.5 py-0.5 bg-rose-50 text-rose-800 border border-rose-200 rounded-md text-[10px] font-bold">
                            Excluded from Ranking
                          </span>
                        )}
                      </div>

                      {/* Price & Rating */}
                      <div className="pt-2 border-t border-slate-100">
                        <div className="flex items-baseline justify-between">
                          <div>
                            <span className="text-[10px] text-slate-400 block font-medium">Listing Price</span>
                            <span className="text-2xl font-black text-slate-900">
                              ₹{listing.price != null ? listing.price.toLocaleString() : 'N/A'}
                            </span>
                          </div>

                          <div className="text-right">
                            <span className="text-[10px] text-slate-400 block font-medium">Customer Rating</span>
                            <span className="text-sm font-bold text-amber-700 flex items-center justify-end gap-1">
                              <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                              {listing.rating != null ? `${listing.rating} / 5` : 'No rating'}
                            </span>
                          </div>
                        </div>
                      </div>

                      {/* Warranty & Stock Info */}
                      <div className="grid grid-cols-2 gap-2 text-xs pt-1">
                        <div className="p-2 bg-slate-50 rounded-xl border border-slate-100">
                          <span className="text-[10px] text-slate-400 block">Warranty</span>
                          <span className="font-bold text-slate-800">
                            {listing.warrantyText || (listing.warrantyMonths != null ? `${listing.warrantyMonths} Months` : '0 Months')}
                          </span>
                        </div>
                        <div className="p-2 bg-slate-50 rounded-xl border border-slate-100">
                          <span className="text-[10px] text-slate-400 block">Availability</span>
                          <span className={`font-bold ${listing.inStock ? 'text-emerald-700' : 'text-rose-600'}`}>
                            {listing.inStock ? 'In Stock' : 'Out of Stock'}
                          </span>
                        </div>
                      </div>

                      {/* Per-Factor Score Breakdown */}
                      <div className="p-3 bg-purple-50/60 rounded-2xl border border-purple-100 space-y-2 text-xs">
                        <div className="flex items-center justify-between text-[11px] font-bold text-purple-950">
                          <span>Overall Value Score:</span>
                          <span className="text-sm font-extrabold text-purple-700">
                            {listing.valueScore != null ? listing.valueScore.toFixed(1) : 'N/A'} / 100
                          </span>
                        </div>

                        {/* Progress Bar */}
                        <div className="w-full bg-slate-200 h-2 rounded-full overflow-hidden">
                          <div
                            className={`h-full transition-all duration-500 ${
                              isBest
                                ? 'bg-gradient-to-r from-purple-600 to-indigo-600'
                                : 'bg-purple-400'
                            }`}
                            style={{ width: `${Math.min(100, Math.max(0, listing.valueScore || 0))}%` }}
                          />
                        </div>

                        {/* Factor Contributions */}
                        <div className="grid grid-cols-3 gap-1 pt-1 text-[10px] text-slate-600">
                          <div>
                            <span className="block text-slate-400">Price Score:</span>
                            <span className="font-bold text-slate-800">
                              {listing.priceScore != null ? (listing.priceScore * 100).toFixed(1) : 'N/A'}
                            </span>
                          </div>
                          <div>
                            <span className="block text-slate-400">Rating Score:</span>
                            <span className="font-bold text-slate-800">
                              {listing.ratingScore != null ? (listing.ratingScore * 100).toFixed(1) : 'N/A'}
                            </span>
                          </div>
                          <div>
                            <span className="block text-slate-400">Warranty Score:</span>
                            <span className="font-bold text-slate-800">
                              {listing.warrantyScore != null ? (listing.warrantyScore * 100).toFixed(1) : 'N/A'}
                            </span>
                          </div>
                        </div>
                      </div>

                      <div className="text-[10px] text-slate-400 flex items-center justify-between pt-1">
                        <span>Collected data, {listing.priceDate || '15-17 Oct 2026'}</span>
                      </div>
                    </div>

                    {/* Action Button */}
                    <div className="pt-4 mt-2">
                      <a
                        href={listing.productUrl || '#'}
                        target="_blank"
                        rel="noopener noreferrer"
                        className={`w-full py-2.5 px-4 rounded-xl text-xs font-bold transition-all shadow-xs flex items-center justify-center gap-1.5 ${
                          isBest
                            ? 'bg-purple-600 hover:bg-purple-700 text-white'
                            : 'bg-slate-900 hover:bg-slate-800 text-white'
                        }`}
                      >
                        <span>Open on {listing.marketplaceName}</span>
                        <ExternalLink className="w-3.5 h-3.5" />
                      </a>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Factor Comparison Table */}
          <div className="bg-white rounded-3xl border border-slate-200/90 shadow-sm overflow-hidden">
            <div className="p-5 bg-slate-50/80 border-b border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div>
                <h3 className="text-sm font-extrabold text-slate-900 uppercase tracking-wider">
                  Side-by-Side Factor Analysis Table
                </h3>
                <p className="text-xs text-slate-500">
                  Direct normalized comparison of all evaluated attributes from collected public data.
                </p>
              </div>
              <span className="text-[11px] font-bold text-purple-700 bg-purple-50 px-2.5 py-1 rounded-full border border-purple-200">
                Algorithmically Computed
              </span>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="bg-slate-100/60 border-b border-slate-200 text-slate-600 font-bold uppercase text-[10px] tracking-wider">
                    <th className="py-3 px-5 w-1/4">Factor / Attribute</th>
                    {compareData.listings.map((l) => (
                      <th key={l.marketplace} className="py-3 px-5">
                        <div className="flex items-center gap-1.5">
                          <span>{l.marketplaceName}</span>
                          {compareData.best?.marketplace?.toUpperCase() === l.marketplace.toUpperCase() && (
                            <span className="bg-purple-600 text-white text-[9px] px-1.5 py-0.2 rounded font-extrabold">
                              Winner
                            </span>
                          )}
                        </div>
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  {/* Price */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Listing Price (₹)
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 font-bold text-slate-900">
                        ₹{l.price != null ? l.price.toLocaleString() : 'N/A'}
                        {compareData.lowestPrice?.marketplace?.toUpperCase() === l.marketplace.toUpperCase() && (
                          <span className="ml-2 text-[10px] text-teal-700 font-bold bg-teal-50 px-1.5 py-0.5 rounded">
                            Cheapest
                          </span>
                        )}
                      </td>
                    ))}
                  </tr>

                  {/* Warranty */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Warranty
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 text-slate-800">
                        {l.warrantyText || (l.warrantyMonths != null ? `${l.warrantyMonths} Months` : '0 Months')}
                      </td>
                    ))}
                  </tr>

                  {/* Rating */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Customer Rating
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 text-slate-800">
                        {l.rating != null ? `⭐ ${l.rating} / 5` : 'No rating'}
                      </td>
                    ))}
                  </tr>

                  {/* Stock Status */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Stock Availability
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 font-bold">
                        <span className={l.inStock ? 'text-emerald-700' : 'text-rose-600'}>
                          {l.inStock ? 'In Stock' : 'Out of Stock'}
                        </span>
                      </td>
                    ))}
                  </tr>

                  {/* Price Score */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Price Score ({priceWeight}% Weight)
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 text-slate-800 font-mono">
                        {l.priceScore != null ? `${(l.priceScore * 100).toFixed(1)} / 100` : 'N/A'}
                      </td>
                    ))}
                  </tr>

                  {/* Rating Score */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Rating Score ({ratingWeight}% Weight)
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 text-slate-800 font-mono">
                        {l.ratingScore != null ? `${(l.ratingScore * 100).toFixed(1)} / 100` : 'N/A'}
                      </td>
                    ))}
                  </tr>

                  {/* Warranty Score */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Warranty Score ({warrantyWeight}% Weight)
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 text-slate-800 font-mono">
                        {l.warrantyScore != null ? `${(l.warrantyScore * 100).toFixed(1)} / 100` : 'N/A'}
                      </td>
                    ))}
                  </tr>

                  {/* Overall Value Score */}
                  <tr className="hover:bg-purple-50/30 transition-colors bg-purple-50/20">
                    <td className="py-3 px-5 font-black text-purple-900 bg-purple-100/40 uppercase">
                      Final Value Score
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 font-black text-purple-700 text-sm">
                        {l.valueScore != null ? l.valueScore.toFixed(1) : 'N/A'}
                      </td>
                    ))}
                  </tr>

                  {/* Final Rank */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Final Rank
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 font-bold text-slate-800">
                        {l.rank ? `#${l.rank}` : 'Excluded (Out of stock)'}
                      </td>
                    ))}
                  </tr>

                  {/* Price Date */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Data Date
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5 text-slate-500 font-mono text-[11px]">
                        {l.priceDate || '15-17 Oct 2026'}
                      </td>
                    ))}
                  </tr>

                  {/* Store Link */}
                  <tr className="hover:bg-purple-50/30 transition-colors">
                    <td className="py-3 px-5 font-bold text-slate-700 bg-slate-50/40">
                      Store Link
                    </td>
                    {compareData.listings.map((l) => (
                      <td key={l.marketplace} className="py-3 px-5">
                        <a
                          href={l.productUrl || '#'}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="text-purple-700 hover:text-purple-900 font-bold inline-flex items-center gap-1 hover:underline"
                        >
                          <span>Open on {l.marketplaceName}</span>
                          <ExternalLink className="w-3 h-3" />
                        </a>
                      </td>
                    ))}
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          {/* Bottom Note */}
          <div className="p-4 bg-amber-50 rounded-2xl border border-amber-200 text-xs text-amber-900 flex items-center gap-2.5">
            <Info className="w-4 h-4 text-amber-600 shrink-0" />
            <span>
              <b>Note:</b> Similar products, not identical models. Prices collected from public listings (15-17 Oct 2026). The best marketplace is calculated on every request from price, warranty and rating by the backend. Nothing "recommended" is ever stored.
            </span>
          </div>
        </div>
      )}

      {/* Price Alert Modal */}
      {showAlertModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in-95 duration-200">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-purple-100 text-purple-700 flex items-center justify-center font-bold">
                  <Bell className="w-4 h-4" />
                </div>
                <h3 className="text-sm font-extrabold text-slate-900">Set Price Alert</h3>
              </div>
              <button
                onClick={() => setShowAlertModal(false)}
                className="p-1 rounded-full text-slate-400 hover:text-slate-600 hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div>
              <p className="text-xs text-slate-700 font-bold mb-0.5">{compareData?.productName}</p>
              <p className="text-[11px] text-slate-500">
                Current lowest: <b className="text-slate-900">₹{compareData?.lowestPrice?.price?.toLocaleString() ?? 'N/A'}</b>
              </p>
            </div>

            {alertSuccessMsg && (
              <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-800 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
                <span>{alertSuccessMsg}</span>
              </div>
            )}

            {alertErrorMsg && (
              <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-800 flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-rose-600 shrink-0" />
                <span>{alertErrorMsg}</span>
              </div>
            )}

            <div className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Target Price (₹)</label>
                <input
                  type="number"
                  min="1"
                  value={alertTargetPrice || ''}
                  onChange={(e) => setAlertTargetPrice(Number(e.target.value))}
                  placeholder="e.g. 250"
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm font-bold text-slate-900 focus:outline-none focus:border-purple-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Marketplace (Optional)</label>
                <select
                  value={alertMarketplace}
                  onChange={(e) => setAlertMarketplace(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold text-slate-800 focus:outline-none focus:border-purple-500"
                >
                  <option value="ALL">Any Marketplace (Amazon, Flipkart, Meesho)</option>
                  <option value="AMAZON">Amazon only</option>
                  <option value="FLIPKART">Flipkart only</option>
                  <option value="MEESHO">Meesho only</option>
                </select>
              </div>

              <div className="p-3 rounded-xl bg-purple-50 border border-purple-100 text-[11px] text-purple-900 leading-relaxed">
                ℹ️ Alerts are checked against collected prices when you open My Alerts. No emails or notifications are sent.
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
              <button
                onClick={() => setShowAlertModal(false)}
                className="px-4 py-2 text-xs font-bold text-slate-600 hover:text-slate-800"
              >
                Cancel
              </button>
              <button
                disabled={alertLoading || alertTargetPrice <= 0}
                onClick={async () => {
                  setAlertLoading(true);
                  setAlertSuccessMsg(null);
                  setAlertErrorMsg(null);
                  try {
                    const res = await api.createPriceAlert({
                      productId: compareData?.productId,
                      targetPrice: alertTargetPrice,
                      marketplace: alertMarketplace === 'ALL' ? undefined : alertMarketplace,
                    });
                    if (res.success) {
                      setAlertSuccessMsg('Price alert saved! View it in My Alerts.');
                      setTimeout(() => setShowAlertModal(false), 1200);
                    } else {
                      setAlertErrorMsg(res.message || 'Failed to set price alert');
                    }
                  } catch (err: any) {
                    setAlertErrorMsg(err?.message || 'Failed to save alert');
                  } finally {
                    setAlertLoading(false);
                  }
                }}
                className="px-5 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-slate-300 text-white text-xs font-bold rounded-xl transition-all shadow-xs"
              >
                {alertLoading ? 'Saving...' : 'Save Alert'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
