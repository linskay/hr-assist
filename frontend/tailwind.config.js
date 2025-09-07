/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        neon: {
          blue: '#00BFFF',
          purple: '#8A2BE2',
          cyan: '#00FFFF',
          pink: '#FF1493',
          green: '#00FF00',
        },
      },
    },
  },
  plugins: [],
}