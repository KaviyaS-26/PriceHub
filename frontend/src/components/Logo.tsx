import React from 'react';

interface LogoProps {
  variant?: 'full' | 'icon' | 'compact' | 'dark' | 'monochrome';
  size?: 'sm' | 'md' | 'lg' | 'xl';
  showTagline?: boolean;
  className?: string;
}

export const Logo: React.FC<LogoProps> = ({
  variant = 'full',
  size = 'md',
  showTagline = true,
  className = '',
}) => {
  const sizeMap = {
    sm: { icon: 24, text: 'text-lg', tagline: 'text-[9px]' },
    md: { icon: 32, text: 'text-2xl', tagline: 'text-xs' },
    lg: { icon: 42, text: 'text-3xl', tagline: 'text-sm' },
    xl: { icon: 54, text: 'text-4xl', tagline: 'text-base' },
  };

  const currentSize = sizeMap[size];

  // SVG Emblem: Shopping Bag + AI Sparkle + Orbit Comparison Curve + Purple to Teal Gradient
  const renderEmblem = (dim: number) => (
    <svg
      width={dim}
      height={dim}
      viewBox="0 0 100 100"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className="shrink-0 transition-transform duration-300 hover:scale-105"
    >
      <defs>
        <linearGradient id="pricehub-grad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stopColor={variant === 'monochrome' ? '#334155' : '#7C3AED'} />
          <stop offset="100%" stopColor={variant === 'monochrome' ? '#0F172A' : '#14B8A6'} />
        </linearGradient>
        <linearGradient id="orbit-grad" x1="0%" y1="100%" x2="100%" y2="0%">
          <stop offset="0%" stopColor={variant === 'monochrome' ? '#64748B' : '#A78BFA'} stopOpacity="0.8" />
          <stop offset="100%" stopColor={variant === 'monochrome' ? '#334155' : '#2DD4BF'} stopOpacity="0.8" />
        </linearGradient>
        <filter id="glow-filter" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="3" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>
      </defs>

      {/* Subtle Comparison Orbit Curve */}
      <ellipse
        cx="50"
        cy="52"
        rx="42"
        ry="22"
        stroke="url(#orbit-grad)"
        strokeWidth="4"
        strokeDasharray="6 4"
        transform="rotate(-20 50 52)"
      />

      {/* Modern Shopping Bag / Price Tag Body */}
      <rect
        x="24"
        y="30"
        width="52"
        height="54"
        rx="14"
        fill="url(#pricehub-grad)"
        filter="url(#glow-filter)"
      />

      {/* Bag Handle Curve */}
      <path
        d="M36 30V22C36 14.268 42.268 8 50 8C57.732 8 64 14.268 64 22V30"
        stroke={variant === 'dark' ? '#F8FAFC' : '#FFFFFF'}
        strokeWidth="5"
        strokeLinecap="round"
      />

      {/* Central AI Sparkle Star (4-Point Diamond) */}
      <path
        d="M50 42L53.5 51.5L63 55L53.5 58.5L50 68L46.5 58.5L37 55L46.5 51.5L50 42Z"
        fill="#FFFFFF"
      />

      {/* Secondary Mini Sparkle */}
      <circle cx="65" cy="40" r="3" fill="#14B8A6" />
      <circle cx="34" cy="68" r="2.5" fill="#EDE9FE" />
    </svg>
  );

  if (variant === 'icon') {
    return <div className={`inline-flex items-center ${className}`}>{renderEmblem(currentSize.icon)}</div>;
  }

  const isDark = variant === 'dark';
  const isMono = variant === 'monochrome';

  return (
    <div className={`inline-flex items-center gap-2.5 select-none ${className}`}>
      {renderEmblem(currentSize.icon)}

      <div className="flex flex-col">
        <div className={`font-extrabold tracking-tight brand-font flex items-center leading-none ${currentSize.text}`}>
          <span className={isMono ? 'text-slate-800' : isDark ? 'text-purple-300' : 'text-[#7C3AED]'}>
            Price
          </span>
          <span className={isMono ? 'text-slate-900' : isDark ? 'text-teal-300' : 'text-[#14B8A6]'}>
            Hub
          </span>
        </div>

        {showTagline && variant !== 'compact' && (
          <span className={`font-medium tracking-wide leading-tight mt-0.5 ${
            isDark ? 'text-slate-300' : isMono ? 'text-slate-600' : 'text-[#64748B]'
          } ${currentSize.tagline}`}>
            Shop Smarter. Choose Better.
          </span>
        )}
      </div>
    </div>
  );
};
