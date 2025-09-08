import React from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { AlertTriangle } from 'lucide-react';

const NotFoundPage: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen text-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.5, rotate: -30 }}
        animate={{ opacity: 1, scale: 1, rotate: 0 }}
        transition={{ type: 'spring', stiffness: 100, damping: 10 }}
        className="mb-8"
      >
        <AlertTriangle className="h-32 w-32 text-yellow-400" />
      </motion.div>

      <motion.h1
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
        className="text-6xl font-bold text-white mb-4"
      >
        404 - Страница не найдена
      </motion.h1>

      <motion.p
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.4 }}
        className="text-xl text-white/70 mb-8"
      >
        Кажется, страница, которую вы ищете, затерялась в цифровой пустоте.
      </motion.p>

      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.6 }}
      >
        <Button asChild size="lg">
          <Link to="/app/dashboard">
            Вернуться в безопасное место (на главную)
          </Link>
        </Button>
      </motion.div>
    </div>
  );
};

export default NotFoundPage;
