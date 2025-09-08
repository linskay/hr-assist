import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import { LoginForm } from './LoginForm';
import NeonLoader from '../ui/NeonLoader';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: string[];
  fallback?: React.ReactNode;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRoles,
  fallback,
}) => {
  const { isAuthenticated, isLoading, hasAnyRole } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen grid place-items-center gradient-bg particles">
        <div className="flex flex-col items-center space-y-4">
          <NeonLoader size={72} />
          <p className="opacity-80">Загрузка...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <LoginForm />;
  }

  if (requiredRoles && !hasAnyRole(requiredRoles)) {
    return (
      fallback || (
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-gray-900 mb-4">
              Доступ запрещен
            </h1>
            <p className="text-gray-600">
              У вас нет прав для доступа к этой странице
            </p>
          </div>
        </div>
      )
    );
  }

  return <>{children}</>;
};
