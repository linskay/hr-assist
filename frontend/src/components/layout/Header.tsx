import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Settings, LogOut } from 'lucide-react';
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

export const Header: React.FC = () => {
  const { user, logout } = useAuth();

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
    <header className="glass border-b border-white/10 backdrop-blur-xl">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center py-3">
          <div className="flex items-center space-x-4">
            <Link to="/app/dashboard" className="flex items-center space-x-2">
              <div className="w-10 h-10 rounded-lg bg-gradient-to-r from-brand-accent to-brand-highlight-aqua flex items-center justify-center shadow-neon-glow-accent">
                <span className="text-2xl font-bold">AI</span>
              </div>
              <h1 className="text-2xl font-bold text-white">
                HR Assistant
              </h1>
            </Link>
          </div>
          
          <div className="flex items-center space-x-4">
            {user && (
              <>
                <div className="text-right">
                  <p className="text-sm font-semibold">{user.email}</p>
                  <p className="text-xs text-white/70">{getRoleDisplayName(user.role)}</p>
                </div>
                <Avatar>
                  <AvatarImage src={`https://i.pravatar.cc/150?u=${user.email}`} alt={user.email} />
                  <AvatarFallback>{user.email.substring(0, 2).toUpperCase()}</AvatarFallback>
                </Avatar>
                <Button asChild variant="ghost" size="icon">
                  <Link to="/app/settings">
                    <Settings className="h-5 w-5" />
                  </Link>
                </Button>
                <Button onClick={handleLogout} variant="destructive" size="icon">
                  <LogOut className="h-5 w-5" />
                </Button>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};