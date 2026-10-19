import React, { createContext, useContext, useState, useEffect } from 'react';
import { UserProfile } from '../types';
import { api } from '../services/api';

interface AuthContextType {
  user: UserProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, pass: string) => Promise<{ success: boolean; message?: string }>;
  loginAsDemo: () => Promise<void>;
  register: (data: any) => Promise<{ success: boolean; message?: string }>;
  logout: () => void;
  refreshProfile: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('pricehub_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const refreshProfile = async () => {
    if (!token) {
      setUser(null);
      setIsLoading(false);
      return;
    }
    try {
      const res = await api.getProfile();
      if (res.success && res.data) {
        setUser(res.data);
      } else {
        logout();
      }
    } catch {
      // Fallback demo user if backend is offline
      setUser({
        id: 1,
        fullName: 'Kaviya Sekar',
        email: 'demo@pricehub.com',
        role: 'ROLE_USER',
        accountStatus: 'ACTIVE',
        createdAt: new Date().toISOString(),
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    refreshProfile();
  }, [token]);

  const login = async (email: string, pass: string) => {
    try {
      const res = await api.login({ email, password: pass });
      if (res.success && res.data?.token) {
        localStorage.setItem('pricehub_token', res.data.token);
        setToken(res.data.token);
        await refreshProfile();
        return { success: true };
      }
      return { success: false, message: res.message || 'Invalid credentials' };
    } catch (err: any) {
      return { success: false, message: 'Server connection error. Please try again.' };
    }
  };

  const loginAsDemo = async () => {
    await login('demo@pricehub.com', 'Password123');
  };

  const register = async (data: any) => {
    try {
      const res = await api.register(data);
      if (res.success && res.data?.token) {
        localStorage.setItem('pricehub_token', res.data.token);
        setToken(res.data.token);
        await refreshProfile();
        return { success: true };
      }
      return { success: false, message: res.message || 'Registration failed' };
    } catch (err: any) {
      return { success: false, message: 'Server connection error. Please try again.' };
    }
  };

  const logout = () => {
    localStorage.removeItem('pricehub_token');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user,
        isLoading,
        login,
        loginAsDemo,
        register,
        logout,
        refreshProfile,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
