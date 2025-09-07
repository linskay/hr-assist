import React from 'react';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen relative">
      <Header />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 relative z-10">
          {children}
        </main>
      </div>
      
      {/* Animated background elements */}
      <div className="fixed inset-0 -z-10 overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-br from-transparent via-neon-blue via-opacity-5 to-neon-purple to-opacity-10"></div>
        <div className="absolute top-1/4 right-0 w-96 h-96 bg-neon-cyan bg-opacity-10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-1/4 left-0 w-80 h-80 bg-neon-purple bg-opacity-10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-64 h-64 bg-neon-pink bg-opacity-10 rounded-full blur-3xl animate-pulse"></div>
      </div>
    </div>
  );
};