import React from 'react';
import { Logo } from './Logo';
import { ShieldCheck, Cpu, Database, Sparkles, ExternalLink, Heart } from 'lucide-react';

interface FooterProps {
  onNavigate: (page: string) => void;
}

export const Footer: React.FC<FooterProps> = ({ onNavigate }) => {
  return (
    <footer className="bg-slate-900 text-slate-300 border-t border-slate-800 pt-12 pb-8 mt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8 mb-12">
          {/* Brand Column */}
          <div className="lg:col-span-2 space-y-4">
            <Logo variant="dark" size="lg" showTagline={true} />
            <p className="text-xs text-slate-400 leading-relaxed max-w-sm">
              PriceHub is an intelligent shopping intelligence platform that compares products across Amazon India, Flipkart, and Meesho, calculates effective purchase costs with bank discounts, and helps you find the best value.
            </p>
            <div className="flex flex-wrap items-center gap-2 pt-2">
              <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-purple-950/80 border border-purple-800/50 text-[11px] text-purple-300 font-medium">
                <Cpu className="w-3 h-3 text-purple-400" /> Java Spring Boot
              </span>
              <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-teal-950/80 border border-teal-800/50 text-[11px] text-teal-300 font-medium">
                <Database className="w-3 h-3 text-teal-400" /> MySQL
              </span>
              <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-indigo-950/80 border border-indigo-800/50 text-[11px] text-indigo-300 font-medium">
                <Sparkles className="w-3 h-3 text-indigo-400" /> React + TypeScript
              </span>
            </div>
          </div>

          {/* Supported Marketplaces */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-100 mb-4 flex items-center gap-1.5">
              <ShieldCheck className="w-4 h-4 text-teal-400" /> Marketplaces
            </h4>
            <ul className="space-y-2.5 text-xs text-slate-400">
              <li className="flex items-center justify-between hover:text-white transition-colors">
                <span>Amazon India</span>
                <span className="text-[10px] bg-slate-800 text-amber-400 px-1.5 py-0.5 rounded font-mono">Monitored</span>
              </li>
              <li className="flex items-center justify-between hover:text-white transition-colors">
                <span>Flipkart</span>
                <span className="text-[10px] bg-slate-800 text-blue-400 px-1.5 py-0.5 rounded font-mono">Monitored</span>
              </li>
              <li className="flex items-center justify-between hover:text-white transition-colors">
                <span>Meesho</span>
                <span className="text-[10px] bg-slate-800 text-pink-400 px-1.5 py-0.5 rounded font-mono">Monitored</span>
              </li>
            </ul>
          </div>

          {/* Quick Platform Links */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-100 mb-4">
              Core Modules
            </h4>
            <ul className="space-y-2 text-xs text-slate-400">
              <li>
                <button onClick={() => onNavigate('search')} className="hover:text-purple-400 transition-colors">
                  Product Catalog &amp; Filters
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('compare')} className="hover:text-purple-400 transition-colors">
                  Smart Product Matcher
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('deallab')} className="hover:text-purple-400 transition-colors text-teal-300 font-semibold">
                  Personalized Deal Lab ✨
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('deals')} className="hover:text-purple-400 transition-colors">
                  Lowest Effective Prices
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('history')} className="hover:text-purple-400 transition-colors">
                  Price History &amp; Alerts
                </button>
              </li>
            </ul>
          </div>

          {/* Account & Architecture */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-100 mb-4">
              Account &amp; Architecture
            </h4>
            <ul className="space-y-2 text-xs text-slate-400">
              <li>
                <button onClick={() => onNavigate('account')} className="hover:text-purple-400 transition-colors">
                  My Account Dashboard
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('about')} className="hover:text-purple-400 transition-colors">
                  OOP Architecture &amp; UML
                </button>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Disclaimer */}
        <div className="border-t border-slate-800 pt-6 flex flex-col sm:flex-row items-center justify-between text-[11px] text-slate-500 gap-4">
          <p>© 2026 PriceHub — Full-Stack Java OOP Project Demonstration. All sample prices and bank card offers are simulated demo records.</p>
          <div className="flex items-center gap-4">
            <button onClick={() => onNavigate('about')} className="hover:text-slate-300">Architecture Specs</button>
            <span className="text-slate-700">|</span>
            <span className="text-slate-400">Built with Java 21+ &amp; React 18</span>
          </div>
        </div>
      </div>
    </footer>
  );
};
