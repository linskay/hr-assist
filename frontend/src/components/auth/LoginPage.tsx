import React, { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { LoginRequest } from '../../types/api';
import toast from 'react-hot-toast';
import { motion } from 'framer-motion';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { LogIn } from 'lucide-react';

const LoginPage: React.FC = () => {
  const { login, isLoading } = useAuth();
  const [formData, setFormData] = useState<LoginRequest>({
    email: '',
    password: '',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.email || !formData.password) {
      toast.error('Пожалуйста, заполните все поля');
      return;
    }

    try {
      await login(formData);
      toast.success('Вход выполнен успешно!');
      // The ProtectedRoute will handle redirection
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Ошибка входа. Проверьте ваши данные.');
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  return (
    <div className="flex items-center justify-center min-h-screen p-4 gradient-bg particles">
      <motion.div
        initial={{ opacity: 0, scale: 0.9, y: -20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
      >
        <div className="w-full" style={{ maxWidth: 280 }}>
          <div className="card-neon neon-glow border border-neon-purple/40 rounded-xl bg-black/40 backdrop-blur-md p-5">
            <div className="text-center mb-8">
              <h2 className="text-2xl font-black glow-white">Вход в HR Assistant</h2>
              <p className="mt-1 text-white/70 text-xs">Войдите в систему для проведения интервью</p>
            </div>
            <form className="space-y-4" onSubmit={handleSubmit}>
              <div className="space-y-3">
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="Email"
                  autoComplete="email"
                  required
                  value={formData.email}
                  onChange={handleChange}
                  disabled={isLoading}
                  className="neon-input"
                />
                <Input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="Пароль"
                  autoComplete="current-password"
                  required
                  value={formData.password}
                  onChange={handleChange}
                  disabled={isLoading}
                  className="neon-input"
                />
              </div>

              <Button
                type="submit"
                isLoading={isLoading}
                className="w-full py-2 text-sm border-2 border-neon-cyan text-neon-cyan bg-transparent hover:bg-neon-cyan hover:text-black transition-all"
                size="lg"
              >
                {!isLoading && <LogIn className="mr-2 h-5 w-5" />}
                Войти
              </Button>
            </form>
            <div className="mt-6 text-center text-xs text-white/40">
              <p>Свяжитесь с вашим HR-администратором, если у вас возникли проблемы со входом.</p>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default LoginPage;
