import React from 'react';
import { useComparison } from '../context/ComparisonContext';
import { Scale, X, Trash2, ArrowRight } from 'lucide-react';

interface ComparisonDrawerProps {
  onNavigate: (page: string, params?: any) => void;
}

export const ComparisonDrawer: React.FC<ComparisonDrawerProps> = ({ onNavigate }) => {
  const { comparedProducts, removeProductFromCompare, clearComparison, isDrawerOpen, setIsDrawerOpen } = useComparison();

  if (!isDrawerOpen || comparedProducts.length === 0) {
    return null;
  }

  return (
    <div className="fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t-2 border-purple-500 shadow-2xl transition-all duration-300 animate-in slide-in-from-bottom">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3">
        <div className="flex flex-col sm:flex-row items-center justify-between gap-3">
          {/* Header info */}
          <div className="flex items-center gap-3 w-full sm:w-auto justify-between sm:justify-start">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-full bg-purple-100 text-purple-700 flex items-center justify-center font-bold">
                <Scale className="w-4 h-4" />
              </div>
              <div>
                <h4 className="text-xs font-bold text-slate-900">
                  Comparing {comparedProducts.length} of 4 Products
                </h4>
                <p className="text-[11px] text-slate-500">Side-by-side spec &amp; marketplace comparison</p>
              </div>
            </div>

            <button
              onClick={() => setIsDrawerOpen(false)}
              className="sm:hidden p-1 text-slate-400 hover:text-slate-600"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Product Thumbnails */}
          <div className="flex items-center gap-3 overflow-x-auto max-w-full py-1">
            {comparedProducts.map((product) => (
              <div
                key={product.id}
                onClick={() => { setIsDrawerOpen(false); onNavigate('compare', { productId: product.id }); }}
                className="flex items-center gap-2 bg-slate-50 hover:bg-purple-50 border border-slate-200 hover:border-purple-300 rounded-xl p-1.5 pr-2.5 shrink-0 shadow-2xs relative group cursor-pointer transition-colors"
              >
                <img
                  src={product.imageUrl}
                  alt={product.title}
                  className="w-8 h-8 object-contain rounded bg-white p-0.5"
                />
                <div className="max-w-[120px]">
                  <p className="text-[11px] font-semibold text-slate-800 truncate">{product.title}</p>
                  <p className="text-[10px] text-purple-700 font-bold">₹{product.lowestEffectivePrice?.toLocaleString() || product.basePrice.toLocaleString()}</p>
                </div>
                <button
                  onClick={(e) => { e.stopPropagation(); removeProductFromCompare(product.id); }}
                  className="p-1 rounded-full text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                  title="Remove"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </div>
            ))}
          </div>

          {/* Actions */}
          <div className="flex items-center gap-2 w-full sm:w-auto justify-end">
            <button
              onClick={clearComparison}
              className="px-3 py-1.5 text-xs text-slate-500 hover:text-red-600 font-medium transition-colors flex items-center gap-1"
            >
              <Trash2 className="w-3.5 h-3.5" /> Clear All
            </button>
            <button
              onClick={() => {
                setIsDrawerOpen(false);
                if (comparedProducts.length > 0) {
                  onNavigate('compare', { productId: comparedProducts[0].id });
                } else {
                  onNavigate('compare');
                }
              }}
              disabled={comparedProducts.length === 0}
              className="px-4 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-slate-300 text-white rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 shadow-md hover:shadow-lg disabled:shadow-none"
            >
              <span>Compare Marketplaces</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
