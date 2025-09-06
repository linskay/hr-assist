import React from 'react';
import { useAuth } from '../../hooks/useAuth';

export const Header: React.FC = () => {
  const { user, logout, hasRole } = useAuth();

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error('Error during logout:', error);
    }
  };

  const getRoleDisplayName = (role: string) => {
    switch (role) {
      case 'ADMIN':
        return 'Администратор';
      case 'HR':
        return 'HR менеджер';
      case 'INTERVIEWER':
        return 'Интервьюер';
      case 'REVIEWER':
        return 'Ревьюер';
      default:
        return role;
    }
  };

  return (
    <header className="bg-white shadow">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center py-6">
          <div className="flex items-center">
            <h1 className="text-2xl font-bold text-gray-900">
              HR Assistant
            </h1>
          </div>
          
          <div className="flex items-center space-x-4">
            {user && (
              <>
                <div className="text-sm text-gray-700">
                  <span className="font-medium">{user.email}</span>
                  <span className="ml-2 px-2 py-1 bg-gray-100 rounded-full text-xs">
                    {getRoleDisplayName(user.role)}
                  </span>
                </div>
                
                {hasRole('ADMIN') && (
                  <div className="text-xs text-gray-500">
                    Админ панель
                  </div>
                )}
                
                <button
                  onClick={handleLogout}
                  className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                >
                  Выйти
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
