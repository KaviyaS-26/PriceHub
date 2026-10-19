import React from 'react';

interface DataSourceBadgeProps {
  dataSource?: string;
  priceDate?: string;
  lastUpdated?: string;
  className?: string;
  showTime?: boolean;
}

export const DataSourceBadge: React.FC<DataSourceBadgeProps> = ({
  dataSource,
  priceDate,
  lastUpdated,
  className = '',
  showTime = false,
}) => {
  const dateStr = priceDate || '15-17 Oct 2026';

  return (
    <div className={`inline-flex items-center gap-1.5 ${className}`}>
      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold tracking-wide shadow-2xs border bg-purple-50 text-purple-800 border-purple-200">
        <span className="w-1.5 h-1.5 rounded-full bg-purple-500" />
        Collected data, {dateStr}
      </span>
      {showTime && lastUpdated && (
        <span className="text-[10px] text-slate-400 font-medium">
          Updated {new Date(lastUpdated).toLocaleDateString([], { month: 'short', day: 'numeric' })}
        </span>
      )}
    </div>
  );
};
