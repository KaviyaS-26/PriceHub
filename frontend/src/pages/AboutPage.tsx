import React from 'react';
import { Logo } from '../components/Logo';
import {
  Cpu,
  Database,
  Code2,
  Sparkles,
  Layers,
  Scale,
  ShieldCheck,
  Zap,
  CheckCircle2,
  ExternalLink,
} from 'lucide-react';

interface AboutPageProps {
  onNavigate: (page: string) => void;
}

export const AboutPage: React.FC<AboutPageProps> = ({ onNavigate }) => {
  const oopConcepts = [
    {
      title: 'Abstraction & Interfaces',
      badge: 'MarketplaceAdapter Interface',
      desc: 'Defines unified contractual behavior (fetch listings, calculate cost, map DTOs) without exposing marketplace-specific crawling or API serialization logic.',
      code: 'public interface MarketplaceAdapter { CostBreakdownDTO calculateEffectiveCost(MarketplaceListing listing, String cardType); }',
    },
    {
      title: 'Polymorphism & Open/Closed Principle',
      badge: 'Concrete Adapters',
      desc: 'AmazonAdapter, FlipkartAdapter, and MeeshoAdapter override base methods to supply marketplace-specific discount calculation and affiliate URL mapping.',
      code: '@Component public class AmazonAdapter extends BaseMarketplaceAdapter { ... }',
    },
    {
      title: 'Encapsulation & DTO Pattern',
      badge: 'Data Transfer Objects',
      desc: 'Entities maintain data integrity with private fields and validations. Thin controllers return DTOs, ensuring internal database schemas are never directly exposed.',
      code: 'public class CostBreakdownDTO { private Double effectiveCost; private List<OfferDTO> appliedOffers; }',
    },
    {
      title: 'Composition & Relational Modeling',
      badge: 'JPA Domain Entities',
      desc: 'Product contains lists of ProductSpecifications and MarketplaceListings. Listings compose Offers and historical PriceHistory records.',
      code: '@OneToMany(mappedBy = "marketplaceListing", cascade = CascadeType.ALL) private List<Offer> offers;',
    },
    {
      title: 'Dependency Injection & Inversion of Control',
      badge: 'Spring IoC Container',
      desc: 'Constructor-injected services and engine components (ProductMatcher, EffectiveCostCalculator, ValueScoreCalculator) decouple dependencies cleanly.',
      code: 'public ComparisonService(ProductMatcher matcher, EffectiveCostCalculator costCalc) { ... }',
    },
    {
      title: 'Constraint Satisfaction & Optimization',
      badge: 'DealLabService Solver',
      desc: 'Evaluates multi-attribute constraints (category, budget, rating, warranty), ranks qualifying listings by comparison value score, and generates actionable relaxation recommendations.',
      code: 'public DealLabResultDTO solveDealLab(DealLabCriteriaDTO criteria) { ... }',
    },
  ];

  const apiEndpoints = [
    { method: 'POST', path: '/api/auth/register', desc: 'Customer registration with BCrypt password hashing' },
    { method: 'POST', path: '/api/auth/login', desc: 'JWT authentication returning Bearer token' },
    { method: 'GET', path: '/api/products', desc: 'Full product search, category filter, price range, and rating filter' },
    { method: 'GET', path: '/api/compare', desc: 'Dynamic single-product multi-marketplace comparison with price, rating, and warranty weighting' },
    { method: 'GET', path: '/api/deals', desc: 'Personalized Deal Lab constraint solver with compare engine value scores and failure reason analysis' },
    { method: 'POST', path: '/api/alerts', desc: 'Create target price threshold alert' },
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-12">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 text-white rounded-3xl p-8 sm:p-10 shadow-xl space-y-4">
        <Logo variant="dark" size="lg" showTagline={true} />
        <h1 className="text-3xl sm:text-4xl font-extrabold brand-font pt-2">
          Technical Architecture &amp; OOP Design
        </h1>
        <p className="text-xs sm:text-sm text-slate-300 max-w-3xl leading-relaxed">
          PriceHub is engineered as a college-level full-stack application demonstrating modern Java 21+ Object-Oriented Software Engineering, Spring Boot REST micro-architecture, MySQL relational schemas, and responsive React UI.
        </p>
      </div>

      {/* 1. Java OOP Principles in Action */}
      <section className="space-y-6">
        <div className="border-b border-slate-200 pb-3">
          <div className="flex items-center gap-2 text-xs font-bold text-purple-700 uppercase tracking-wider">
            <Cpu className="w-4 h-4" /> Section 17
          </div>
          <h2 className="text-2xl font-extrabold text-slate-900 brand-font">
            Object-Oriented Programming (OOP) Architecture
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {oopConcepts.map((item, idx) => (
            <div
              key={idx}
              className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-3 flex flex-col justify-between"
            >
              <div className="space-y-2">
                <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 rounded-full bg-purple-50 text-purple-700 border border-purple-200">
                  {item.badge}
                </span>
                <h3 className="font-bold text-base text-slate-900">{item.title}</h3>
                <p className="text-xs text-slate-600 leading-relaxed">{item.desc}</p>
              </div>

              <div className="bg-slate-900 text-purple-300 font-mono text-[11px] p-2.5 rounded-xl overflow-x-auto">
                <code>{item.code}</code>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 2. Core Mathematical Formulations */}
      <section className="bg-white rounded-3xl p-8 border border-slate-200 shadow-sm space-y-6">
        <h2 className="text-2xl font-extrabold text-slate-900 brand-font flex items-center gap-2">
          <Layers className="w-6 h-6 text-teal-600" /> Mathematical &amp; Algorithmic Formulations
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-xs text-slate-700">
          {/* Formula 1 */}
          <div className="p-5 bg-purple-50/70 border border-purple-200 rounded-2xl space-y-2">
            <h4 className="font-bold text-sm text-purple-900">1. Effective Cost Formula (Section 9)</h4>
            <div className="p-3 bg-white rounded-xl font-mono font-bold text-slate-900 border border-purple-200 text-xs">
              Effective Cost = Listed Price + Delivery Charges + Handling Fees - Applicable Discounts
            </div>
            <p className="text-[11px] text-slate-600 leading-relaxed">
              Discounts dynamically evaluate user payment card eligibility (HDFC, ICICI, SBI), instant coupon codes, and verified public marketplace promos.
            </p>
          </div>

          {/* Formula 2 */}
          <div className="p-5 bg-teal-50/70 border border-teal-200 rounded-2xl space-y-2">
            <h4 className="font-bold text-sm text-teal-900">2. Multi-Factor Value Score Formula (Section 10)</h4>
            <div className="p-3 bg-white rounded-xl font-mono font-bold text-slate-900 border border-teal-200 text-xs">
              Score = (w_p · PriceNorm) + (w_r · RatingNorm) + (w_s · SpecNorm) + (w_w · WarrantyNorm) + (w_d · SpeedNorm)
            </div>
            <p className="text-[11px] text-slate-600 leading-relaxed">
              Weights (w) automatically rebalance when users toggle priorities (Lowest Price vs Higher Rating vs Faster Delivery).
            </p>
          </div>
        </div>
      </section>

      {/* 3. REST API Documentation Table */}
      <section className="bg-white rounded-3xl p-8 border border-slate-200 shadow-sm space-y-6">
        <h2 className="text-2xl font-extrabold text-slate-900 brand-font flex items-center gap-2">
          <Code2 className="w-6 h-6 text-purple-600" /> Backend REST API Specification
        </h2>

        <div className="border border-slate-200 rounded-2xl overflow-hidden shadow-2xs">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 border-b border-slate-200 text-slate-600 font-bold uppercase tracking-wider">
              <tr>
                <th className="py-3 px-4">Method</th>
                <th className="py-3 px-4">Endpoint Path</th>
                <th className="py-3 px-4">Description</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {apiEndpoints.map((ep, i) => (
                <tr key={i} className="hover:bg-slate-50">
                  <td className="py-3 px-4">
                    <span className={`px-2 py-0.5 rounded font-mono font-bold text-[10px] ${
                      ep.method === 'GET' ? 'bg-blue-100 text-blue-800' :
                      ep.method === 'POST' ? 'bg-emerald-100 text-emerald-800' :
                      ep.method === 'PUT' ? 'bg-amber-100 text-amber-800' : 'bg-red-100 text-red-800'
                    }`}>
                      {ep.method}
                    </span>
                  </td>
                  <td className="py-3 px-4 font-mono font-bold text-purple-700">{ep.path}</td>
                  <td className="py-3 px-4 text-slate-600 font-medium">{ep.desc}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
};
