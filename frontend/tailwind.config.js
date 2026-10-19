/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        pricehub: {
          purple: '#7C3AED',
          deepPurple: '#4C1D95',
          lavender: '#A78BFA',
          lightLavender: '#EDE9FE',
          veryLightLavender: '#F5F3FF',
          softWhite: '#F8FAFC',
          mainText: '#1E293B',
          secondaryText: '#64748B',
          teal: '#14B8A6',
          border: '#E2E8F0',
          success: '#16A34A',
          warning: '#F59E0B',
          error: '#EF4444',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'soft': '0 4px 20px -2px rgba(124, 58, 237, 0.08), 0 2px 6px -1px rgba(0, 0, 0, 0.04)',
        'glow': '0 0 25px rgba(124, 58, 237, 0.25)',
        'card': '0 10px 30px -5px rgba(30, 41, 59, 0.05)',
      }
    },
  },
  plugins: [],
}
