import React from 'react';

export default function ReportScreen() {
  return (
    <div className="p-6 space-y-8">
      {/* Header */}
      <div className="text-center space-y-4">
        <h1 className="text-5xl font-black text-gradient animate-pulse">
          🤖 AI Анализ Кандидата
        </h1>
        <p className="text-xl opacity-80">
          Интеллектуальный отчет с анализом компетенций и рекомендациями ✨
        </p>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="card-neon neon-glow hover:neon-glow-purple transition-all duration-300 group p-6 text-center">
          <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-r from-neon-blue to-neon-purple flex items-center justify-center">
            <span className="text-3xl">📈</span>
          </div>
          <h3 className="text-4xl font-bold text-neon-blue group-hover:text-neon-purple transition-colors">82%</h3>
          <p className="text-sm opacity-70">Общая оценка</p>
        </div>

        <div className="card-neon neon-glow-cyan hover:neon-glow transition-all duration-300 group p-6 text-center">
          <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-r from-neon-cyan to-neon-blue flex items-center justify-center">
            <span className="text-3xl">⭐</span>
          </div>
          <h3 className="text-4xl font-bold text-neon-cyan group-hover:text-neon-blue transition-colors">4.2</h3>
          <p className="text-sm opacity-70">Средний балл</p>
        </div>

        <div className="card-neon neon-glow-purple hover:neon-glow-cyan transition-all duration-300 group p-6 text-center">
          <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-r from-neon-purple to-neon-pink flex items-center justify-center">
            <span className="text-3xl">🏆</span>
          </div>
          <h3 className="text-4xl font-bold text-neon-purple group-hover:text-neon-cyan transition-colors">7/10</h3>
          <p className="text-sm opacity-70">Компетенции</p>
        </div>

        <div className="card-neon neon-glow hover:neon-glow-purple transition-all duration-300 group p-6 text-center">
          <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-r from-neon-green to-neon-cyan flex items-center justify-center">
            <span className="text-3xl">👥</span>
          </div>
          <h3 className="text-4xl font-bold text-neon-green group-hover:text-neon-purple transition-colors">85%</h3>
          <p className="text-sm opacity-70">Совпадение</p>
        </div>
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Competency Chart */}
        <div className="card-neon neon-glow-purple p-6">
          <div className="flex items-center space-x-3 mb-6">
            <span className="text-3xl">📊</span>
            <h2 className="text-2xl font-bold">Анализ Компетенций</h2>
          </div>
          <div className="h-80 flex items-center justify-center">
            <p className="text-lg opacity-70">Радарная диаграмма будет здесь</p>
          </div>
        </div>

        {/* Key Quotes */}
        <div className="card-neon neon-glow-cyan p-6">
          <div className="flex items-center space-x-3 mb-6">
            <span className="text-3xl">💬</span>
            <h2 className="text-2xl font-bold">Ключевые Цитаты</h2>
          </div>
          <div className="space-y-4">
            <div className="p-4 rounded-lg bg-gradient-to-r from-neon-blue from-opacity-10 to-neon-purple to-opacity-10 border border-neon-blue border-opacity-20">
              <p className="italic">«Я координировал проект из 5 человек и успешно внедрил микросервисную архитектуру»</p>
              <p className="text-sm opacity-60 mt-2">— О лидерстве и технических навыках</p>
            </div>
            
            <div className="p-4 rounded-lg bg-gradient-to-r from-neon-cyan from-opacity-10 to-neon-blue to-opacity-10 border border-neon-cyan border-opacity-20">
              <p className="italic">«Успешно внедрил Jira и настроил CI/CD pipeline, что сократило время деплоя на 60%»</p>
              <p className="text-sm opacity-60 mt-2">— О процессе разработки</p>
            </div>
            
            <div className="p-4 rounded-lg bg-gradient-to-r from-neon-purple from-opacity-10 to-neon-pink to-opacity-10 border border-neon-purple border-opacity-20">
              <p className="italic">«Работал с Spring Boot, PostgreSQL и Docker в команде из 8 разработчиков»</p>
              <p className="text-sm opacity-60 mt-2">— О техническом опыте</p>
            </div>
          </div>
        </div>
      </div>

      {/* Detailed Analysis */}
      <div className="card-neon p-6">
        <h2 className="text-3xl font-bold mb-6">🔍 Детальный Анализ</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-neon-blue">✅ Сильные стороны</h3>
            <ul className="space-y-2">
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-neon-green"></div>
                <span className="text-sm">Отличные технические навыки</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-neon-green"></div>
                <span className="text-sm">Опыт работы в команде</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-neon-green"></div>
                <span className="text-sm">Знание современных технологий</span>
              </li>
            </ul>
          </div>
          
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-neon-cyan">🔄 Области для развития</h3>
            <ul className="space-y-2">
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-yellow-400"></div>
                <span className="text-sm">Лидерские качества</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-yellow-400"></div>
                <span className="text-sm">Презентационные навыки</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-yellow-400"></div>
                <span className="text-sm">Управление проектами</span>
              </li>
            </ul>
          </div>
          
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-neon-purple">💡 Рекомендации</h3>
            <ul className="space-y-2">
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-neon-blue"></div>
                <span className="text-sm">Подходит для Senior позиции</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-neon-blue"></div>
                <span className="text-sm">Рекомендуется к найму</span>
              </li>
              <li className="flex items-center space-x-2">
                <div className="w-2 h-2 rounded-full bg-neon-blue"></div>
                <span className="text-sm">Потенциал для роста</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex flex-wrap gap-4 justify-center">
        <button className="btn-neon flex items-center space-x-2">
          <span>📥</span>
          <span>Экспорт PDF</span>
        </button>
        
        <button className="btn-neon-purple flex items-center space-x-2">
          <span>💾</span>
          <span>Сохранить отчет</span>
        </button>
        
        <button className="btn-neon-cyan flex items-center space-x-2">
          <span>📊</span>
          <span>Детальная аналитика</span>
        </button>
      </div>
    </div>
  );
}