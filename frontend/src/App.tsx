import React, { useState } from 'react';
import { AuthProvider } from './context/AuthContext';
import { ComparisonProvider } from './context/ComparisonContext';

import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { ComparisonDrawer } from './components/ComparisonDrawer';

// Pages
import { Home } from './pages/Home';
import { SearchResults } from './pages/SearchResults';
import { ProductComparison } from './pages/ProductComparison';
import { BestDeals } from './pages/BestDeals';
import { DealLab } from './pages/DealLab';
import { PriceHistoryPage } from './pages/PriceHistoryPage';
import { MyAlertsPage } from './pages/MyAlertsPage';
import { MyAccount } from './pages/MyAccount';
import { SavedProducts } from './pages/SavedProducts';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { AboutPage } from './pages/AboutPage';

export function AppContent() {
  const [currentPage, setCurrentPage] = useState<string>('home');
  const [pageParams, setPageParams] = useState<any>(null);

  const handleNavigate = (page: string, params?: any) => {
    setCurrentPage(page);
    setPageParams(params || null);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const renderPage = () => {
    switch (currentPage) {
      case 'home':
        return <Home onNavigate={handleNavigate} />;
      case 'search':
        return <SearchResults onNavigate={handleNavigate} initialParams={pageParams} />;
      case 'compare':
        return <ProductComparison onNavigate={handleNavigate} initialParams={pageParams} />;
      case 'deals':
        return <BestDeals onNavigate={handleNavigate} />;
      case 'deallab':
        return <DealLab onNavigate={handleNavigate} />;
      case 'history':
        return <PriceHistoryPage onNavigate={handleNavigate} />;
      case 'alerts':
        return <MyAlertsPage onNavigate={handleNavigate} />;
      case 'account':
        return <MyAccount onNavigate={handleNavigate} />;
      case 'saved':
        return <SavedProducts onNavigate={handleNavigate} />;
      case 'login':
        return <LoginPage onNavigate={handleNavigate} />;
      case 'register':
        return <RegisterPage onNavigate={handleNavigate} />;
      case 'forgot-password':
        return <ForgotPasswordPage onNavigate={handleNavigate} />;
      case 'about':
        return <AboutPage onNavigate={handleNavigate} />;
      default:
        return <Home onNavigate={handleNavigate} />;
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-900 selection:bg-purple-500 selection:text-white">
      <Navbar onNavigate={handleNavigate} currentPage={currentPage} />
      <main className="flex-1">
        {renderPage()}
      </main>
      <ComparisonDrawer onNavigate={handleNavigate} />
      <Footer onNavigate={handleNavigate} />
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <ComparisonProvider>
        <AppContent />
      </ComparisonProvider>
    </AuthProvider>
  );
}
