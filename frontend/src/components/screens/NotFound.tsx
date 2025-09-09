import React from 'react';
import { Link } from 'react-router-dom';
import NeonButton from '../ui/NeonButton';

export default function NotFound() {
  return (
    <div className="min-h-screen flex items-center justify-center text-center gradient-bg particles">
      <div className="space-y-6">
        <div className="text-7xl">🤖</div>
        <h1 className="text-5xl font-black text-gradient">404 — Робот потерялся</h1>
        <p className="opacity-80">Страница не найдена. Вернуться на главную?</p>
        <Link to="/">
          <NeonButton variant="cyan">На дашборд</NeonButton>
        </Link>
      </div>
    </div>
  );
}


