import React, { useState, useEffect } from 'react';
import { Product, MarketplaceListing } from '../types';
import { api } from '../services/api';
import { ProductCard } from '../components/ProductCard';
import { DataSourceBadge } from '../components/DataSourceBadge';
import { useComparison } from '../context/ComparisonContext';
import {
  Search,
  SlidersHorizontal,
  Grid,
  List,
  Star,
  ShieldCheck,
  Scale,
  Sparkles,
  ExternalLink,
  ChevronRight,
  X,
  TrendingDown,
  Check,
  AlertCircle,
  ArrowRight,
} from 'lucide-react';

interface SearchResultsProps {
  onNavigate: (page: string, params?: any) => void;
  initialParams?: any;
}

export const SearchResults: React.FC<SearchResultsProps> = ({ onNavigate, initialParams }) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [viewMode, setViewMode] = useState<'grid' | 'table'>('grid');

  // Filters State
  const [keyword, setKeyword] = useState<string>(initialParams?.keyword || '');
  const [category, setCategory] = useState<string>(initialParams?.category || 'All');
  const [marketplace, setMarketplace] = useState<string>(initialParams?.marketplace || 'ALL');
  const [maxPrice, setMaxPrice] = useState<number>(initialParams?.maxPrice || 250000);
  const [minRating, setMinRating] = useState<number>(initialParams?.minRating || 0);
  const [sortBy, setSortBy] = useState<string>(initialParams?.sortBy || 'BEST_VALUE');

  // Sync state if initialParams prop updates (e.g. user navigates from navbar or home)
  useEffect(() => {
    if (initialParams) {
      if (initialParams.keyword !== undefined) setKeyword(initialParams.keyword);
      if (initialParams.category !== undefined) setCategory(initialParams.category);
      if (initialParams.marketplace !== undefined) setMarketplace(initialParams.marketplace);
      if (initialParams.maxPrice !== undefined) setMaxPrice(initialParams.maxPrice);
      if (initialParams.minRating !== undefined) setMinRating(initialParams.minRating);
      if (initialParams.sortBy !== undefined) setSortBy(initialParams.sortBy);
      if (initialParams.selectedProductId) {
        handleOpenDetails(initialParams.selectedProductId);
      }
    } else {
      // Clean reset when clicking Explore Products from navbar
      setKeyword('');
      setCategory('All');
      setMarketplace('ALL');
      setMaxPrice(250000);
      setMinRating(0);
      setSortBy('BEST_VALUE');
    }
  }, [initialParams]);

  // Selected Product Drilldown modal
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [detailLoading, setDetailLoading] = useState<boolean>(false);

  const { addProductToCompare, isProductCompared } = useComparison();

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const res = await api.getProducts({
        keyword: keyword.trim() || undefined,
        category: category && category !== 'All' ? category : undefined,
        marketplace: marketplace && marketplace !== 'ALL' ? marketplace : undefined,
        maxPrice: maxPrice < 250000 ? maxPrice : undefined,
        minRating: minRating > 0 ? minRating : undefined,
        sortBy,
      });
      if (res.success && res.data) {
        setProducts(res.data);
      }
    } catch (e) {
      console.error('Error fetching search results', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [category, marketplace, maxPrice, minRating, sortBy, keyword]);

  const handleResetFilters = () => {
    setKeyword('');
    setCategory('All');
    setMarketplace('ALL');
    setMaxPrice(250000);
    setMinRating(0);
    setSortBy('BEST_VALUE');
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchProducts();
  };

  const handleOpenDetails = async (id: number) => {
    setDetailLoading(true);
    try {
      const res = await api.getProductById(id);
      if (res.success && res.data) {
        setSelectedProduct(res.data);
      }
    } finally {
      setDetailLoading(false);
    }
  };

  const [availableCategories, setAvailableCategories] = useState<string[]>([
    'Audio',
    'Clothing',
    'Footwear',
    'Watches',
    'Bags',
    'Home Decor',
    'Kids',
  ]);

  useEffect(() => {
    api.getCategories().then((res) => {
      if (res.success && res.data && res.data.length > 0) {
        setAvailableCategories(res.data);
      }
    }).catch(() => {});
  }, []);

  const categories = ['All', ...availableCategories];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header & Search Bar */}
      <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-200/80 space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-[11px] font-bold text-amber-800 bg-amber-50 border border-amber-200 px-2.5 py-0.5 rounded-full">
                Similar products, not identical models
              </span>
              <span className="text-[11px] font-semibold text-purple-700 bg-purple-50 border border-purple-200 px-2.5 py-0.5 rounded-full">
                Collected data, 15-17 Oct 2026
              </span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 brand-font">
              Product Search &amp; Price Comparison
            </h1>
            <p className="text-xs sm:text-sm text-slate-500">
              Comparing listings across Amazon, Flipkart, and Meesho
            </p>
          </div>

          {/* Search Input */}
          <form onSubmit={handleSearchSubmit} className="flex items-center gap-2 max-w-md w-full">
            <div className="relative flex-1">
              <input
                type="text"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="Search model, brand, spec..."
                className="w-full pl-10 pr-4 py-2 text-sm bg-slate-100 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:bg-white text-slate-900"
              />
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
            </div>
            <button
              type="submit"
              className="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-sm font-bold shadow-sm transition-colors"
            >
              Filter
            </button>
          </form>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 items-start">
        {/* Sidebar Filters */}
        <aside className="bg-white rounded-3xl p-6 shadow-sm border border-slate-200/80 space-y-6 lg:sticky lg:top-24">
          <div className="flex items-center justify-between border-b border-slate-100 pb-3">
            <h3 className="font-bold text-sm text-slate-900 flex items-center gap-2">
              <SlidersHorizontal className="w-4 h-4 text-purple-600" /> Filters &amp; Sorting
            </h3>
            <button
              onClick={handleResetFilters}
              className="text-[11px] text-purple-600 hover:underline font-semibold"
            >
              Reset All
            </button>
          </div>

          {/* Category Filter */}
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Category
            </label>
            <div className="space-y-1">
              {categories.map((c) => (
                <button
                  key={c}
                  onClick={() => setCategory(c)}
                  className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                    category === c
                      ? 'bg-purple-50 text-purple-700 font-bold border border-purple-200'
                      : 'text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  {c}
                </button>
              ))}
            </div>
          </div>

          {/* Marketplace Filter */}
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Marketplace
            </label>
            <div className="space-y-1 text-xs">
              {[
                { label: 'All Marketplaces', value: 'ALL' },
                { label: 'Amazon', value: 'AMAZON' },
                { label: 'Flipkart', value: 'FLIPKART' },
                { label: 'Meesho', value: 'MEESHO' },
              ].map((m) => (
                <label
                  key={m.value}
                  className="flex items-center gap-2 text-slate-700 cursor-pointer hover:text-purple-700 py-1"
                >
                  <input
                    type="radio"
                    name="marketplace"
                    checked={marketplace === m.value}
                    onChange={() => setMarketplace(m.value)}
                    className="text-purple-600 focus:ring-purple-500"
                  />
                  <span>{m.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Max Budget Slider */}
          <div className="space-y-2">
            <div className="flex justify-between text-xs font-bold text-slate-700">
              <span className="uppercase tracking-wider">Max Budget</span>
              <span className="text-purple-700 font-extrabold">₹{maxPrice.toLocaleString()}</span>
            </div>
            <input
              type="range"
              min="100"
              max="250000"
              step="500"
              value={maxPrice}
              onChange={(e) => setMaxPrice(Number(e.target.value))}
              className="w-full accent-purple-600"
            />
            <div className="flex justify-between text-[10px] text-slate-400 font-medium">
              <span>₹10,000</span>
              <span>₹2,50,000+</span>
            </div>
          </div>

          {/* Minimum Rating */}
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Minimum Rating
            </label>
            <div className="flex items-center gap-1.5">
              {[0, 4.0, 4.5, 4.7].map((r) => (
                <button
                  key={r}
                  onClick={() => setMinRating(r)}
                  className={`flex-1 py-1.5 rounded-lg text-xs font-bold flex items-center justify-center gap-1 border transition-all ${
                    minRating === r
                      ? 'bg-purple-600 text-white border-purple-600 shadow-xs'
                      : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                  }`}
                >
                  {r === 0 ? 'Any' : `${r}★`}
                </button>
              ))}
            </div>
          </div>

          {/* Sort Selection */}
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Sort By
            </label>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="w-full bg-slate-50 text-slate-800 text-xs font-semibold py-2 px-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500"
            >
              <option value="BEST_VALUE">Best-Value Score (Default)</option>
              <option value="PRICE_LOW_HIGH">Lowest Effective Price</option>
              <option value="PRICE_HIGH_LOW">Highest Price</option>
              <option value="RATING">Highest Customer Rating</option>
            </select>
          </div>
        </aside>

        {/* Results Main Area */}
        <main className="lg:col-span-3 space-y-6">
          {/* Controls Bar */}
          <div className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200/80 flex items-center justify-between gap-4">
            <span className="text-xs font-bold text-slate-600">
              Showing <span className="text-purple-700 font-extrabold">{products.length}</span> Products
            </span>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setViewMode('grid')}
                className={`p-1.5 rounded-lg transition-colors ${
                  viewMode === 'grid' ? 'bg-purple-100 text-purple-700' : 'text-slate-400 hover:text-slate-600'
                }`}
                title="Grid View"
              >
                <Grid className="w-4 h-4" />
              </button>
              <button
                onClick={() => setViewMode('table')}
                className={`p-1.5 rounded-lg transition-colors ${
                  viewMode === 'table' ? 'bg-purple-100 text-purple-700' : 'text-slate-400 hover:text-slate-600'
                }`}
                title="Table Comparison View"
              >
                <List className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Product Grid or Table */}
          {loading ? (
            <div className="py-24 flex flex-col items-center justify-center gap-3 text-slate-400">
              <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
              <span className="text-xs font-semibold">Filtering products and calculating effective prices...</span>
            </div>
          ) : products.length === 0 ? (
            <div className="bg-white rounded-3xl p-12 text-center border border-slate-200 space-y-4 shadow-sm">
              <AlertCircle className="w-12 h-12 text-amber-500 mx-auto" />
              <h3 className="text-lg font-bold text-slate-900">No products match the selected filters</h3>
              <p className="text-xs text-slate-500 max-w-sm mx-auto">
                No items matched your current filter criteria. Clear your filters to view the full product catalog.
              </p>
              <button
                onClick={handleResetFilters}
                className="mt-2 inline-flex items-center gap-1.5 px-5 py-2.5 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold transition-all shadow-md active:scale-95"
              >
                <span>Reset All Filters</span>
              </button>
            </div>
          ) : viewMode === 'grid' ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
              {products.map((p) => (
                <ProductCard key={p.id} product={p} onNavigate={onNavigate} />
              ))}
            </div>
          ) : (
            /* Table View */
            <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 border-b border-slate-200 text-slate-600 font-bold uppercase tracking-wider">
                  <tr>
                    <th className="py-3 px-4">Product</th>
                    <th className="py-3 px-4">Category</th>
                    <th className="py-3 px-4">Lowest Price</th>
                    <th className="py-3 px-4">Best Marketplace</th>
                    <th className="py-3 px-4">Rating</th>
                    <th className="py-3 px-4 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {products.map((p) => (
                    <tr key={p.id} className="hover:bg-purple-50/50 transition-colors">
                      <td className="py-3 px-4 flex items-center gap-3">
                        <img src={p.imageUrl} alt={p.title} className="w-10 h-10 object-contain rounded bg-white p-1 border border-slate-200" />
                        <div>
                          <p className="font-bold text-slate-900">{p.title}</p>
                          <span className="text-[10px] text-purple-700 font-semibold uppercase">{p.brand}</span>
                        </div>
                      </td>
                      <td className="py-3 px-4 font-medium text-slate-600">{p.category}</td>
                      <td className="py-3 px-4 font-extrabold text-slate-900">
                        ₹{(p.lowestEffectivePrice || p.basePrice).toLocaleString()}
                      </td>
                      <td className="py-3 px-4">
                        <span className="px-2 py-0.5 rounded-full bg-purple-100 text-purple-800 text-[11px] font-bold">
                          {p.bestValueMarketplace}
                        </span>
                      </td>
                      <td className="py-3 px-4 font-bold text-amber-700">
                        ⭐ {p.ratingAverage}
                      </td>
                      <td className="py-3 px-4 text-right space-x-2">
                        <button
                          onClick={() => handleOpenDetails(p.id)}
                          className="px-3 py-1.5 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-bold text-xs"
                        >
                          View Listings
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </main>
      </div>

      {/* Selected Product Drilldown Modal (Section 7 Side-by-side Listings Table) */}
      {selectedProduct && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto animate-in fade-in">
          <div className="bg-white rounded-3xl max-w-4xl w-full max-h-[90vh] overflow-y-auto p-6 shadow-2xl border border-slate-200 space-y-6">
            {/* Modal Header */}
            <div className="flex items-start justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-4">
                <img
                  src={selectedProduct.imageUrl}
                  alt={selectedProduct.title}
                  className="w-16 h-16 object-contain rounded-xl bg-slate-50 p-2 border border-slate-200"
                />
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-xs font-extrabold uppercase text-purple-700 bg-purple-50 px-2 py-0.5 rounded">
                      {selectedProduct.brand}
                    </span>
                    <span className="text-xs text-slate-400">Model: {selectedProduct.modelNumber || 'Standard'}</span>
                  </div>
                  <h2 className="text-lg font-bold text-slate-900">{selectedProduct.title}</h2>
                </div>
              </div>

              <button
                onClick={() => setSelectedProduct(null)}
                className="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Side-by-Side Marketplace Listings Table */}
            <div className="space-y-3">
              <h3 className="font-bold text-sm text-slate-900 flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-teal-600" /> Marketplace Price &amp; Discount Comparison
              </h3>

              <div className="border border-slate-200 rounded-2xl overflow-hidden shadow-2xs">
                <table className="w-full text-left text-xs">
                  <thead className="bg-slate-50 border-b border-slate-200 text-slate-600 font-bold uppercase tracking-wider">
                    <tr>
                      <th className="py-3 px-4">Marketplace</th>
                      <th className="py-3 px-4">Listed Price</th>
                      <th className="py-3 px-4">Delivery</th>
                      <th className="py-3 px-4">Discounts &amp; Offers</th>
                      <th className="py-3 px-4">Effective Cost</th>
                      <th className="py-3 px-4">Fulfillment</th>
                      <th className="py-3 px-4 text-right">Store Link</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {selectedProduct.listings?.map((listing) => (
                      <tr key={listing.id} className="hover:bg-slate-50/80">
                        <td className="py-3.5 px-4 font-bold text-slate-900 flex items-center gap-2">
                          <span className="w-2 h-2 rounded-full bg-teal-500"></span>
                          <span>{listing.marketplace.name}</span>
                        </td>
                        <td className="py-3.5 px-4 text-slate-700 font-semibold">
                          ₹{listing.listedPrice.toLocaleString()}
                        </td>
                        <td className="py-3.5 px-4 text-slate-600">
                          {listing.deliveryCharge === 0 ? (
                            <span className="text-emerald-600 font-bold">FREE</span>
                          ) : (
                            `₹${listing.deliveryCharge}`
                          )}
                        </td>
                        <td className="py-3.5 px-4">
                          {listing.offers && listing.offers.length > 0 ? (
                            <div className="space-y-1">
                              {listing.offers.map((o, idx) => (
                                <span
                                  key={idx}
                                  className="inline-block bg-teal-50 border border-teal-200 text-teal-800 text-[10px] font-bold px-1.5 py-0.5 rounded mr-1"
                                >
                                  🏷️ {o.title}
                                </span>
                              ))}
                            </div>
                          ) : (
                            <span className="text-slate-400">No active coupons</span>
                          )}
                        </td>
                        <td className="py-3.5 px-4">
                          <span className="text-base font-extrabold text-purple-700">
                            ₹{listing.costBreakdown?.effectiveCost.toLocaleString()}
                          </span>
                        </td>
                        <td className="py-3.5 px-4 text-slate-600 text-[11px]">
                          <div>{listing.deliveryEstimateDays} Days Delivery</div>
                          <div className="text-slate-400">{listing.warrantyMonths} Mo. Warranty</div>
                        </td>
                        <td className="py-3.5 px-4 text-right">
                          <a
                            href={listing.productUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="inline-flex items-center gap-1 px-3 py-1.5 bg-slate-100 hover:bg-purple-100 text-purple-700 rounded-lg font-bold text-xs transition-colors"
                          >
                            <span>Visit Store</span>
                            <ExternalLink className="w-3 h-3" />
                          </a>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Specifications Matrix */}
            <div className="space-y-3 pt-4 border-t border-slate-100">
              <h3 className="font-bold text-sm text-slate-900">Technical Specifications</h3>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs">
                {selectedProduct.specifications?.map((spec, i) => (
                  <div key={i} className="p-3 bg-slate-50 rounded-xl border border-slate-200/80">
                    <span className="text-slate-400 text-[10px] uppercase font-bold block">{spec.specKey}</span>
                    <span className="text-slate-800 font-semibold">{spec.specValue}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Modal Bottom Actions */}
            <div className="flex items-center justify-between pt-4 border-t border-slate-100">
              <button
                onClick={() => {
                  addProductToCompare(selectedProduct);
                  setSelectedProduct(null);
                }}
                className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-xl text-xs font-bold transition-colors flex items-center gap-1.5"
              >
                <Scale className="w-4 h-4 text-purple-600" /> Add to Tray
              </button>

              <button
                onClick={() => {
                  const id = selectedProduct.id;
                  setSelectedProduct(null);
                  onNavigate('compare', { productId: id });
                }}
                className="px-5 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center gap-1.5 shadow-sm"
              >
                <span>Compare Marketplaces</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
