import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { Home, Briefcase, Mic, Users, BarChart2, Cpu, Settings } from 'lucide-react';

interface NavigationItem {
  name: string;
  href: string;
  icon: React.ReactNode;
  roles?: string[];
}

export const Sidebar: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const location = useLocation();

  const navigation: NavigationItem[] = [
    { name: 'Дашборд', href: '/', icon: <Home size={18} /> },
    {
      name: 'Вакансии',
      href: '/vacancies',
      icon: <Briefcase size={18} />,
      roles: ['ADMIN', 'HR_MANAGER'],
    },
    {
      name: 'Интервью',
      href: '/interviews',
      icon: <Mic size={18} />,
      roles: ['ADMIN', 'HR_MANAGER', 'INTERVIEWER'],
    },
    {
      name: 'Кандидаты',
      href: '/candidates',
      icon: <Users size={18} />,
      roles: ['ADMIN', 'HR_MANAGER'],
    },
    {
      name: 'Отчеты',
      href: '/reports',
      icon: <BarChart2 size={18} />,
      roles: ['ADMIN', 'HR_MANAGER', 'REVIEWER'],
    },
    {
      name: 'AI Анализ',
      href: '/ai-analysis',
      icon: <Cpu size={18} />,
      roles: ['ADMIN', 'HR_MANAGER'],
    },
    {
      name: 'Администрирование',
      href: '/admin',
      icon: <Settings size={18} />,
      roles: ['ADMIN'],
    },
  ];

  const filteredNavigation = navigation.filter(item => 
    !item.roles || hasAnyRole(item.roles)
  );

  return (
    <div className="hidden md:flex md:w-64 md:flex-col">
      <div className="flex flex-col flex-grow pt-5 glass border-r border-neon-blue border-opacity-20 overflow-y-auto">
        <div className="flex flex-col flex-grow">
          <nav className="flex-1 px-4 pb-4 space-y-2">
            {filteredNavigation.map((item) => {
              const isActive = location.pathname === item.href;
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`group flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-all duration-300 ${
                    isActive
                      ? 'bg-gradient-to-r from-neon-blue from-opacity-20 to-neon-purple to-opacity-20 text-neon-blue border border-neon-blue border-opacity-30 neon-glow'
                      : 'opacity-70 hover:bg-gradient-to-r hover:from-neon-cyan hover:from-opacity-10 hover:to-neon-purple hover:to-opacity-10 hover:text-white hover:border hover:border-neon-cyan hover:border-opacity-20'
                  }`}
                >
                  <span className={`mr-3 flex-shrink-0 transition-colors duration-300 ${
                    isActive 
                      ? 'text-neon-blue' 
                      : 'opacity-70 group-hover:text-neon-cyan'
                  }`}>
                    {item.icon}
                  </span>
                  <span className="font-semibold">{item.name}</span>
                  {isActive && (
                    <div className="ml-auto w-2 h-2 rounded-full bg-neon-blue animate-pulse"></div>
                  )}
                </Link>
              );
            })}
          </nav>
          
          {/* Bottom section */}
          <div className="px-4 pb-4">
            <div className="glass rounded-lg p-4 border border-neon-purple border-opacity-20">
              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 rounded-lg bg-gradient-to-r from-neon-purple to-neon-pink flex items-center justify-center">
                  <span className="text-lg">✨</span>
                </div>
                <div>
                  <p className="text-xs font-semibold">AI Powered</p>
                  <p className="text-xs opacity-60">Интеллектуальный анализ</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};