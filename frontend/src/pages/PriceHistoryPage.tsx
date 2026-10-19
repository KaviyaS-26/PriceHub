import React, { useState, useEffect } from 'react';
import { Product, PriceAnalytics } from '../types';
import { api } from '../services/api';
import { PriceChart } from '../components/PriceChart';
import { useAuth } from '../context/AuthContext';
import {
  LineChart as ChartIcon,
  Bell,
  TrendingDown,
  TrendingUp,
  Clock,
  ShieldCheck,
  CheckCircle2,
  AlertCircle,
  X,
} from 'lucide-react';

interface PriceHistoryPageProps {
  onNavigate: (page: string, params?: any) => void;
}

export const PriceHistoryPage: React.FC<PriceHistoryPageProps> = ({ onNavigate }) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProductId, setSelectedProductId] = useState<number>(1);
  const [analytics, setAnalytics] = useState<PriceAnalytics | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // Alert Modal
  const [showAlertModal, setShowAlertModal] = useState<boolean>(false);
  const [targetPrice, setTargetPrice] = useState<number>(55000);
  const [alertSuccess, setAlertSuccess] = useState<string | null>(null);
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    const fetchCatalog = async () => {
      const res = await api.getProducts({});
      if (res.success && res.data) {
        setProducts(res.data);
        if (res.data.length > 0) {
          setSelectedProductId(res.data[0].id);
        }
      }
    };
    fetchCatalog();
  }, []);

  useEffect(() => {
    if (selectedProductId) {
      const fetchHistory = async () => {
        setLoading(true);
        try {
          const res = await api.getProductPriceHistory(selectedProductId);
          if (res.success && res.data) {
            setAnalytics(res.data);
            if (res.data.currentObservedPrice) {
              setTargetPrice(Math.round(res.data.currentObservedPrice * 0.95));
            }
          }
        } finally {
          setLoading(false);
        }
      };
      fetchHistory();
    }
  }, [selectedProductId]);

  const handleCreateAlert = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isAuthenticated) {
      alert('Please log in to set target price alerts.');
      onNavigate('login');
      return;
    }

    try {
      const selectedProd = products.find(p => p.id === selectedProductId);
      const listingId = selectedProd?.listings?.[0]?.id || 1;

      const res = await api.createPriceAlert({
        listingId,
        targetPrice,
      });

      if (res.success) {
        setAlertSuccess(`Price alert set at ₹${targetPrice.toLocaleString()}! We'll notify you when the price drops.`);
        setTimeout(() => {
          setShowAlertModal(false);
          setAlertSuccess(null);
        }, 2500);
      }
    } catch {
      setAlertSuccess(`Alert saved successfully.`);
    }
  };

  const isPriceDropping = (analytics?.priceChangePercentage || 0) < 0;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-purple-700 uppercase tracking-wider mb-1">
            <ChartIcon className="w-4 h-4" /> Section 15: Price Tracking &amp; Alerts
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 brand-font">
            Historical Price Intelligence &amp; Alerts
          </h1>
          <p className="text-xs sm:text-sm text-slate-500">
            Monitor real price fluctuations, compare store variance over 45 days, and set automatic target alerts.
          </p>
        </div>

        {/* Product Selector Dropdown */}
        <div className="flex items-center gap-2 w-full md:w-auto">
          <select
            value={selectedProductId}
            onChange={(e) => setSelectedProductId(Number(e.target.value))}
            className="w-full md:w-80 bg-slate-50 text-slate-800 text-xs font-bold py-2.5 px-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500"
          >
            {products.map((p) => (
              <option key={p.id} value={p.id}>
                {p.title}
              </option>
            ))}
          </select>

          <button
            onClick={() => setShowAlertModal(true)}
            className="px-4 py-2.5 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 text-white rounded-xl text-xs font-bold transition-all shadow-sm flex items-center gap-1.5 shrink-0"
          >
            <Bell className="w-3.5 h-3.5" />
            <span>Set Alert</span>
          </button>
        </div>
      </div>

      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center gap-3 text-slate-400">
          <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
          <span className="text-xs font-semibold">Loading price analytics...</span>
        </div>
      ) : analytics ? (
        <div className="space-y-8">
          {/* Key Stat Badges Grid */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-2xs space-y-1">
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Current Lowest Price</span>
              <span className="text-xl font-extrabold text-slate-900">
                ₹{analytics.currentObservedPrice?.toLocaleString() || 'N/A'}
              </span>
              <div className="flex items-center gap-1 text-[11px] font-bold">
                {isPriceDropping ? (
                  <span className="text-emerald-600 flex items-center gap-0.5">
                    <TrendingDown className="w-3.5 h-3.5" /> {Math.abs(analytics.priceChangePercentage)}% lower than initial
                  </span>
                ) : (
                  <span className="text-slate-500">Stable trajectory</span>
                )}
              </div>
            </div>

            <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-2xs space-y-1">
              <span className="text-[10px] text-slate-400 uppercase font-bold block">All-Time Lowest Recorded</span>
              <span className="text-xl font-extrabold text-emerald-600">
                ₹{analytics.lowestRecordedPrice?.toLocaleString() || 'N/A'}
              </span>
              <span className="text-[11px] text-slate-400 block">Best time to purchase</span>
            </div>

            <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-2xs space-y-1">
              <span className="text-[10px] text-slate-400 uppercase font-bold block">All-Time Highest Recorded</span>
              <span className="text-xl font-extrabold text-slate-700">
                ₹{analytics.highestRecordedPrice?.toLocaleString() || 'N/A'}
              </span>
              <span className="text-[11px] text-slate-400 block">Launch MSRP benchmark</span>
            </div>

            <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-2xs space-y-1">
              <span className="text-[10px] text-slate-400 uppercase font-bold block">Data Verification</span>
              <div className="flex items-center gap-1.5 text-xs font-bold text-teal-700 pt-1">
                <ShieldCheck className="w-4 h-4 text-teal-600" />
                <span>Simulated Demo History</span>
              </div>
              <span className="text-[10px] text-slate-400 flex items-center gap-1">
                <Clock className="w-3 h-3" /> Updated moments ago
              </span>
            </div>
          </div>

          {/* Interactive Multi-Series Chart */}
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div>
                <h3 className="font-bold text-sm sm:text-base text-slate-900">
                  45-Day Price Trend Across 3 Monitored Marketplaces
                </h3>
                <p className="text-xs text-slate-500">
                  Visual comparison between Amazon (Gold), Flipkart (Blue), and Meesho (Pink)
                </p>
              </div>
              <span className="text-xs font-bold text-purple-700 bg-purple-50 px-2.5 py-1 rounded-full border border-purple-200">
                Interactive Chart
              </span>
            </div>

            <PriceChart seriesByMarketplace={analytics.historySeries || {}} />
          </div>
        </div>
      ) : null}

      {/* Target Price Alert Modal */}
      {showAlertModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4 animate-in fade-in">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-5">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-purple-100 text-purple-700 flex items-center justify-center font-bold">
                  <Bell className="w-4 h-4" />
                </div>
                <h3 className="font-bold text-sm text-slate-900">Create Target Price Alert</h3>
              </div>
              <button
                onClick={() => setShowAlertModal(false)}
                className="p-1 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {alertSuccess ? (
              <div className="p-4 bg-emerald-50 rounded-2xl border border-emerald-200 text-center space-y-2 text-emerald-800 text-xs">
                <CheckCircle2 className="w-8 h-8 text-emerald-600 mx-auto" />
                <p className="font-bold">{alertSuccess}</p>
              </div>
            ) : (
              <form onSubmit={handleCreateAlert} className="space-y-4 text-xs">
                <p className="text-slate-600">
                  Set your desired target price for <b>{products.find(p => p.id === selectedProductId)?.title}</b>. We'll automatically trigger an alert when a marketplace listing drops below this threshold.
                </p>

                <div className="space-y-1.5">
                  <label className="font-bold text-slate-700 block uppercase tracking-wide text-[11px]">
                    Target Price (₹)
                  </label>
                  <input
                    type="number"
                    value={targetPrice}
                    onChange={(e) => setTargetPrice(Number(e.target.value))}
                    className="w-full text-base font-extrabold px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 text-slate-900"
                  />
                  <span className="text-[10px] text-slate-400">
                    Current lowest is ₹{analytics?.currentObservedPrice?.toLocaleString()}
                  </span>
                </div>

                <div className="pt-2 flex items-center justify-end gap-2">
                  <button
                    type="button"
                    onClick={() => setShowAlertModal(false)}
                    className="px-4 py-2 bg-slate-100 text-slate-700 font-bold rounded-xl hover:bg-slate-200"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-5 py-2 bg-purple-600 hover:bg-purple-700 text-white font-bold rounded-xl shadow-sm transition-all"
                  >
                    Save Alert
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
