import React, { useState } from 'react';
import { Logo } from './Logo';
import { useAuth } from '../context/AuthContext';
import { useComparison } from '../context/ComparisonContext';
import {
  Search,
  SlidersHorizontal,
  Sparkles,
  Scale,
  TrendingDown,
  LineChart,
  User,
  Heart,
  Menu,
  X,
  LogOut,
  ChevronDown,
  Info,
} from 'lucide-react';

interface NavbarProps {
  onNavigate: (page: string, params?: any) => void;
  currentPage: string;
}

export const Navbar: React.FC<NavbarProps> = ({ onNavigate, currentPage }) => {
  const { user, isAuthenticated, logout } = useAuth();
  const { comparedProducts, setIsDrawerOpen } = useComparison();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      onNavigate('search', { keyword: searchQuery.trim() });
    }
  };

  const navLinks = [
    { id: 'home', label: 'Home' },
    { id: 'search', label: 'Explore Products' },
    { id: 'compare', label: 'Compare', badge: comparedProducts.length > 0 ? comparedProducts.length : null },
    { id: 'deals', label: 'Best Deals' },
    { id: 'deallab', label: 'Personalized Deal Lab', highlight: true },
    { id: 'history', label: 'Price History' },
  ];

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200 shadow-sm transition-all">
      {/* Top Banner with Collected data indicator */}
      <div className="bg-gradient-to-r from-purple-700 via-indigo-600 to-teal-600 text-white text-xs font-medium py-1 px-4 text-center flex items-center justify-between">
        <div className="hidden md:flex items-center gap-2">
          <span className="inline-block w-2 h-2 rounded-full bg-teal-400"></span>
          <span>Compare prices across marketplaces (Amazon, Flipkart &amp; Meesho)</span>
        </div>
        <div className="mx-auto md:mx-0 flex items-center gap-3">
          <span className="bg-white/20 text-white border border-white/30 px-2.5 py-0.5 rounded-full text-[11px] font-bold tracking-wider">
            Collected data, 15-17 Oct 2026
          </span>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16 gap-4">
          {/* Logo */}
          <div
            onClick={() => onNavigate('home')}
            className="cursor-pointer shrink-0"
          >
            <Logo variant="full" size="md" />
          </div>

          {/* Search Bar (Desktop) */}
          <form
            onSubmit={handleSearchSubmit}
            className="hidden lg:flex flex-1 max-w-md relative items-center"
          >
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search MacBook, iPhone 15, Sony TV, Dell..."
              className="w-full bg-slate-100/90 text-slate-900 pl-10 pr-20 py-2 rounded-full text-sm border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:bg-white transition-all shadow-inner"
            />
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5" />
            <button
              type="submit"
              className="absolute right-1.5 px-3 py-1 bg-purple-600 hover:bg-purple-700 text-white text-xs font-semibold rounded-full transition-colors shadow-sm"
            >
              Search
            </button>
          </form>

          {/* Desktop Navigation Links */}
          <nav className="hidden xl:flex items-center space-x-1">
            {navLinks.map((link) => {
              const isActive = currentPage === link.id;
              return (
                <button
                  key={link.id}
                  onClick={() => onNavigate(link.id)}
                  className={`px-3 py-2 rounded-lg text-xs font-semibold transition-all relative flex items-center gap-1.5 ${
                    isActive
                      ? 'bg-purple-50 text-purple-700 shadow-sm border border-purple-200'
                      : link.highlight
                      ? 'text-purple-700 hover:bg-purple-50 font-bold'
                      : 'text-slate-600 hover:text-purple-600 hover:bg-slate-50'
                  }`}
                >
                  {link.highlight && <Sparkles className="w-3.5 h-3.5 text-teal-500 animate-pulse" />}
                  {link.label}
                  {link.badge && (
                    <span className="bg-purple-600 text-white text-[10px] px-1.5 py-0.2 rounded-full font-bold">
                      {link.badge}
                    </span>
                  )}
                </button>
              );
            })}
          </nav>

          {/* Action Buttons (Right) */}
          <div className="flex items-center gap-2">
            {/* Compare Tray quick trigger */}
            {comparedProducts.length > 0 && (
              <button
                onClick={() => setIsDrawerOpen(true)}
                className="hidden sm:flex items-center gap-1.5 px-3 py-1.5 bg-teal-50 border border-teal-200 text-teal-800 rounded-full text-xs font-bold hover:bg-teal-100 transition-colors shadow-sm"
              >
                <Scale className="w-3.5 h-3.5 text-teal-600" />
                <span>Compare ({comparedProducts.length})</span>
              </button>
            )}



            {/* User Account / Login */}
            {isAuthenticated && user ? (
              <div className="relative">
                <button
                  onClick={() => setProfileDropdownOpen(!profileDropdownOpen)}
                  className="flex items-center gap-2 pl-2 pr-3 py-1.5 rounded-full bg-slate-100 hover:bg-purple-50 border border-slate-200 text-slate-800 text-xs font-semibold transition-all"
                >
                  <div className="w-6 h-6 rounded-full bg-purple-600 text-white flex items-center justify-center font-bold text-xs shadow-sm">
                    {user.fullName.charAt(0).toUpperCase()}
                  </div>
                  <span className="max-w-[100px] truncate">{user.fullName.split(' ')[0]}</span>
                  <ChevronDown className="w-3 h-3 text-slate-500" />
                </button>

                {profileDropdownOpen && (
                  <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl border border-slate-200 py-2 z-50 animate-in fade-in slide-in-from-top-2 duration-150">
                    <div className="px-4 py-2 border-b border-slate-100">
                      <p className="text-xs font-bold text-slate-800">{user.fullName}</p>
                      <p className="text-[11px] text-slate-500 truncate">{user.email}</p>
                    </div>

                    <button
                      onClick={() => { setProfileDropdownOpen(false); onNavigate('account'); }}
                      className="w-full text-left px-4 py-2 text-xs text-slate-700 hover:bg-purple-50 hover:text-purple-700 flex items-center gap-2"
                    >
                      <User className="w-3.5 h-3.5 text-purple-600" /> My Account &amp; Dashboard
                    </button>

                    <button
                      onClick={() => { setProfileDropdownOpen(false); onNavigate('saved'); }}
                      className="w-full text-left px-4 py-2 text-xs text-slate-700 hover:bg-purple-50 hover:text-purple-700 flex items-center gap-2"
                    >
                      <Heart className="w-3.5 h-3.5 text-rose-500" /> Saved Products
                    </button>



                    <div className="border-t border-slate-100 my-1"></div>

                    <button
                      onClick={() => { setProfileDropdownOpen(false); logout(); onNavigate('home'); }}
                      className="w-full text-left px-4 py-2 text-xs text-red-600 hover:bg-red-50 flex items-center gap-2 font-medium"
                    >
                      <LogOut className="w-3.5 h-3.5" /> Log Out
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex items-center gap-1.5">
                <button
                  onClick={() => onNavigate('login')}
                  className="px-3 py-1.5 text-xs font-semibold text-purple-700 hover:bg-purple-50 rounded-lg transition-colors"
                >
                  Log In
                </button>
                <button
                  onClick={() => onNavigate('register')}
                  className="px-3.5 py-1.5 text-xs font-semibold bg-purple-600 hover:bg-purple-700 text-white rounded-lg shadow-sm hover:shadow transition-all"
                >
                  Register
                </button>
              </div>
            )}

            {/* Mobile Menu Toggle */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="xl:hidden p-2 rounded-lg text-slate-600 hover:text-slate-900 hover:bg-slate-100"
            >
              {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div className="xl:hidden border-t border-slate-200 bg-white px-4 pt-3 pb-6 space-y-2 shadow-lg">
          <form onSubmit={handleSearchSubmit} className="relative mb-3">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search products..."
              className="w-full bg-slate-100 text-slate-900 pl-10 pr-4 py-2 rounded-lg text-sm border border-slate-200"
            />
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          </form>

          {navLinks.map((link) => (
            <button
              key={link.id}
              onClick={() => { setMobileMenuOpen(false); onNavigate(link.id); }}
              className={`w-full text-left px-3 py-2.5 rounded-lg text-sm font-semibold flex items-center justify-between ${
                currentPage === link.id ? 'bg-purple-50 text-purple-700 font-bold' : 'text-slate-700 hover:bg-slate-50'
              }`}
            >
              <span className="flex items-center gap-2">
                {link.highlight && <Sparkles className="w-4 h-4 text-teal-500" />}
                {link.label}
              </span>
              {link.badge && (
                <span className="bg-purple-600 text-white text-xs px-2 py-0.5 rounded-full">
                  {link.badge}
                </span>
              )}
            </button>
          ))}

          <button
            onClick={() => { setMobileMenuOpen(false); onNavigate('about'); }}
            className="w-full text-left px-3 py-2.5 rounded-lg text-sm font-semibold text-slate-600 hover:bg-slate-50 flex items-center gap-2"
          >
            <Info className="w-4 h-4 text-slate-400" /> About PriceHub &amp; Architecture
          </button>
        </div>
      )}
    </header>
  );
};
