import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { UserProfile, PriceAlert, SavedProduct } from '../types';
import { api } from '../services/api';
import {
  User,
  Mail,
  Phone,
  Shield,
  Heart,
  Bell,
  Scale,
  Sliders,
  Trash2,
  CheckCircle2,
  AlertCircle,
  ExternalLink,
} from 'lucide-react';

interface MyAccountProps {
  onNavigate: (page: string, params?: any) => void;
}

export const MyAccount: React.FC<MyAccountProps> = ({ onNavigate }) => {
  const { user, refreshProfile, logout, isAuthenticated } = useAuth();
  const [alerts, setAlerts] = useState<PriceAlert[]>([]);
  const [savedProducts, setSavedProducts] = useState<SavedProduct[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [fullName, setFullName] = useState<string>('');
  const [phoneNumber, setPhoneNumber] = useState<string>('');
  const [updateMsg, setUpdateMsg] = useState<string | null>(null);

  // Preference fields
  const [preferredCategory, setPreferredCategory] = useState<string>('Laptops');
  const [maxBudget, setMaxBudget] = useState<number>(75000);

  useEffect(() => {
    if (!isAuthenticated) {
      onNavigate('login');
      return;
    }

    if (user) {
      setFullName(user.fullName);
      setPhoneNumber(user.phoneNumber || '');
      if (user.preferences) {
        setPreferredCategory(user.preferences.preferredCategory || 'Laptops');
        setMaxBudget(user.preferences.maxBudget || 75000);
      }
    }

    const loadAccountData = async () => {
      setLoading(true);
      try {
        const [alertRes, savedRes] = await Promise.all([
          api.getUserPriceAlerts(),
          api.getSavedProducts(),
        ]);
        if (alertRes.success && alertRes.data) setAlerts(alertRes.data);
        if (savedRes.success && savedRes.data) setSavedProducts(savedRes.data);
      } catch (e) {
        console.error('Error loading account data', e);
      } finally {
        setLoading(false);
      }
    };
    loadAccountData();
  }, [user, isAuthenticated]);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.updateProfile({ fullName, phoneNumber });
      if (res.success) {
        setUpdateMsg('Profile updated successfully!');
        await refreshProfile();
        setTimeout(() => setUpdateMsg(null), 3000);
      }
    } catch {
      setUpdateMsg('Update failed. Please try again.');
    }
  };

  const handleUpdatePreferences = async () => {
    try {
      const res = await api.updatePreferences({ preferredCategory, maxBudget });
      if (res.success) {
        setUpdateMsg('Preferences saved!');
        await refreshProfile();
        setTimeout(() => setUpdateMsg(null), 3000);
      }
    } catch {
      setUpdateMsg('Could not save preferences.');
    }
  };

  const handleDeleteAlert = async (id: number) => {
    await api.deletePriceAlert(id);
    setAlerts(prev => prev.filter(a => a.id !== id));
  };

  const handleDeleteAccount = async () => {
    if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      await fetch('http://localhost:8080/api/account', {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${localStorage.getItem('pricehub_token')}` },
      });
      logout();
      onNavigate('home');
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header Profile Greeting */}
      <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 text-white rounded-3xl p-8 shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-purple-400 to-teal-400 text-slate-950 flex items-center justify-center font-extrabold text-2xl shadow-lg">
            {user?.fullName?.charAt(0).toUpperCase() || 'U'}
          </div>
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-xs font-extrabold uppercase px-2.5 py-0.5 rounded-full bg-teal-400/20 text-teal-300 border border-teal-400/30">
                {user?.role === 'ROLE_ADMIN' ? 'Administrator' : 'Verified Member'}
              </span>
              <span className="text-xs text-slate-400">Account Active</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold brand-font">
              Welcome, {user?.fullName || 'Shopper'}!
            </h1>
            <p className="text-xs text-slate-300">{user?.email}</p>
          </div>
        </div>
      </div>

      {updateMsg && (
        <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 p-4 rounded-2xl flex items-center gap-2 text-xs font-bold animate-in fade-in">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>{updateMsg}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Left Col: Personal Details & Preferences (5 Cols) */}
        <div className="lg:col-span-5 space-y-6">
          {/* Profile Form */}
          <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
            <h3 className="font-bold text-sm text-slate-900 flex items-center gap-2">
              <User className="w-4 h-4 text-purple-600" /> Profile Information
            </h3>

            <form onSubmit={handleUpdateProfile} className="space-y-3.5 text-xs">
              <div className="space-y-1">
                <label className="font-bold text-slate-700 block">Full Name</label>
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 font-medium"
                />
              </div>

              <div className="space-y-1">
                <label className="font-bold text-slate-700 block">Email Address</label>
                <input
                  type="email"
                  value={user?.email || ''}
                  disabled
                  className="w-full px-3 py-2 bg-slate-100 border border-slate-200 rounded-xl text-slate-500 font-mono"
                />
              </div>

              <div className="space-y-1">
                <label className="font-bold text-slate-700 block">Phone Number</label>
                <input
                  type="text"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  placeholder="+91 9876543210"
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 font-medium"
                />
              </div>

              <button
                type="submit"
                className="w-full py-2.5 bg-purple-600 hover:bg-purple-700 text-white rounded-xl font-bold text-xs shadow-sm transition-colors"
              >
                Update Profile
              </button>
            </form>
          </div>

          {/* Saved Shopping Preferences */}
          <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
            <h3 className="font-bold text-sm text-slate-900 flex items-center gap-2">
              <Sliders className="w-4 h-4 text-teal-600" /> Shopping Defaults
            </h3>

            <div className="space-y-3.5 text-xs">
              <div className="space-y-1">
                <label className="font-bold text-slate-700 block">Preferred Category</label>
                <select
                  value={preferredCategory}
                  onChange={(e) => setPreferredCategory(e.target.value)}
                  className="w-full bg-slate-50 text-slate-800 font-semibold py-2 px-3 rounded-xl border border-slate-200"
                >
                  <option value="Laptops">Laptops</option>
                  <option value="Smartphones">Smartphones</option>
                  <option value="Headphones">Headphones</option>
                  <option value="Smartwatches">Smartwatches</option>
                  <option value="TVs">TVs</option>
                </select>
              </div>

              <div className="space-y-1">
                <div className="flex justify-between font-bold text-slate-700">
                  <span>Default Budget</span>
                  <span className="text-purple-700 font-extrabold">₹{maxBudget.toLocaleString()}</span>
                </div>
                <input
                  type="range"
                  min="20000"
                  max="150000"
                  step="5000"
                  value={maxBudget}
                  onChange={(e) => setMaxBudget(Number(e.target.value))}
                  className="w-full accent-purple-600"
                />
              </div>

              <button
                onClick={handleUpdatePreferences}
                className="w-full py-2.5 bg-teal-600 hover:bg-teal-700 text-white rounded-xl font-bold text-xs shadow-sm transition-colors"
              >
                Save Shopping Defaults
              </button>
            </div>
          </div>
        </div>

        {/* Right Col: Active Price Alerts & Wishlist (7 Cols) */}
        <div className="lg:col-span-7 space-y-6">
          {/* Active Price Alerts */}
          <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="font-bold text-sm text-slate-900 flex items-center gap-2">
                <Bell className="w-4 h-4 text-purple-600" /> Active Price Alerts ({alerts.length})
              </h3>
              <button
                onClick={() => onNavigate('compare')}
                className="text-[11px] text-purple-700 font-bold hover:underline"
              >
                Set Alert +
              </button>
            </div>

            <div className="p-3 bg-purple-50 rounded-xl border border-purple-100 text-[11px] text-purple-900 flex items-start gap-2">
              <span className="font-bold shrink-0">ℹ️</span>
              <span>Alerts are checked against the collected prices when you open this page and after new prices are imported. No emails or notifications are sent.</span>
            </div>

            {alerts.length === 0 ? (
              <p className="text-xs text-slate-400 text-center py-6">
                No active price alerts. Open any product on the <b>Compare</b> page to set a target price alert.
              </p>
            ) : (
              <div className="space-y-3">
                {alerts.map((alert) => {
                  const isMet = alert.status?.startsWith('Target met');
                  return (
                    <div
                      key={alert.id}
                      className={`p-3.5 rounded-2xl border flex items-center justify-between gap-4 text-xs ${
                        isMet ? 'bg-emerald-50/50 border-emerald-200' : 'bg-slate-50 border-slate-200'
                      }`}
                    >
                      <div className="space-y-1">
                        <p className="font-bold text-slate-900">{alert.productTitle}</p>
                        <div className="flex flex-wrap items-center gap-2 text-[11px] text-slate-500">
                          <span>Target: <b className="text-purple-700">₹{alert.targetPrice?.toLocaleString()}</b></span>
                          <span>• Store: {alert.marketplaceName}</span>
                        </div>
                        {alert.status && (
                          <div className="pt-0.5">
                            <span
                              className={`inline-block px-2 py-0.5 rounded-md text-[10px] font-extrabold ${
                                isMet ? 'bg-emerald-600 text-white' : 'bg-amber-100 text-amber-900 border border-amber-300'
                              }`}
                            >
                              {alert.status}
                            </span>
                          </div>
                        )}
                      </div>

                      <button
                        onClick={() => handleDeleteAlert(alert.id)}
                        className="p-1.5 text-slate-400 hover:text-red-500 rounded-full hover:bg-red-50"
                        title="Remove alert"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Quick Saved Products Shortcut */}
          <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="font-bold text-sm text-slate-900 flex items-center gap-2">
                <Heart className="w-4 h-4 text-rose-500" /> Saved Wishlist Items ({savedProducts.length})
              </h3>
              <button
                onClick={() => onNavigate('saved')}
                className="text-[11px] text-purple-700 font-bold hover:underline"
              >
                View Wishlist →
              </button>
            </div>

            {savedProducts.length === 0 ? (
              <p className="text-xs text-slate-400 text-center py-6">
                Your wishlist is currently empty. Click the heart icon on any product to save it.
              </p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {savedProducts.slice(0, 4).map((sp) => (
                  <div
                    key={sp.id}
                    onClick={() => onNavigate('search', { selectedProductId: sp.product.id })}
                    className="p-3 bg-slate-50 hover:bg-purple-50 rounded-2xl border border-slate-200 cursor-pointer transition-colors text-xs flex items-center gap-3"
                  >
                    <img src={sp.product.imageUrl} alt={sp.product.title} className="w-10 h-10 object-contain rounded bg-white p-1" />
                    <div className="truncate">
                      <p className="font-bold text-slate-900 truncate">{sp.product.title}</p>
                      <p className="text-purple-700 font-extrabold text-[11px]">₹{sp.product.basePrice?.toLocaleString()}</p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Danger Zone: Account Deletion */}
          <div className="p-6 bg-red-50/70 border border-red-200 rounded-3xl space-y-2 text-xs">
            <h4 className="font-bold text-red-900">Account Management</h4>
            <p className="text-red-700">
              Permanently close and delete your PriceHub account and all associated saved preferences.
            </p>
            <button
              onClick={handleDeleteAccount}
              className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-xl font-bold transition-colors"
            >
              Request Account Deletion
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
