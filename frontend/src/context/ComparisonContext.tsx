import React, { createContext, useContext, useState, useEffect } from 'react';
import { Product } from '../types';

interface ComparisonContextType {
  comparedProducts: Product[];
  addProductToCompare: (product: Product) => boolean;
  removeProductFromCompare: (productId: number) => void;
  clearComparison: () => void;
  isProductCompared: (productId: number) => boolean;
  isDrawerOpen: boolean;
  setIsDrawerOpen: (open: boolean) => void;
}

const ComparisonContext = createContext<ComparisonContextType | undefined>(undefined);

export const ComparisonProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [comparedProducts, setComparedProducts] = useState<Product[]>(() => {
    const saved = localStorage.getItem('pricehub_compared');
    return saved ? JSON.parse(saved) : [];
  });

  const [isDrawerOpen, setIsDrawerOpen] = useState<boolean>(false);

  useEffect(() => {
    localStorage.setItem('pricehub_compared', JSON.stringify(comparedProducts));
  }, [comparedProducts]);

  const addProductToCompare = (product: Product): boolean => {
    if (comparedProducts.some(p => p.id === product.id)) {
      return false; // Already present
    }
    if (comparedProducts.length >= 4) {
      alert('You can compare a maximum of 4 products at a time.');
      return false;
    }
    setComparedProducts(prev => [...prev, product]);
    setIsDrawerOpen(true);
    return true;
  };

  const removeProductFromCompare = (productId: number) => {
    setComparedProducts(prev => prev.filter(p => p.id !== productId));
  };

  const clearComparison = () => {
    setComparedProducts([]);
    setIsDrawerOpen(false);
  };

  const isProductCompared = (productId: number) => {
    return comparedProducts.some(p => p.id === productId);
  };

  return (
    <ComparisonContext.Provider
      value={{
        comparedProducts,
        addProductToCompare,
        removeProductFromCompare,
        clearComparison,
        isProductCompared,
        isDrawerOpen,
        setIsDrawerOpen,
      }}
    >
      {children}
    </ComparisonContext.Provider>
  );
};

export const useComparison = () => {
  const context = useContext(ComparisonContext);
  if (!context) {
    throw new Error('useComparison must be used within a ComparisonProvider');
  }
  return context;
};
