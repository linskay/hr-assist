import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import { Rocket, LogOut, Shield, Crown, Users } from 'lucide-react';

export const Header: React.FC = () => {
  const { user, logout, hasRole } = useAuth();

  const getRoleDisplayName = (role: string) => {
    switch (role) {
      case 'ADMIN':
        return 'Администратор';
      case 'HR_MANAGER':
        return 'HR Менеджер';
      case 'INTERVIEWER':
        return 'Интервьюер';
      case 'REVIEWER':
        return 'Ревьюер';
      default:
        return role;
    }
  };

  const handleLogout = () => {
    logout();
  };

  return (
    <header className="glass border-b border-neon-blue border-opacity-20 backdrop-blur-xl">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center py-4">
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <div className="w-10 h-10 rounded-lg bg-gradient-to-r from-neon-blue to-neon-purple flex items-center justify-center pulse-glow">
                <Rocket size={20} />
              </div>
              <h1 className="text-3xl font-bold text-gradient">
                HR Assistant AI
              </h1>
            </div>
          </div>
          
          <div className="flex items-center space-x-6">
            {user && (
              <>
                <div className="flex items-center space-x-3">
                  <div className="flex items-center space-x-2 px-4 py-2 rounded-lg glass border border-neon-blue border-opacity-30">
                    <span className="text-lg">👤</span>
                    <span className="text-sm font-medium">{user.email}</span>
                  </div>
                  
                  <div className="flex items-center space-x-2 px-3 py-2 rounded-lg bg-gradient-to-r from-neon-purple from-opacity-20 to-neon-cyan to-opacity-20 border border-neon-purple border-opacity-30">
                    <span className="text-lg">
                      {user.role === 'ADMIN' && <Crown size={16} />}
                      {user.role === 'HR_MANAGER' && <Shield size={16} />}
                      {user.role === 'INTERVIEWER' && <Users size={16} />}
                    </span>
                    <span className="text-xs font-semibold text-neon-purple">
                      {getRoleDisplayName(user.role)}
                    </span>
                  </div>
                  
                  {hasRole('ADMIN') && (
                    <div className="px-3 py-1 rounded-full bg-gradient-to-r from-neon-pink from-opacity-20 to-neon-purple to-opacity-20 border border-neon-pink border-opacity-30">
                      <span className="text-xs font-semibold text-neon-pink">АДМИН</span>
                    </div>
                  )}
                </div>
                
                <button onClick={handleLogout} className="btn-neon-purple flex items-center space-x-2">
                  <LogOut size={16} />
                  <span>Выйти</span>
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};