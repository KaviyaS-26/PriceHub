import React, { useState, useEffect } from 'react';
import { SavedProduct } from '../types';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useComparison } from '../context/ComparisonContext';
import {
  Heart,
  Scale,
  Trash2,
  ExternalLink,
  ArrowRight,
  ShieldCheck,
} from 'lucide-react';

interface SavedProductsProps {
  onNavigate: (page: string, params?: any) => void;
}

export const SavedProducts: React.FC<SavedProductsProps> = ({ onNavigate }) => {
  const [savedItems, setSavedItems] = useState<SavedProduct[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const { isAuthenticated } = useAuth();
  const { addProductToCompare } = useComparison();

  const fetchSaved = async () => {
    setLoading(true);
    try {
      const res = await api.getSavedProducts();
      if (res.success && res.data) {
        setSavedItems(res.data);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!isAuthenticated) {
      onNavigate('login');
      return;
    }
    fetchSaved();
  }, [isAuthenticated]);

  const handleRemove = async (productId: number) => {
    await api.removeSavedProduct(productId);
    setSavedItems(prev => prev.filter(item => item.product.id !== productId));
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-rose-600 uppercase tracking-wider mb-1">
            <Heart className="w-4 h-4" /> Personal Wishlist
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 brand-font">
            Saved Products &amp; Monitored Deals
          </h1>
          <p className="text-xs sm:text-sm text-slate-500">
            Keep track of favorite products and view instant price comparisons across marketplaces.
          </p>
        </div>
      </div>

      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center gap-3 text-slate-400">
          <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
          <span className="text-xs font-semibold">Loading saved wishlist items...</span>
        </div>
      ) : savedItems.length === 0 ? (
        <div className="bg-white rounded-3xl p-12 text-center border border-slate-200 space-y-4">
          <Heart className="w-12 h-12 text-slate-300 mx-auto" />
          <h3 className="text-lg font-bold text-slate-900">Your wishlist is empty</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            Click the heart icon on any product in our catalog to track it here.
          </p>
          <button
            onClick={() => onNavigate('search')}
            className="px-5 py-2.5 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold transition-all shadow-md inline-flex items-center gap-2"
          >
            <span>Explore Catalog</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {savedItems.map((item) => (
            <div
              key={item.id}
              className="bg-white rounded-3xl p-5 border border-slate-200/80 shadow-sm hover:shadow-xl hover:border-purple-300 transition-all flex flex-col justify-between"
            >
              <div className="space-y-3">
                <div className="flex items-start justify-between">
                  <span className="text-[10px] font-extrabold uppercase text-purple-700 bg-purple-50 px-2 py-0.5 rounded">
                    {item.product.brand}
                  </span>
                  <button
                    onClick={() => handleRemove(item.product.id)}
                    className="p-1.5 text-slate-400 hover:text-red-500 rounded-full hover:bg-red-50"
                    title="Remove from saved"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>

                <div className="h-40 flex items-center justify-center bg-slate-50 rounded-2xl p-2 border border-slate-100">
                  <img src={item.product.imageUrl} alt={item.product.title} className="max-h-36 object-contain" />
                </div>

                <h3 className="font-bold text-sm text-slate-900 line-clamp-2">{item.product.title}</h3>

                <div className="flex items-baseline justify-between pt-2 border-t border-slate-100">
                  <span className="text-xs text-slate-500">MSRP / Baseline</span>
                  <span className="text-lg font-extrabold text-slate-900">
                    ₹{item.product.basePrice?.toLocaleString()}
                  </span>
                </div>
              </div>

              <div className="pt-4 grid grid-cols-2 gap-2">
                <button
                  onClick={() => addProductToCompare(item.product)}
                  className="py-2 px-3 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1"
                >
                  <Scale className="w-3.5 h-3.5 text-purple-600" /> Compare
                </button>
                <button
                  onClick={() => onNavigate('search', { selectedProductId: item.product.id })}
                  className="py-2 px-3 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold transition-colors text-center"
                >
                  View Deals
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
