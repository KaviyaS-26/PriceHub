import React, { useState, useEffect } from 'react';
import { Product, MarketplaceListing } from '../types';
import { api } from '../services/api';
import { ProductCard } from '../components/ProductCard';
import { DataSourceBadge } from '../components/DataSourceBadge';
import {
  Search,
  Sparkles,
  Scale,
  ShieldCheck,
  TrendingDown,
  ArrowRight,
  Zap,
  SlidersHorizontal,
  CheckCircle2,
  Percent,
  Laptop,
  Smartphone,
  Headphones,
  Watch,
  Tv,
  Home as HomeIcon,
  Flame,
  Award,
  Info,
} from 'lucide-react';

interface HomeProps {
  onNavigate: (page: string, params?: any) => void;
}

export const Home: React.FC<HomeProps> = ({ onNavigate }) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [topDeals, setTopDeals] = useState<MarketplaceListing[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('All');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);
  const [dbCategories, setDbCategories] = useState<string[]>([]);

  useEffect(() => {
    const fetchCats = async () => {
      try {
        const res = await api.getCategories();
        if (res.success && res.data && res.data.length > 0) {
          setDbCategories(res.data);
        } else {
          setDbCategories(['Audio', 'Clothing', 'Footwear', 'Watches', 'Bags', 'Home Decor', 'Kids']);
        }
      } catch {
        setDbCategories(['Audio', 'Clothing', 'Footwear', 'Watches', 'Bags', 'Home Decor', 'Kids']);
      }
    };
    fetchCats();
  }, []);

  const categories = [
    { name: 'All', icon: <Sparkles className="w-4 h-4" /> },
    ...dbCategories.map((cat) => ({
      name: cat,
      icon: <Sparkles className="w-4 h-4" />,
    })),
  ];

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [prodRes, dealRes] = await Promise.all([
          api.getProducts({ category: selectedCategory === 'All' ? undefined : selectedCategory }),
          api.getTopDeals(selectedCategory === 'All' ? undefined : selectedCategory, 6),
        ]);
        if (prodRes.success && prodRes.data) setProducts(prodRes.data);
        if (dealRes.success && dealRes.data) setTopDeals(dealRes.data);
      } catch (e) {
        console.error('Error fetching home data', e);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [selectedCategory]);

  const handleHeroSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      onNavigate('search', { keyword: searchQuery.trim(), category: selectedCategory !== 'All' ? selectedCategory : undefined });
    }
  };

  return (
    <div className="space-y-16 pb-12">
      {/* 1. Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-b from-purple-900 via-indigo-900 to-slate-900 text-white pt-16 pb-24 px-4 sm:px-6 lg:px-8 rounded-b-[40px] shadow-xl">
        {/* Background Ambient Orbs */}
        <div className="absolute top-0 left-1/4 w-96 h-96 bg-purple-500/20 rounded-full blur-3xl pointer-events-none animate-pulse"></div>
        <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-teal-500/15 rounded-full blur-3xl pointer-events-none"></div>

        <div className="max-w-5xl mx-auto text-center relative z-10 space-y-6">
          {/* Eyebrow Pill */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs font-semibold text-purple-200 shadow-inner">
            <Sparkles className="w-3.5 h-3.5 text-teal-300" />
            <span>Multi-Marketplace Intelligence Platform</span>
          </div>

          {/* Headline */}
          <h1 className="text-3xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight brand-font leading-tight sm:leading-none">
            Find the Best Deal.<br />
            <span className="bg-gradient-to-r from-purple-300 via-pink-300 to-teal-300 bg-clip-text text-transparent">
              Make the Smart Choice.
            </span>
          </h1>

          {/* Description */}
          <p className="max-w-2xl mx-auto text-sm sm:text-base text-slate-300 leading-relaxed">
            Compare prices across marketplaces, calculate true effective purchase costs with bank discounts, and uncover eligible instant coupons across Amazon, Flipkart, and Meesho.
          </p>

          {/* Main Hero Search Bar */}
          <form
            onSubmit={handleHeroSearch}
            className="max-w-3xl mx-auto bg-white rounded-2xl p-2 sm:p-2.5 shadow-2xl flex flex-col sm:flex-row items-center gap-2 border-2 border-purple-400/40"
          >
            <div className="flex items-center gap-2 flex-1 w-full pl-3 text-slate-800">
              <Search className="w-5 h-5 text-purple-600 shrink-0" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search Neckband, Sling Bag, T-Shirt, Cycle, Watch..."
                className="w-full text-sm sm:text-base bg-transparent focus:outline-none placeholder:text-slate-400 font-medium"
              />
            </div>

            <div className="flex items-center gap-2 w-full sm:w-auto">
              <select
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
                className="bg-slate-100 text-slate-700 text-xs sm:text-sm font-semibold py-2.5 px-3 rounded-xl border border-slate-200 focus:outline-none"
              >
                {categories.map((c) => (
                  <option key={c.name} value={c.name}>
                    {c.name}
                  </option>
                ))}
              </select>

              <button
                type="submit"
                className="w-full sm:w-auto px-6 py-2.5 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white font-bold text-sm rounded-xl transition-all shadow-md shrink-0 flex items-center justify-center gap-1.5"
              >
                <span>Compare</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </form>

          {/* Three Supported Marketplaces Badge */}
          <div className="pt-4 flex flex-wrap items-center justify-center gap-4 sm:gap-8 text-xs text-slate-300">
            <span className="text-slate-400 text-[11px] uppercase tracking-wider font-bold">Monitored Stores:</span>
            <div className="flex items-center gap-1.5 bg-white/5 border border-white/10 px-3 py-1 rounded-full">
              <span className="w-2 h-2 rounded-full bg-amber-400"></span>
              <span className="font-semibold text-slate-200">Amazon</span>
            </div>
            <div className="flex items-center gap-1.5 bg-white/5 border border-white/10 px-3 py-1 rounded-full">
              <span className="w-2 h-2 rounded-full bg-blue-400"></span>
              <span className="font-semibold text-slate-200">Flipkart</span>
            </div>
            <div className="flex items-center gap-1.5 bg-white/5 border border-white/10 px-3 py-1 rounded-full">
              <span className="w-2 h-2 rounded-full bg-pink-500"></span>
              <span className="font-semibold text-slate-200">Meesho</span>
            </div>
          </div>

          <div className="pt-2 text-center">
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 text-amber-200 border border-amber-300/30 text-xs font-semibold">
              <Info className="w-3.5 h-3.5 text-amber-300" /> Similar products, not identical models — Collected data, 15-17 Oct 2026
            </span>
          </div>
        </div>
      </section>

      {/* 2. Category Selector Chips */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-8 relative z-20">
        <div className="bg-white rounded-2xl p-2 shadow-lg border border-slate-200/80 flex items-center gap-2 overflow-x-auto">
          {categories.map((cat) => {
            const isSelected = selectedCategory === cat.name;
            return (
              <button
                key={cat.name}
                onClick={() => setSelectedCategory(cat.name)}
                className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold whitespace-nowrap transition-all ${
                  isSelected
                    ? 'bg-purple-600 text-white shadow-md'
                    : 'text-slate-600 hover:text-purple-700 hover:bg-purple-50'
                }`}
              >
                {cat.icon}
                <span>{cat.name}</span>
              </button>
            );
          })}
        </div>
      </section>

      {/* 3. Signature Feature: Personalized Deal Lab Teaser */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-teal-900 rounded-3xl p-6 sm:p-10 text-white shadow-xl relative overflow-hidden flex flex-col lg:flex-row items-center justify-between gap-8 border border-purple-500/30">
          <div className="space-y-4 max-w-xl">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-teal-500/20 text-teal-300 text-xs font-extrabold border border-teal-400/30">
              <Sparkles className="w-3.5 h-3.5" /> Signature Feature
            </div>
            <h2 className="text-2xl sm:text-4xl font-extrabold brand-font leading-tight">
              Personalized Deal Lab
            </h2>
            <p className="text-xs sm:text-sm text-slate-200 leading-relaxed">
              Find deals that match your exact shopping criteria. Set your category, maximum budget, minimum rating, and minimum warranty. Our solver evaluates every candidate listing, separates qualifying products, and explains why items fail.
            </p>
            <div className="flex flex-wrap items-center gap-3 pt-2">
              <button
                onClick={() => onNavigate('deallab')}
                className="px-6 py-3 bg-teal-400 hover:bg-teal-300 text-slate-950 rounded-xl text-xs sm:text-sm font-extrabold transition-all shadow-md flex items-center gap-2"
              >
                <span>Launch Deal Lab</span>
                <ArrowRight className="w-4 h-4" />
              </button>
              <button
                onClick={() => onNavigate('about')}
                className="px-4 py-3 bg-white/10 hover:bg-white/15 text-white rounded-xl text-xs sm:text-sm font-semibold border border-white/20 transition-colors"
              >
                View OOP Design
              </button>
            </div>
          </div>

          {/* Interactive Simulation Preview Graphic */}
          <div className="bg-white/10 backdrop-blur-md rounded-2xl p-5 border border-white/15 w-full lg:w-96 space-y-3 shrink-0 shadow-inner text-xs">
            <div className="flex items-center justify-between font-bold text-slate-200 border-b border-white/10 pb-2">
              <span>Dynamic Constraint Solver</span>
              <span className="text-teal-300 text-[11px]">Instant Evaluation</span>
            </div>
            <div className="space-y-2 text-[11px]">
              <div className="flex justify-between items-center text-slate-300">
                <span>Category:</span>
                <span className="font-bold text-white">Audio, Footwear, etc.</span>
              </div>
              <div className="flex justify-between items-center text-slate-300">
                <span>Budget Limit:</span>
                <span className="font-bold text-white">Custom (e.g. ≤ ₹1,000)</span>
              </div>
              <div className="flex justify-between items-center text-slate-300">
                <span>Quality &amp; Coverage:</span>
                <span className="font-bold text-white">Rating ★ + Warranty Mo</span>
              </div>
              <div className="flex justify-between items-center text-slate-300">
                <span>Marketplaces:</span>
                <span className="font-bold text-white">Amazon · Flipkart · Meesho</span>
              </div>
            </div>
            <div className="p-2.5 rounded-xl bg-teal-950/60 border border-teal-500/30 text-[11px] text-teal-200">
              💡 <b>Smart Solver:</b> Ranks qualifiers by comparison value score and suggests constraint relaxations.
            </div>
          </div>
        </div>
      </section>

      {/* 4. Top Collected Deals & Discounts */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        <div className="flex flex-col sm:flex-row items-start sm:items-end justify-between gap-2 border-b border-slate-200 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold text-purple-700 uppercase tracking-wider">
              <Flame className="w-4 h-4 text-rose-500" /> Lowest Effective Prices
            </div>
            <h2 className="text-xl sm:text-3xl font-extrabold text-slate-900 brand-font">
              Top Bargains &amp; Collected Offers
            </h2>
          </div>
          <button
            onClick={() => onNavigate('deals')}
            className="text-xs sm:text-sm font-bold text-purple-700 hover:text-purple-800 flex items-center gap-1 group"
          >
            <span>View All Deals</span>
            <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </button>
        </div>

        {/* Deals Listing Carousel / Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {topDeals.map((deal) => (
            <div
              key={deal.id}
              onClick={() => onNavigate('compare', { productId: deal.productId })}
              className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm hover:shadow-xl hover:border-purple-300 transition-all cursor-pointer flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between gap-3 mb-2.5">
                  <DataSourceBadge dataSource={deal.dataSource} />
                  <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200">
                    {deal.costBreakdown?.savingsPercentage ? `${Math.round(deal.costBreakdown.savingsPercentage)}% OFF` : 'Best Deal'}
                  </span>
                </div>

                <h4 className="font-bold text-sm text-slate-900 line-clamp-2 mb-2">
                  {deal.title}
                </h4>

                <div className="bg-purple-50/70 border border-purple-100 rounded-xl p-3 space-y-1">
                  <div className="flex items-baseline justify-between">
                    <span className="text-[11px] text-purple-900 font-medium">Lowest Effective Cost:</span>
                    <span className="text-base font-extrabold text-purple-700">
                      ₹{deal.costBreakdown?.effectiveCost != null ? deal.costBreakdown.effectiveCost.toLocaleString() : (deal.listedPrice?.toLocaleString() ?? '0')}
                    </span>
                  </div>
                  <div className="flex items-center justify-between text-[11px] text-slate-500">
                    <span>Best store:</span>
                    <span className="font-bold text-slate-800">{deal.marketplace?.name ?? 'Best Store'}</span>
                  </div>
                  {deal.costBreakdown?.totalDiscountAmount ? (
                    <p className="text-[10px] text-teal-700 font-semibold pt-0.5">
                      ✓ Includes ₹{deal.costBreakdown.totalDiscountAmount.toLocaleString()} discounts &amp; coupons
                    </p>
                  ) : null}
                </div>
              </div>

              {/* Store-by-Store price breakdown inside this product's card */}
              <div className="mt-4 pt-3 border-t border-slate-100 space-y-1.5">
                <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">
                  Compare Stores:
                </span>
                <div className="space-y-1">
                  {deal.allMarketplaceListings && deal.allMarketplaceListings.length > 0 ? (
                    deal.allMarketplaceListings.map((store) => (
                      <div
                        key={store.id}
                        className={`flex items-center justify-between text-xs px-2.5 py-1.5 rounded-lg ${
                          store.marketplace?.code === deal.marketplace?.code
                            ? 'bg-purple-100/70 text-purple-950 font-bold border border-purple-200'
                            : 'text-slate-600 bg-slate-50'
                        }`}
                      >
                        <span className="flex items-center gap-1.5">
                          {store.marketplace?.name ?? 'Store'}
                          {store.marketplace?.code && store.marketplace.code === deal.marketplace?.code && (
                            <span className="text-[9px] bg-teal-600 text-white font-extrabold px-1.5 py-0.2 rounded-full uppercase">
                              Lowest
                            </span>
                          )}
                        </span>
                        <span>₹{store.costBreakdown?.effectiveCost != null ? store.costBreakdown.effectiveCost.toLocaleString() : (store.listedPrice?.toLocaleString() ?? '0')}</span>
                      </div>
                    ))
                  ) : (
                    <div className="flex items-center justify-between text-xs px-2.5 py-1.5 rounded-lg bg-purple-100/70 text-purple-950 font-bold border border-purple-200">
                      <span>{deal.marketplace?.name ?? 'Store'} (Lowest)</span>
                      <span>₹{deal.costBreakdown?.effectiveCost != null ? deal.costBreakdown.effectiveCost.toLocaleString() : (deal.listedPrice?.toLocaleString() ?? '0')}</span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 5. Featured Catalog Products */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        <div className="flex flex-col sm:flex-row items-start sm:items-end justify-between gap-2 border-b border-slate-200 pb-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold text-teal-700 uppercase tracking-wider">
              <Award className="w-4 h-4 text-teal-600" /> Best-Value Recommendation Engine
            </div>
            <h2 className="text-xl sm:text-3xl font-extrabold text-slate-900 brand-font">
              Featured Products in {selectedCategory}
            </h2>
          </div>
          <button
            onClick={() => onNavigate('search', { category: selectedCategory !== 'All' ? selectedCategory : undefined })}
            className="text-xs sm:text-sm font-bold text-purple-700 hover:text-purple-800 flex items-center gap-1 group"
          >
            <span>Explore All {products.length} Items</span>
            <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </button>
        </div>

        {loading ? (
          <div className="py-20 flex flex-col items-center justify-center gap-3 text-slate-400">
            <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
            <span className="text-xs font-medium">Loading catalog products...</span>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {products.slice(0, 8).map((product) => (
              <ProductCard key={product.id} product={product} onNavigate={onNavigate} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
};
