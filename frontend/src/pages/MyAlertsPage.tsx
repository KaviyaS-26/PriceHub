import React, { useState, useEffect } from 'react';
import { PriceAlert } from '../types';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import {
  Bell,
  Trash2,
  CheckCircle2,
  AlertCircle,
  ExternalLink,
  ArrowRight,
  Info,
  Scale,
} from 'lucide-react';

interface MyAlertsPageProps {
  onNavigate: (page: string, params?: any) => void;
}

export const MyAlertsPage: React.FC<MyAlertsPageProps> = ({ onNavigate }) => {
  const { isAuthenticated } = useAuth();
  const [alerts, setAlerts] = useState<PriceAlert[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchAlerts = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.getUserPriceAlerts();
      if (res.success && res.data) {
        setAlerts(res.data);
      } else {
        setError(res.message || 'Unable to load alerts');
      }
    } catch (err: any) {
      setError(err?.message || 'Failed to connect to alerts API');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      fetchAlerts();
    } else {
      setLoading(false);
    }
  }, [isAuthenticated]);

  const handleDeleteAlert = async (id: number) => {
    try {
      await api.deletePriceAlert(id);
      setAlerts((prev) => prev.filter((a) => a.id !== id));
    } catch (err) {
      console.error('Failed to delete alert', err);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center space-y-4">
        <div className="w-16 h-16 rounded-full bg-purple-100 text-purple-700 flex items-center justify-center mx-auto">
          <Bell className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-extrabold text-slate-900">Please Log In to View Alerts</h2>
        <p className="text-xs text-slate-500 max-w-md mx-auto">
          You need an active PriceHub account to monitor product target prices and view your active alerts.
        </p>
        <button
          onClick={() => onNavigate('login')}
          className="px-6 py-2.5 bg-purple-600 hover:bg-purple-700 text-white text-xs font-bold rounded-xl transition-all shadow-md"
        >
          Log In
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8 animate-in fade-in duration-300">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 rounded-3xl p-8 text-white shadow-xl border border-purple-500/20 space-y-3">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs font-semibold text-purple-200">
          <Bell className="w-3.5 h-3.5 text-teal-300" />
          <span>Automated Price Monitoring</span>
        </div>
        <h1 className="text-2xl sm:text-4xl font-extrabold tracking-tight brand-font">
          My Price Alerts
        </h1>
        <p className="text-xs sm:text-sm text-slate-300 max-w-2xl">
          Set price alerts on any product from the Compare page. PriceHub calculates whether your target price is met across Amazon, Flipkart, and Meesho whenever you view this page.
        </p>
      </div>

      {/* Mandatory Disclaimer Callout */}
      <div className="p-4 bg-purple-50 border border-purple-200 rounded-2xl text-xs text-purple-900 flex items-start sm:items-center gap-3 shadow-2xs">
        <Info className="w-5 h-5 text-purple-600 shrink-0 mt-0.5 sm:mt-0" />
        <span className="font-medium">
          Alerts are checked against the collected prices when you open this page and after new prices are imported. No emails or notifications are sent.
        </span>
      </div>

      {/* Alerts Content */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-sm space-y-6">
        <div className="flex items-center justify-between border-b border-slate-100 pb-4">
          <h2 className="text-base font-extrabold text-slate-900 flex items-center gap-2">
            <span>Active Alerts</span>
            <span className="bg-purple-100 text-purple-800 text-xs px-2.5 py-0.5 rounded-full font-mono font-bold">
              {alerts.length}
            </span>
          </h2>
          <button
            onClick={() => onNavigate('compare')}
            className="text-xs font-bold text-purple-700 hover:text-purple-900 hover:underline flex items-center gap-1"
          >
            <span>Set New Alert</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

        {loading ? (
          <div className="py-20 flex flex-col items-center justify-center gap-3 text-slate-400">
            <div className="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
            <span className="text-xs font-medium">Checking alerts against current collected prices...</span>
          </div>
        ) : error ? (
          <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-xs text-rose-800">
            {error}
          </div>
        ) : alerts.length === 0 ? (
          <div className="py-16 text-center space-y-4">
            <div className="w-12 h-12 rounded-full bg-slate-100 text-slate-400 flex items-center justify-center mx-auto">
              <Bell className="w-6 h-6" />
            </div>
            <h3 className="text-sm font-bold text-slate-800">No active price alerts</h3>
            <p className="text-xs text-slate-500 max-w-sm mx-auto">
              Open any product on the <b>Compare</b> page and click <b>Set price alert</b> to monitor price drops.
            </p>
            <button
              onClick={() => onNavigate('compare')}
              className="px-5 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold transition-all shadow-xs inline-flex items-center gap-1.5"
            >
              <Scale className="w-3.5 h-3.5" />
              <span>Go to Compare</span>
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {alerts.map((alert) => {
              const isMet = alert.status?.startsWith('Target met');
              return (
                <div
                  key={alert.id}
                  className={`p-5 rounded-2xl border transition-all flex flex-col md:flex-row items-start md:items-center justify-between gap-4 ${
                    isMet
                      ? 'bg-emerald-50/40 border-emerald-200 hover:border-emerald-300'
                      : 'bg-slate-50/70 border-slate-200 hover:border-slate-300'
                  }`}
                >
                  <div className="flex items-start gap-4">
                    {alert.productImageUrl ? (
                      <div className="w-14 h-14 bg-white rounded-xl border border-slate-200 p-1 flex items-center justify-center shrink-0">
                        <img
                          src={alert.productImageUrl}
                          alt={alert.productTitle}
                          className="max-h-12 max-w-full object-contain"
                        />
                      </div>
                    ) : (
                      <div className="w-14 h-14 bg-purple-100 text-purple-700 rounded-xl flex items-center justify-center shrink-0 font-bold">
                        <Bell className="w-6 h-6" />
                      </div>
                    )}

                    <div className="space-y-1.5">
                      <h3 className="text-sm font-bold text-slate-900 leading-snug">
                        {alert.productTitle}
                      </h3>

                      <div className="flex flex-wrap items-center gap-3 text-xs text-slate-500">
                        <span>
                          Target: <b className="text-purple-700">₹{alert.targetPrice?.toLocaleString()}</b>
                        </span>
                        <span>•</span>
                        <span>
                          Store: <b className="text-slate-700">{alert.marketplaceName}</b>
                        </span>
                      </div>

                      {/* Dynamic Status Badge */}
                      <div className="pt-1">
                        <span
                          className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-extrabold ${
                            isMet
                              ? 'bg-emerald-600 text-white shadow-2xs'
                              : 'bg-amber-100 text-amber-900 border border-amber-300'
                          }`}
                        >
                          {isMet ? (
                            <CheckCircle2 className="w-3.5 h-3.5 text-white" />
                          ) : (
                            <AlertCircle className="w-3.5 h-3.5 text-amber-700" />
                          )}
                          <span>{alert.status || 'Checking status...'}</span>
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 self-end md:self-center">
                    {alert.productId && (
                      <button
                        onClick={() => onNavigate('compare', { productId: alert.productId })}
                        className="px-3 py-1.5 bg-white hover:bg-purple-50 text-purple-700 border border-slate-200 hover:border-purple-300 rounded-xl text-xs font-bold transition-colors flex items-center gap-1"
                      >
                        <span>Compare</span>
                        <ExternalLink className="w-3 h-3" />
                      </button>
                    )}

                    <button
                      onClick={() => handleDeleteAlert(alert.id)}
                      className="p-2 text-slate-400 hover:text-rose-600 rounded-xl hover:bg-rose-50 transition-colors"
                      title="Delete price alert"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
