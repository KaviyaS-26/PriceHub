import React, { useState, useEffect } from 'react';
import { DealLabResult, DealItem } from '../types';
import { api } from '../services/api';
import {
  Sparkles,
  Sliders,
  CheckCircle2,
  XCircle,
  Lightbulb,
  ExternalLink,
  ShieldCheck,
  Star,
  RefreshCw,
  ShoppingBag,
  RotateCcw,
} from 'lucide-react';

interface DealLabProps {
  onNavigate: (page: string, params?: any) => void;
}

export const DealLab: React.FC<DealLabProps> = ({ onNavigate }) => {
  const [categories, setCategories] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('All');
  const [hasBudgetLimit, setHasBudgetLimit] = useState<boolean>(false);
  const [maxBudget, setMaxBudget] = useState<number>(1000);
  const [minRating, setMinRating] = useState<number>(0);
  const [minWarrantyMonths, setMinWarrantyMonths] = useState<number>(0);
  const [selectedMarketplace, setSelectedMarketplace] = useState<string>('ALL');

  const [result, setResult] = useState<DealLabResult | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<'qualifying' | 'failing'>('qualifying');

  // Load categories from database
  useEffect(() => {
    const loadCategories = async () => {
      try {
        const res = await api.getCategories();
        if (res.success && res.data) {
          setCategories(res.data);
        }
      } catch (err) {
        console.error('Failed to load categories', err);
      }
    };
    loadCategories();
  }, []);

  const handleSolve = async (override?: {
    category?: string;
    hasBudget?: boolean;
    budget?: number;
    rating?: number;
    warranty?: number;
    marketplace?: string;
  }) => {
    setLoading(true);
    try {
      const cat = override?.category !== undefined ? override.category : selectedCategory;
      const budgetActive = override?.hasBudget !== undefined ? override.hasBudget : hasBudgetLimit;
      const budgetVal = override?.budget !== undefined ? override.budget : maxBudget;
      const ratingVal = override?.rating !== undefined ? override.rating : minRating;
      const warrantyVal = override?.warranty !== undefined ? override.warranty : minWarrantyMonths;
      const marketVal = override?.marketplace !== undefined ? override.marketplace : selectedMarketplace;

      const res = await api.getDeals({
        category: cat === 'All' ? undefined : cat,
        maxBudget: budgetActive && budgetVal > 0 ? budgetVal : undefined,
        minRating: ratingVal > 0 ? ratingVal : undefined,
        minWarrantyMonths: warrantyVal > 0 ? warrantyVal : undefined,
        marketplace: marketVal === 'ALL' ? undefined : marketVal,
      });

      if (res.success && res.data) {
        setResult(res.data);
      }
    } catch (err) {
      console.error('Failed to solve Deal Lab', err);
    } finally {
      setLoading(false);
    }
  };

  // Initial solve on page load
  useEffect(() => {
    handleSolve();
  }, []);

  const handleReset = () => {
    setSelectedCategory('All');
    setHasBudgetLimit(false);
    setMaxBudget(1000);
    setMinRating(0);
    setMinWarrantyMonths(0);
    setSelectedMarketplace('ALL');

    handleSolve({
      category: 'All',
      hasBudget: false,
      budget: 1000,
      rating: 0,
      warranty: 0,
      marketplace: 'ALL',
    });
  };

  const handleRelaxConstraint = (constraint?: string, constraintType?: string) => {
    const c = (constraint || constraintType || '').toLowerCase();
    if (c.includes('warranty')) {
      setMinWarrantyMonths(0);
      handleSolve({ warranty: 0 });
    } else if (c.includes('budget')) {
      setHasBudgetLimit(false);
      handleSolve({ hasBudget: false });
    } else if (c.includes('rating')) {
      setMinRating(0);
      handleSolve({ rating: 0 });
    }
  };

  const getMarketplaceBadge = (marketplace: string, name?: string) => {
    const code = marketplace?.toUpperCase();
    if (code === 'AMAZON') {
      return (
        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-500/10 text-amber-800 border border-amber-300">
          Amazon
        </span>
      );
    }
    if (code === 'FLIPKART') {
      return (
        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-blue-500/10 text-blue-800 border border-blue-300">
          Flipkart
        </span>
      );
    }
    return (
      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-pink-500/10 text-pink-800 border border-pink-300">
        Meesho
      </span>
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Hero Header */}
      <div className="bg-gradient-to-r from-purple-900 via-indigo-950 to-teal-900 text-white rounded-3xl p-8 shadow-xl relative overflow-hidden space-y-4 border border-teal-500/30">
        <div className="inline-flex items-center gap-2 px-3.5 py-1 rounded-full bg-teal-500/20 text-teal-300 text-xs font-extrabold border border-teal-400/40">
          <Sparkles className="w-4 h-4 text-teal-300 animate-spin" style={{ animationDuration: '6s' }} />
          <span>Multi-Marketplace Constraint Solver</span>
        </div>
        <h1 className="text-3xl sm:text-5xl font-extrabold brand-font">
          Personalized Deal Lab
        </h1>
        <p className="text-xs sm:text-sm text-slate-300 max-w-3xl leading-relaxed">
          Filter collected listings across Amazon, Flipkart, and Meesho by category, maximum budget, minimum rating, and minimum warranty. Evaluated dynamically on every request with reason analysis for non-qualifiers.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
        {/* Controls Configuration Panel */}
        <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-200/80 space-y-5 lg:sticky lg:top-24">
          <div className="flex items-center justify-between border-b border-slate-100 pb-3">
            <h3 className="font-bold text-sm text-slate-900 flex items-center gap-2">
              <Sliders className="w-4 h-4 text-purple-600" /> Constraint Setup
            </h3>
            <button
              onClick={handleReset}
              className="text-[11px] text-purple-600 hover:text-purple-800 font-semibold flex items-center gap-1 transition-colors"
            >
              <RotateCcw className="w-3 h-3" /> Reset
            </button>
          </div>

          {/* 1. Category */}
          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Category
            </label>
            <select
              value={selectedCategory}
              onChange={(e) => {
                setSelectedCategory(e.target.value);
                handleSolve({ category: e.target.value });
              }}
              className="w-full bg-slate-50 text-slate-800 text-xs font-semibold py-2.5 px-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500"
            >
              <option value="All">All Categories</option>
              {categories.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>

          {/* 2. Maximum Budget Limit */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={hasBudgetLimit}
                  onChange={(e) => {
                    setHasBudgetLimit(e.target.checked);
                    handleSolve({ hasBudget: e.target.checked });
                  }}
                  className="rounded text-purple-600 focus:ring-purple-500"
                />
                Max Budget Limit
              </label>
              {hasBudgetLimit && (
                <span className="text-purple-700 font-extrabold text-sm">₹{maxBudget.toLocaleString()}</span>
              )}
            </div>

            {hasBudgetLimit && (
              <div className="space-y-2 pt-1">
                <input
                  type="range"
                  min="100"
                  max="3000"
                  step="50"
                  value={maxBudget}
                  onChange={(e) => {
                    const val = Number(e.target.value);
                    setMaxBudget(val);
                  }}
                  onMouseUp={() => handleSolve()}
                  onTouchEnd={() => handleSolve()}
                  className="w-full accent-purple-600"
                />
                <div className="flex justify-between text-[10px] text-slate-400 font-medium">
                  <span>₹100</span>
                  <span>₹1,500</span>
                  <span>₹3,000</span>
                </div>
                {/* Preset Chips */}
                <div className="flex flex-wrap gap-1.5 pt-1">
                  {[200, 300, 500, 1000, 2000].map((b) => (
                    <button
                      key={b}
                      onClick={() => {
                        setMaxBudget(b);
                        handleSolve({ budget: b, hasBudget: true });
                      }}
                      className={`text-[10px] px-2 py-0.5 rounded-lg border font-semibold transition-all ${
                        maxBudget === b
                          ? 'bg-purple-600 text-white border-purple-600'
                          : 'bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100'
                      }`}
                    >
                      ₹{b}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* 3. Minimum Rating */}
          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Minimum Rating
            </label>
            <div className="grid grid-cols-5 gap-1.5">
              {[
                { val: 0, label: 'Any' },
                { val: 3.0, label: '3.0★' },
                { val: 3.5, label: '3.5★' },
                { val: 4.0, label: '4.0★' },
                { val: 4.5, label: '4.5★' },
              ].map((r) => (
                <button
                  key={r.val}
                  onClick={() => {
                    setMinRating(r.val);
                    handleSolve({ rating: r.val });
                  }}
                  className={`py-1.5 text-[11px] rounded-xl font-bold transition-all border ${
                    minRating === r.val
                      ? 'bg-purple-600 text-white border-purple-600 shadow-sm'
                      : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                  }`}
                >
                  {r.label}
                </button>
              ))}
            </div>
          </div>

          {/* 4. Minimum Warranty */}
          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Minimum Warranty
            </label>
            <div className="grid grid-cols-3 gap-1.5">
              {[
                { val: 0, label: 'Any' },
                { val: 1, label: '1 Month' },
                { val: 6, label: '6 Months' },
                { val: 12, label: '12 Mo (1 Yr)' },
                { val: 24, label: '24 Mo (2 Yr)' },
              ].map((w) => (
                <button
                  key={w.val}
                  onClick={() => {
                    setMinWarrantyMonths(w.val);
                    handleSolve({ warranty: w.val });
                  }}
                  className={`py-1.5 text-[11px] rounded-xl font-bold transition-all border ${
                    minWarrantyMonths === w.val
                      ? 'bg-purple-600 text-white border-purple-600 shadow-sm'
                      : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                  }`}
                >
                  {w.label}
                </button>
              ))}
            </div>
          </div>

          {/* 5. Marketplace */}
          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700 block uppercase tracking-wider">
              Marketplace
            </label>
            <div className="grid grid-cols-2 gap-1.5">
              {[
                { val: 'ALL', label: 'All Stores' },
                { val: 'AMAZON', label: 'Amazon' },
                { val: 'FLIPKART', label: 'Flipkart' },
                { val: 'MEESHO', label: 'Meesho' },
              ].map((m) => (
                <button
                  key={m.val}
                  onClick={() => {
                    setSelectedMarketplace(m.val);
                    handleSolve({ marketplace: m.val });
                  }}
                  className={`py-1.5 text-[11px] rounded-xl font-bold transition-all border ${
                    selectedMarketplace === m.val
                      ? 'bg-teal-600 text-white border-teal-600 shadow-sm'
                      : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                  }`}
                >
                  {m.label}
                </button>
              ))}
            </div>
          </div>

          {/* Solve Button */}
          <button
            onClick={() => handleSolve()}
            disabled={loading}
            className="w-full py-3 bg-gradient-to-r from-teal-500 to-emerald-600 hover:from-teal-600 hover:to-emerald-700 text-slate-950 font-extrabold text-sm rounded-xl transition-all shadow-md flex items-center justify-center gap-2"
          >
            {loading ? <RefreshCw className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
            <span>Solve Deals</span>
          </button>
        </div>

        {/* Results Panel */}
        <div className="lg:col-span-2 space-y-6">
          {/* Header Summary & Tab Selector */}
          {result && (
            <div className="bg-white rounded-3xl p-6 border border-slate-200/80 shadow-sm space-y-4">
              <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 pb-3">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Results:</span>
                  <span className="px-3 py-1 rounded-full bg-emerald-100 text-emerald-800 text-xs font-extrabold flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                    {result.totalQualifying} Qualified
                  </span>
                  <span className="px-3 py-1 rounded-full bg-rose-100 text-rose-800 text-xs font-extrabold flex items-center gap-1">
                    <XCircle className="w-3.5 h-3.5 text-rose-600" />
                    {result.totalNonQualifying} Failed
                  </span>
                </div>
              </div>

              <p className="text-sm font-semibold text-slate-800 leading-relaxed">
                {result.synthesisSummary}
              </p>

              {/* Navigation Tabs */}
              <div className="flex items-center gap-2 pt-2">
                <button
                  onClick={() => setActiveTab('qualifying')}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    activeTab === 'qualifying'
                      ? 'bg-purple-600 text-white shadow-sm'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                  }`}
                >
                  ✓ Qualifying Deals ({result.totalQualifying})
                </button>
                <button
                  onClick={() => setActiveTab('failing')}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    activeTab === 'failing'
                      ? 'bg-purple-600 text-white shadow-sm'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                  }`}
                >
                  ✕ Why Items Fail ({result.totalNonQualifying})
                </button>
              </div>
            </div>
          )}

          {/* Relaxation Suggestions Banner */}
          {result?.relaxationSuggestions && result.relaxationSuggestions.length > 0 && (
            <div className="bg-amber-50 rounded-3xl p-5 border border-amber-200/80 space-y-3 shadow-sm">
              <h4 className="text-xs font-bold text-amber-900 uppercase tracking-wider flex items-center gap-1.5">
                <Lightbulb className="w-4 h-4 text-amber-600" /> Constraint Relaxation Suggestions
              </h4>
              <div className="space-y-2">
                {result.relaxationSuggestions.map((sug, i) => (
                  <div
                    key={i}
                    className="flex flex-col sm:flex-row sm:items-center justify-between bg-white/90 p-3.5 rounded-2xl border border-amber-200 text-xs text-amber-900 gap-3"
                  >
                    <div className="space-y-0.5">
                      <span className="font-semibold">{sug.suggestionText}</span>
                      <span className="block text-[11px] text-amber-700">
                        Unlocks <b>{sug.additionalProductsUnlocked}</b> deal{sug.additionalProductsUnlocked > 1 ? 's' : ''} currently blocked by this constraint alone.
                      </span>
                    </div>
                    <button
                      onClick={() => handleRelaxConstraint(sug.constraint, sug.constraintType)}
                      className="shrink-0 px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white font-bold rounded-xl text-xs transition-colors shadow-xs"
                    >
                      {sug.actionLabel || 'Relax Constraint'}
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tab 1: Qualifying Deals */}
          {activeTab === 'qualifying' && result && (
            <div className="space-y-4">
              {result.qualifying.length === 0 ? (
                <div className="bg-white rounded-3xl p-10 text-center border border-slate-200 space-y-3">
                  <XCircle className="w-10 h-10 text-rose-500 mx-auto" />
                  <h3 className="text-base font-bold text-slate-900">No deals matched all your constraints</h3>
                  <p className="text-xs text-slate-500 max-w-sm mx-auto">
                    Try relaxing one constraint above (e.g. increase your budget or lower rating requirement) to see available deals.
                  </p>
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-4">
                  {result.qualifying.map((item: DealItem, idx: number) => (
                    <div
                      key={item.id || idx}
                      className="bg-white rounded-3xl p-5 border border-slate-200/80 shadow-sm hover:border-teal-400 transition-all space-y-4"
                    >
                      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                        <div className="flex items-center gap-3">
                          {item.imageUrl ? (
                            <img
                              src={item.imageUrl}
                              alt={item.productTitle}
                              className="w-16 h-16 object-contain rounded-xl bg-slate-50 p-1.5 border border-slate-200 shrink-0"
                            />
                          ) : (
                            <div className="w-16 h-16 rounded-xl bg-purple-50 flex items-center justify-center shrink-0 border border-purple-100">
                              <ShoppingBag className="w-6 h-6 text-purple-400" />
                            </div>
                          )}
                          <div className="space-y-1">
                            <div className="flex flex-wrap items-center gap-2">
                              {getMarketplaceBadge(item.marketplace, item.marketplaceName)}
                              <span className="text-[10px] font-bold uppercase text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                                {item.category}
                              </span>
                              {idx === 0 && (
                                <span className="text-[10px] font-extrabold uppercase text-purple-700 bg-purple-100 px-2 py-0.5 rounded">
                                  🏆 #1 Best Value
                                </span>
                              )}
                            </div>
                            <h3 className="font-bold text-sm text-slate-900 line-clamp-1">
                              {item.productTitle}
                            </h3>
                            <div className="flex items-center gap-3 text-xs text-slate-600">
                              <span className="flex items-center gap-1 font-semibold text-amber-700">
                                <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                                {item.rating ? item.rating.toFixed(1) : 'N/A'}
                              </span>
                              <span className="flex items-center gap-1 text-slate-500">
                                <ShieldCheck className="w-3.5 h-3.5 text-teal-600" />
                                {item.warrantyText || 'No warranty'}
                              </span>
                            </div>
                          </div>
                        </div>

                        <div className="flex sm:flex-col items-center sm:items-end justify-between w-full sm:w-auto shrink-0 border-t sm:border-t-0 pt-3 sm:pt-0 border-slate-100 gap-2">
                          <div className="text-left sm:text-right">
                            <span className="text-2xl font-black text-slate-900">
                              ₹{item.price?.toLocaleString()}
                            </span>
                            <div className="text-[11px] font-bold text-teal-700 bg-teal-50 px-2 py-0.5 rounded mt-0.5">
                              Value Score: {item.valueScore ? item.valueScore.toFixed(1) : '0.0'}/100
                            </div>
                          </div>
                          {item.productUrl && (
                            <a
                              href={item.productUrl}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 hover:bg-purple-900 text-white rounded-xl text-xs font-bold transition-colors shadow-xs"
                            >
                              <span>Open on {item.marketplaceName || item.marketplace}</span>
                              <ExternalLink className="w-3.5 h-3.5" />
                            </a>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Tab 2: Why Items Fail */}
          {activeTab === 'failing' && result && (
            <div className="space-y-4">
              {result.failing.length === 0 ? (
                <div className="bg-white rounded-3xl p-10 text-center border border-slate-200 space-y-2">
                  <CheckCircle2 className="w-10 h-10 text-emerald-500 mx-auto" />
                  <h3 className="text-base font-bold text-slate-900">All candidate items qualified!</h3>
                  <p className="text-xs text-slate-500">Zero items failed your requirements.</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-3">
                  {result.failing.map((item: DealItem, idx: number) => (
                    <div
                      key={item.id || idx}
                      className="bg-white rounded-2xl p-4 border border-rose-200/80 shadow-2xs space-y-2.5"
                    >
                      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-2">
                        <div className="flex items-center gap-2">
                          {getMarketplaceBadge(item.marketplace, item.marketplaceName)}
                          <span className="font-bold text-xs text-slate-900 line-clamp-1">
                            {item.productTitle}
                          </span>
                        </div>
                        <div className="flex items-center gap-3 text-xs">
                          <span className="font-extrabold text-slate-900">₹{item.price?.toLocaleString()}</span>
                          <span className="text-slate-500">★ {item.rating ? item.rating.toFixed(1) : 'N/A'}</span>
                          <span className="text-slate-500">{item.warrantyText || 'No warranty'}</span>
                        </div>
                      </div>

                      {/* Reasons why it failed */}
                      <div className="space-y-1">
                        <span className="text-[10px] font-extrabold uppercase text-rose-700 tracking-wider">
                          Failed Requirements:
                        </span>
                        <div className="flex flex-wrap gap-2">
                          {item.failureReasons && item.failureReasons.length > 0 ? (
                            item.failureReasons.map((reason: string, rIdx: number) => (
                              <span
                                key={rIdx}
                                className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-rose-50 text-rose-800 text-[11px] font-medium border border-rose-200"
                              >
                                <XCircle className="w-3 h-3 text-rose-600 shrink-0" />
                                {reason}
                              </span>
                            ))
                          ) : (
                            <span className="text-xs text-rose-600">Did not satisfy active constraints</span>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
