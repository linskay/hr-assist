// Единая конфигурация приложения
export const appConfig = {
  // Основные настройки
  name: 'HR Assistant AI',
  version: '1.0.0',
  description: 'Система управления HR процессами с AI',
  
  // API настройки
  api: {
    baseUrl: process.env.VITE_API_URL || 'http://localhost:8080/api',
    timeout: 10000,
  },
  
  // UI настройки
  ui: {
    theme: {
      colors: {
        neon: {
          blue: '#00BFFF',
          purple: '#8A2BE2',
          cyan: '#00FFFF',
          pink: '#FF1493',
          green: '#00FF00',
        }
      }
    },
    animations: {
      duration: 300,
      easing: 'ease-in-out'
    }
  },
  
  // Настройки разработки
  dev: {
    port: 3000,
    host: 'localhost'
  },
  
  // Настройки сборки
  build: {
    outDir: 'dist',
    sourcemap: true
  }
};

export default appConfig;
