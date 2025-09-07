import React from 'react';

export default function Dashboard() {
  return (
    <div className="p-6 space-y-8">
      {/* Header */}
      <div className="text-center space-y-4">
        <h1 className="text-6xl font-black text-gradient animate-pulse">
          🚀 HR Assistant AI
        </h1>
        <p className="text-xl opacity-80">
          Добро пожаловать в будущее рекрутинга с искусственным интеллектом ✨
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="card-neon neon-glow hover:neon-glow-purple transition-all duration-300 group p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium opacity-70">📅 Интервью сегодня</p>
              <p className="text-4xl font-bold text-neon-blue group-hover:text-neon-purple transition-colors">3</p>
              <p className="text-xs opacity-60">запланировано</p>
            </div>
            <div className="text-4xl">🎯</div>
          </div>
        </div>

        <div className="card-neon neon-glow-cyan hover:neon-glow transition-all duration-300 group p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium opacity-70">💼 Открытые вакансии</p>
              <p className="text-4xl font-bold text-neon-cyan group-hover:text-neon-blue transition-colors">5</p>
              <p className="text-xs opacity-60">активных</p>
            </div>
            <div className="text-4xl">📋</div>
          </div>
        </div>

        <div className="card-neon neon-glow-purple hover:neon-glow-cyan transition-all duration-300 group p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium opacity-70">👥 Кандидаты</p>
              <p className="text-4xl font-bold text-neon-purple group-hover:text-neon-cyan transition-colors">12</p>
              <p className="text-xs opacity-60">в процессе</p>
            </div>
            <div className="text-4xl">🌟</div>
          </div>
        </div>

        <div className="card-neon neon-glow hover:neon-glow-purple transition-all duration-300 group p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium opacity-70">📊 Конверсия</p>
              <p className="text-4xl font-bold text-neon-blue group-hover:text-neon-purple transition-colors">78%</p>
              <p className="text-xs opacity-60">успешных</p>
            </div>
            <div className="text-4xl">📈</div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Quick Actions */}
        <div className="lg:col-span-2 space-y-6">
          <h2 className="text-3xl font-bold">⚡ Быстрые действия</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="card-neon hover:neon-glow transition-all duration-300 group cursor-pointer p-6">
              <div className="flex items-center space-x-4">
                <div className="text-4xl">🎤</div>
                <div>
                  <h3 className="font-semibold text-lg">Начать интервью</h3>
                  <p className="text-sm opacity-70">Запустить новое интервью с AI</p>
                </div>
              </div>
            </div>

            <div className="card-neon hover:neon-glow-cyan transition-all duration-300 group cursor-pointer p-6">
              <div className="flex items-center space-x-4">
                <div className="text-4xl">📝</div>
                <div>
                  <h3 className="font-semibold text-lg">Создать вакансию</h3>
                  <p className="text-sm opacity-70">Добавить новую позицию</p>
                </div>
              </div>
            </div>

            <div className="card-neon hover:neon-glow-purple transition-all duration-300 group cursor-pointer p-6">
              <div className="flex items-center space-x-4">
                <div className="text-4xl">👨‍💼</div>
                <div>
                  <h3 className="font-semibold text-lg">Управление кандидатами</h3>
                  <p className="text-sm opacity-70">Просмотр и редактирование</p>
                </div>
              </div>
            </div>

            <div className="card-neon hover:neon-glow transition-all duration-300 group cursor-pointer p-6">
              <div className="flex items-center space-x-4">
                <div className="text-4xl">📊</div>
                <div>
                  <h3 className="font-semibold text-lg">Аналитика</h3>
                  <p className="text-sm opacity-70">Отчеты и метрики</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Chart */}
        <div className="space-y-6">
          <h2 className="text-3xl font-bold">📈 Конверсия кандидатов</h2>
          <div className="card-neon neon-glow-purple p-6">
            <div className="h-64 flex items-center justify-center">
              <p className="text-lg opacity-70">График будет здесь</p>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="space-y-6">
        <h2 className="text-3xl font-bold">🕒 Последняя активность</h2>
        <div className="card-neon p-6">
          <div className="space-y-4">
            <div className="flex items-center space-x-4 p-4 rounded-lg bg-white bg-opacity-5 border border-neon-green">
              <div className="text-2xl">✅</div>
              <div className="flex-1">
                <p className="font-medium">Иван Петров прошел интервью</p>
                <p className="text-sm opacity-60">2 минуты назад</p>
              </div>
              <span className="text-neon-green font-semibold text-lg">85% совпадение</span>
            </div>

            <div className="flex items-center space-x-4 p-4 rounded-lg bg-white bg-opacity-5 border border-neon-blue">
              <div className="text-2xl">📄</div>
              <div className="flex-1">
                <p className="font-medium">Создана вакансия "Frontend Developer"</p>
                <p className="text-sm opacity-60">1 час назад</p>
              </div>
              <span className="text-neon-blue font-semibold text-lg">Активна</span>
            </div>

            <div className="flex items-center space-x-4 p-4 rounded-lg bg-white bg-opacity-5 border border-neon-purple">
              <div className="text-2xl">👤</div>
              <div className="flex-1">
                <p className="font-medium">Мария Сидорова подала заявку</p>
                <p className="text-sm opacity-60">3 часа назад</p>
              </div>
              <span className="text-neon-purple font-semibold text-lg">Новая</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}