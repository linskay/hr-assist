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
    <div className="flex items-center justify-center min-h-screen p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9, y: -20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
      >
        <Card className="w-full max-w-md" glow="accent">
          <CardContent className="p-8">
            <div className="text-center mb-8">
              <h2 className="text-4xl font-bold text-white">
                Авторизация
              </h2>
              <p className="mt-2 text-white/60">
                Войдите, чтобы получить доступ к панели AI HR Assistant.
              </p>
            </div>
            <form className="space-y-6" onSubmit={handleSubmit}>
              <div className="space-y-4">
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
                />
              </div>

              <Button type="submit" isLoading={isLoading} className="w-full" size="lg">
                {!isLoading && <LogIn className="mr-2 h-5 w-5" />}
                Войти
              </Button>
            </form>
            <div className="mt-6 text-center text-xs text-white/40">
              <p>Свяжитесь с вашим HR-администратором, если у вас возникли проблемы со входом.</p>
            </div>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  );
};

export default LoginPage;
