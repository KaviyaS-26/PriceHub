import React, { useState, useEffect } from 'react';
import { MarketplaceListing } from '../types';
import { api } from '../services/api';
import { DataSourceBadge } from '../components/DataSourceBadge';
import { useComparison } from '../context/ComparisonContext';
import {
  Flame,
  Tag,
  Percent,
  Copy,
  Check,
  ExternalLink,
  ShieldCheck,
  TrendingDown,
  Sparkles,
  Scale,
} from 'lucide-react';

interface BestDealsProps {
  onNavigate: (page: string, params?: any) => void;
}

export const BestDeals: React.FC<BestDealsProps> = ({ onNavigate }) => {
  const [deals, setDeals] = useState<MarketplaceListing[]>([]);
  const [category, setCategory] = useState<string>('All');
  const [loading, setLoading] = useState<boolean>(true);
  const [copiedCode, setCopiedCode] = useState<string | null>(null);

  const fetchDeals = async () => {
    setLoading(true);
    try {
      const res = await api.getTopDeals(category !== 'All' ? category : undefined, 18);
      if (res.success && res.data) {
        setDeals(res.data);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDeals();
  }, [category]);

  const handleCopyCode = (code: string) => {
    navigator.clipboard.writeText(code);
    setCopiedCode(code);
    setTimeout(() => setCopiedCode(null), 2000);
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
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 text-white rounded-3xl p-8 shadow-xl relative overflow-hidden space-y-4">
        <div className="flex flex-wrap items-center gap-2">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-rose-500/20 text-rose-300 text-xs font-bold border border-rose-500/30">
            <Flame className="w-4 h-4 text-rose-400" /> Collected Price Drops
          </div>
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 text-amber-200 border border-amber-300/30 text-xs font-semibold">
            Similar products, not identical models
          </span>
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/10 text-purple-200 border border-purple-300/30 text-xs font-semibold">
            Collected data, 15-17 Oct 2026
          </span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-extrabold brand-font">
          Lowest Effective Prices &amp; Exclusive Deals
        </h1>
        <p className="text-xs sm:text-sm text-slate-300 max-w-2xl leading-relaxed">
          Comparing prices across Amazon, Flipkart, and Meesho to compute the absolute lowest out-of-pocket cost.
        </p>

        {/* Category Pills */}
        <div className="flex items-center gap-2 overflow-x-auto pt-2">
          {categories.map((c) => (
            <button
              key={c}
              onClick={() => setCategory(c)}
              className={`px-4 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all ${
                category === c
                  ? 'bg-white text-purple-900 shadow-md'
                  : 'bg-white/10 hover:bg-white/20 text-white'
              }`}
            >
              {c}
            </button>
          ))}
        </div>
      </div>

      {/* Deals Grid */}
      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center gap-3 text-slate-400">
          <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
          <span className="text-xs font-semibold">Calculating best value discounts...</span>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {deals.map((deal) => {
            const savingsPercent = deal.costBreakdown?.savingsPercentage || 0;
            const savingsAmount = deal.costBreakdown?.totalDiscountAmount || 0;

            return (
              <div
                key={deal.id}
                className="bg-white rounded-3xl border border-slate-200/80 p-5 shadow-sm hover:shadow-xl hover:border-purple-300 transition-all flex flex-col justify-between"
              >
                <div>
                  {/* Top Bar */}
                  <div className="flex items-center justify-between gap-2 mb-3">
                    <DataSourceBadge dataSource={deal.dataSource} />
                    {savingsPercent > 0 && (
                      <span className="text-xs font-extrabold text-emerald-700 bg-emerald-50 px-2.5 py-0.5 rounded-full border border-emerald-200 flex items-center gap-1">
                        <TrendingDown className="w-3.5 h-3.5 text-emerald-600" /> Save {Math.round(savingsPercent)}%
                      </span>
                    )}
                  </div>

                  <h3
                    onClick={() => onNavigate('compare', { productId: deal.productId })}
                    className="font-bold text-sm text-slate-900 line-clamp-2 hover:text-purple-600 cursor-pointer transition-colors mb-2"
                  >
                    {deal.title}
                  </h3>

                  {/* Best Marketplace Indicator */}
                  <div className="mb-3 px-2.5 py-1.5 bg-purple-50/70 border border-purple-100 rounded-xl flex items-center justify-between text-xs">
                    <span className="text-slate-600">Lowest effective on:</span>
                    <span className="font-extrabold text-purple-700">{deal.marketplace.name}</span>
                  </div>

                  {/* Prices across all stores */}
                  <div className="space-y-1 mb-4 bg-slate-50 p-2.5 rounded-xl border border-slate-200/70">
                    <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block mb-1">
                      Store Prices:
                    </span>
                    {deal.allMarketplaceListings && deal.allMarketplaceListings.length > 0 ? (
                      deal.allMarketplaceListings.map((store) => (
                        <div
                          key={store.id}
                          className={`flex items-center justify-between text-xs px-2 py-1 rounded-md ${
                            store.marketplace.code === deal.marketplace.code
                              ? 'bg-purple-100/80 text-purple-950 font-bold'
                              : 'text-slate-600'
                          }`}
                        >
                          <span className="flex items-center gap-1">
                            {store.marketplace.name}
                            {store.marketplace.code === deal.marketplace.code && (
                              <span className="text-[9px] bg-teal-600 text-white font-extrabold px-1 rounded-full">
                                Best
                              </span>
                            )}
                          </span>
                          <span>₹{store.costBreakdown?.effectiveCost.toLocaleString()}</span>
                        </div>
                      ))
                    ) : (
                      <div className="flex items-center justify-between text-xs text-purple-900 font-bold">
                        <span>{deal.marketplace.name}</span>
                        <span>₹{deal.costBreakdown?.effectiveCost.toLocaleString()}</span>
                      </div>
                    )}
                  </div>

                  {/* Active Offers preview */}
                  <div className="space-y-1.5 mb-4">
                    {deal.offers?.map((offer, idx) => (
                      <div
                        key={idx}
                        className="p-2 bg-teal-50/70 border border-teal-200/70 rounded-xl text-xs flex items-center justify-between gap-2 text-teal-900"
                      >
                        <div className="flex items-center gap-1.5 truncate">
                          <Tag className="w-3.5 h-3.5 text-teal-600 shrink-0" />
                          <span className="truncate font-semibold text-[11px]">{offer.title}</span>
                        </div>
                        {offer.couponCode && (
                          <button
                            onClick={() => handleCopyCode(offer.couponCode!)}
                            className="px-2 py-0.5 bg-white text-teal-700 font-mono text-[10px] font-bold rounded border border-teal-300 hover:bg-teal-100 flex items-center gap-1 shrink-0"
                            title="Copy Code"
                          >
                            {copiedCode === offer.couponCode ? <Check className="w-3 h-3 text-emerald-600" /> : <Copy className="w-3 h-3" />}
                            <span>{offer.couponCode}</span>
                          </button>
                        )}
                      </div>
                    ))}
                  </div>
                </div>

                {/* Pricing Breakdown Card */}
                <div className="pt-3 border-t border-slate-100 space-y-2">
                  <div className="flex items-baseline justify-between">
                    <div>
                      <span className="text-[10px] text-slate-500 block">Effective Purchase Cost</span>
                      <span className="text-xl font-extrabold text-slate-900">
                        ₹{deal.costBreakdown?.effectiveCost.toLocaleString()}
                      </span>
                    </div>
                    {savingsAmount > 0 && (
                      <div className="text-right">
                        <span className="text-xs text-slate-400 line-through">₹{deal.listedPrice.toLocaleString()}</span>
                        <span className="text-[10px] text-emerald-600 font-bold block">
                          You save ₹{savingsAmount.toLocaleString()}
                        </span>
                      </div>
                    )}
                  </div>

                  <div className="flex items-center justify-between text-[11px] text-slate-500 pt-1">
                    <span>{deal.deliveryEstimateDays} Days Fulfillment</span>
                    <span>{deal.warrantyMonths} Months Warranty</span>
                  </div>

                  {/* Actions */}
                  <div className="grid grid-cols-2 gap-2 pt-2">
                    <button
                      onClick={() => onNavigate('search', { selectedProductId: deal.productId })}
                      className="py-2 px-3 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-xl text-xs font-bold transition-colors text-center"
                    >
                      Compare Prices
                    </button>
                    <a
                      href={deal.productUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="py-2 px-3 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1 shadow-xs"
                    >
                      <span>Go to Store</span>
                      <ExternalLink className="w-3.5 h-3.5" />
                    </a>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
