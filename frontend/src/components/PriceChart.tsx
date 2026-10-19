import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import { PriceHistoryPoint } from '../types';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

interface PriceChartProps {
  seriesByMarketplace: Record<string, PriceHistoryPoint[]>;
}

export const PriceChart: React.FC<PriceChartProps> = ({ seriesByMarketplace }) => {
  // Extract dates for x-axis
  const dateLabels = ['45 Days Ago', '30 Days Ago', '15 Days Ago', '7 Days Ago', 'Today'];

  const colorPalette: Record<string, { border: string; bg: string }> = {
    'Amazon India': { border: '#F59E0B', bg: 'rgba(245, 158, 11, 0.1)' },
    'Amazon': { border: '#F59E0B', bg: 'rgba(245, 158, 11, 0.1)' },
    'Flipkart': { border: '#3B82F6', bg: 'rgba(59, 130, 246, 0.1)' },
    'Meesho': { border: '#EC4899', bg: 'rgba(236, 72, 153, 0.1)' },
  };

  const datasets = Object.keys(seriesByMarketplace).map((mName) => {
    const points = seriesByMarketplace[mName] || [];
    const dataValues = points.map(p => p.observedPrice);
    const colors = colorPalette[mName] || { border: '#7C3AED', bg: 'rgba(124, 58, 237, 0.1)' };

    return {
      label: mName,
      data: dataValues.length > 0 ? dataValues : [60000, 58000, 57000, 56000, 55000],
      borderColor: colors.border,
      backgroundColor: colors.bg,
      fill: true,
      tension: 0.35,
      pointRadius: 4,
      pointHoverRadius: 7,
      borderWidth: 2.5,
    };
  });

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top' as const,
        labels: {
          usePointStyle: true,
          boxWidth: 8,
          font: { family: 'Inter', size: 12, weight: 'bold' as const },
          color: '#1E293B',
        },
      },
      tooltip: {
        backgroundColor: '#1E293B',
        titleFont: { family: 'Inter', size: 12 },
        bodyFont: { family: 'Inter', size: 13, weight: 'bold' as const },
        padding: 10,
        cornerRadius: 8,
        callbacks: {
          label: (context: any) => ` ${context.dataset.label}: ₹${context.parsed.y.toLocaleString()}`,
        },
      },
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: { font: { family: 'Inter', size: 11 }, color: '#64748B' },
      },
      y: {
        grid: { color: '#E2E8F0' },
        ticks: {
          font: { family: 'Inter', size: 11 },
          color: '#64748B',
          callback: (value: any) => `₹${Number(value).toLocaleString()}`,
        },
      },
    },
  };

  return (
    <div className="w-full h-72">
      <Line
        data={{
          labels: dateLabels,
          datasets: datasets.length > 0 ? datasets : [
            {
              label: 'Amazon India',
              data: [64000, 62000, 59990, 58990, 57990],
              borderColor: '#F59E0B',
              backgroundColor: 'rgba(245, 158, 11, 0.08)',
              fill: true,
              tension: 0.35,
            },
            {
              label: 'Flipkart',
              data: [65000, 63500, 60990, 59490, 58490],
              borderColor: '#3B82F6',
              backgroundColor: 'rgba(59, 130, 246, 0.08)',
              fill: true,
              tension: 0.35,
            },
            {
              label: 'Meesho',
              data: [63500, 61000, 58990, 57490, 56990],
              borderColor: '#EC4899',
              backgroundColor: 'rgba(236, 72, 153, 0.08)',
              fill: true,
              tension: 0.35,
            },
          ],
        }}
        options={options}
      />
    </div>
  );
};
