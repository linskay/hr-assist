/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Poppins', 'sans-serif'],
      },
      colors: {
        // New color palette from the spec
        'brand-bg': '#0d0b33',
        'brand-accent': '#7f00ff',
        'brand-highlight-aqua': '#00eaff',
        'brand-highlight-pink': '#ff00ff',

        // Keeping old neon colors for now, might be useful
        neon: {
          blue: '#00BFFF',
          purple: '#8A2BE2',
          cyan: '#00FFFF',
          pink: '#FF1493',
          green: '#00FF00',
        },
      },
      animation: {
        'gradient-bg': 'gradient-bg 15s ease infinite',
        'pulse-glow': 'pulse-glow 2s infinite ease-in-out',
        'border-path': 'border-path 4s linear infinite',
      },
      keyframes: {
        'gradient-bg': {
          '0%, 100%': { backgroundPosition: '0% 50%' },
          '50%': { backgroundPosition: '100% 50%' },
        },
        'pulse-glow': {
          '0%, 100%': {
            boxShadow: '0 0 5px #7f00ff, 0 0 10px #7f00ff, 0 0 15px #7f00ff',
          },
          '50%': {
            boxShadow: '0 0 10px #00eaff, 0 0 20px #00eaff, 0 0 30px #00eaff',
          },
        },
        'border-path': {
          '0%': { 'clip-path': 'inset(0 100% 100% 0)' },
          '25%': { 'clip-path': 'inset(0 0 100% 0)' },
          '50%': { 'clip-path': 'inset(0 0 0 0)' },
          '75%': { 'clip-path': 'inset(100% 0 0 0)' },
          '100%': { 'clip-path': 'inset(0 100% 100% 0)' },
        }
      },
      boxShadow: {
        'neon-glow-accent': '0 0 5px #7f00ff, 0 0 10px #7f00ff, 0 0 15px #7f00ff',
        'neon-glow-aqua': '0 0 5px #00eaff, 0 0 10px #00eaff, 0 0 15px #00eaff',
        'neon-glow-pink': '0 0 5px #ff00ff, 0 0 10px #ff00ff, 0 0 15px #ff00ff',
      }
    },
  },
  plugins: [],
}