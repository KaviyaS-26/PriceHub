import React, { useState } from 'react';
import { Product } from '../types';
import { DataSourceBadge } from './DataSourceBadge';
import { useComparison } from '../context/ComparisonContext';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import {
  Star,
  Scale,
  Heart,
  TrendingDown,
  Sparkles,
  Check,
  ShieldCheck,
  LineChart,
  Plus,
} from 'lucide-react';

interface ProductCardProps {
  product: Product;
  onNavigate: (page: string, params?: any) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onNavigate }) => {
  const { addProductToCompare, removeProductFromCompare, isProductCompared } = useComparison();
  const { isAuthenticated } = useAuth();
  const [isSaved, setIsSaved] = useState(false);
  const [saveLoading, setSaveLoading] = useState(false);

  const isCompared = isProductCompared(product.id);

  const handleToggleCompare = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (isCompared) {
      removeProductFromCompare(product.id);
    } else {
      addProductToCompare(product);
    }
  };

  const handleToggleSave = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!isAuthenticated) {
      alert('Please log in to save products to your account.');
      onNavigate('login');
      return;
    }

    setSaveLoading(true);
    try {
      if (isSaved) {
        await api.removeSavedProduct(product.id);
        setIsSaved(false);
      } else {
        await api.saveProduct(product.id);
        setIsSaved(true);
      }
    } catch {
      setIsSaved(!isSaved);
    } finally {
      setSaveLoading(false);
    }
  };



  const lowestPrice = product.lowestEffectivePrice || product.basePrice;
  const highestPrice = product.highestEffectivePrice || product.basePrice * 1.1;
  const savings = highestPrice - lowestPrice;
  const savingsPercent = highestPrice > 0 ? (savings / highestPrice) * 100 : 0;

  return (
    <div
      onClick={() => onNavigate('compare', { productId: product.id })}
      className="group bg-white rounded-2xl border border-slate-200/80 hover:border-purple-300 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 flex flex-col justify-between overflow-hidden cursor-pointer"
    >
      {/* Top Media & Floating Badges */}
      <div className="relative p-4 bg-gradient-to-b from-slate-50 to-white flex items-center justify-center h-52 overflow-hidden border-b border-slate-100">
        <div className="w-full h-44 flex items-center justify-center">
          <img
            src={product.imageUrl}
            alt={product.title}
            className="w-full h-full object-contain transition-transform duration-500 group-hover:scale-105"
          />
        </div>

        {/* Savings Badge */}
        {savingsPercent > 4 && (
          <div className="absolute top-3 left-3 bg-gradient-to-r from-teal-600 to-emerald-600 text-white text-[10px] font-extrabold px-2 py-0.5 rounded-full shadow-sm flex items-center gap-1">
            <TrendingDown className="w-3 h-3" /> Save {Math.round(savingsPercent)}%
          </div>
        )}

        {/* Category Pill & Data Source */}
        <div className="absolute bottom-2 left-3 flex items-center gap-1.5">
          <span className="bg-white/90 backdrop-blur-sm text-slate-600 text-[10px] font-semibold px-2 py-0.5 rounded-md border border-slate-200 shadow-2xs">
            {product.category}
          </span>
          <DataSourceBadge dataSource={product.listings?.[0]?.dataSource || 'COLLECTED'} priceDate={product.listings?.[0]?.priceDate || '15-17 Oct 2026'} />
        </div>

        {/* Quick Action Top-Right */}
        <div className="absolute top-3 right-3 flex flex-col gap-1.5">
          <button
            onClick={handleToggleSave}
            disabled={saveLoading}
            className={`p-2 rounded-full backdrop-blur-sm transition-all shadow-sm ${
              isSaved
                ? 'bg-rose-50 text-rose-600 border border-rose-200'
                : 'bg-white/90 text-slate-400 hover:text-rose-500 hover:bg-white border border-slate-200'
            }`}
            title="Save to Wishlist"
          >
            <Heart className={`w-3.5 h-3.5 ${isSaved ? 'fill-rose-500' : ''}`} />
          </button>
        </div>
      </div>

      {/* Card Content */}
      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          {/* Brand & Rating */}
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1.5">
            <span className="font-bold text-purple-700 tracking-wider uppercase text-[10px]">
              {product.brand}
            </span>
            <div className="flex items-center gap-1 bg-amber-50 border border-amber-200/60 px-1.5 py-0.5 rounded text-[11px] font-bold text-amber-800">
              <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
              <span>{product.ratingAverage || 4.5}</span>
            </div>
          </div>

          {/* Title & Model */}
          <h3 className="font-bold text-slate-900 text-sm leading-snug line-clamp-2 mb-1 group-hover:text-purple-600 transition-colors">
            {product.title}
          </h3>
          {product.modelNumber && (
            <div className="text-[11px] text-slate-500 font-mono mb-2">
              Model: {product.modelNumber}
            </div>
          )}

          {/* Highlights specs pills */}
          <div className="flex flex-wrap gap-1 mb-3">
            {product.specifications?.slice(0, 3).map((spec, i) => (
              <span
                key={i}
                className="text-[10px] bg-slate-100 text-slate-600 px-1.5 py-0.5 rounded font-medium"
              >
                {spec.specValue}
              </span>
            ))}
          </div>
        </div>

        {/* Pricing Summary */}
        <div className="pt-3 border-t border-slate-100">
          <div className="flex items-baseline justify-between mb-1">
            <div>
              <span className="text-[10px] text-slate-500 block">Lowest Effective Price</span>
              <span className="text-lg font-extrabold text-slate-900 tracking-tight">
                ₹{lowestPrice.toLocaleString()}
              </span>
            </div>
            {savings > 0 && (
              <div className="text-right">
                <span className="text-[11px] text-slate-400 line-through">
                  ₹{highestPrice.toLocaleString()}
                </span>
                <span className="text-[10px] text-emerald-600 font-bold block">
                  -₹{Math.round(savings).toLocaleString()} off
                </span>
              </div>
            )}
          </div>

          {/* Best Marketplace Indicator */}
          <div className="flex items-center justify-between text-[11px] text-slate-500 mb-3 bg-purple-50/60 px-2 py-1 rounded-lg border border-purple-100">
            <span className="flex items-center gap-1 font-medium text-slate-700">
              <ShieldCheck className="w-3.5 h-3.5 text-teal-600" /> Best deal on:
            </span>
            <span className="font-bold text-purple-700">{product.bestValueMarketplace || 'Amazon'}</span>
          </div>

          {/* Action Row */}
          <div className="flex items-center gap-2">
            <button
              onClick={(e) => {
                e.stopPropagation();
                onNavigate('compare', { productId: product.id });
              }}
              className="flex-1 py-2 px-3 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 bg-purple-600 hover:bg-purple-700 text-white shadow-xs"
            >
              <Scale className="w-3.5 h-3.5" />
              <span>Compare</span>
            </button>
            <button
              onClick={handleToggleCompare}
              className={`p-2 rounded-xl text-xs font-bold transition-all border ${
                isCompared
                  ? 'bg-teal-50 border-teal-300 text-teal-700'
                  : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100'
              }`}
              title={isCompared ? 'Remove from Compare Tray' : 'Add to Compare Tray'}
            >
              {isCompared ? <Check className="w-3.5 h-3.5 text-teal-600" /> : <Plus className="w-3.5 h-3.5 text-slate-500" />}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
