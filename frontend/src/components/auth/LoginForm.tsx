import React, { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { LoginRequest } from '../../types/api';
import toast from 'react-hot-toast';
import NeonInput from '../ui/NeonInput';
import NeonButton from '../ui/NeonButton';

interface LoginFormProps {
  onSuccess?: () => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onSuccess }) => {
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
      toast.success('Успешный вход в систему');
      onSuccess?.();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Ошибка входа в систему');
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  return (
    <div className="min-h-screen gradient-bg particles px-4 py-6 flex justify-center">
      <div
        className="space-y-3 card-neon p-3 border border-neon-purple/30 neon-glow-purple inline-block"
        style={{ width: 260, marginTop: '12vh' }}
      >
        <div className="text-center space-y-1">
          <h2 className="text-xl sm:text-2xl font-black glow-white">Вход в HR Assistant</h2>
          <p className="opacity-80 text-[11px] leading-tight">Войдите, чтобы начать интервью и анализ</p>
        </div>
        <form className="space-y-2" onSubmit={handleSubmit}>
          <NeonInput
            id="email"
            name="email"
            type="email"
            placeholder="Email адрес"
            value={formData.email}
            onChange={handleChange}
            required
          />
          <NeonInput
            id="password"
            name="password"
            type="password"
            placeholder="Пароль"
            value={formData.password}
            onChange={handleChange}
            required
          />
          <NeonButton type="submit" fullWidth disabled={isLoading} className="py-2 text-sm">
            {isLoading ? 'Вход...' : 'Войти'}
          </NeonButton>

          <div className="text-center">
            <p className="text-[11px] opacity-70">Тестовые аккаунты:</p>
            <div className="mt-1 text-[9px] opacity-60 space-y-1 leading-tight">
              <div>admin@hr-assistant.com / admin123 (DEV)</div>
              <div>hr@hr-assistant.com / admin123 (DEV)</div>
              <div>interviewer@hr-assistant.com / admin123 (DEV)</div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};
