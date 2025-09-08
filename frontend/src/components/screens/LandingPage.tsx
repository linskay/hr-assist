import React from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { ArrowRight } from 'lucide-react';

const LandingPage: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen text-center p-4 overflow-hidden">
      <motion.div
        initial={{ opacity: 0, y: -50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, ease: "easeOut" }}
        className="relative"
      >
        <h1 className="text-6xl md:text-8xl font-bold text-white tracking-tighter">
          AI HR Assistant
        </h1>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-full bg-brand-accent blur-3xl opacity-30 rounded-full" />
      </motion.div>

      <motion.p
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, delay: 0.3, ease: "easeOut" }}
        className="mt-6 max-w-2xl text-lg md:text-xl text-white/70"
      >
        Добро пожаловать в будущее собеседований. Наш AI-ассистент обеспечивает бесшовный, объективный и глубокий опыт интервью для каждого кандидата.
      </motion.p>

      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.6, ease: "backOut" }}
        className="mt-10"
      >
        <Button asChild size="lg" variant="default">
          <Link to="/login">
            Пройти собеседование
            <ArrowRight className="ml-2 h-5 w-5" />
          </Link>
        </Button>
      </motion.div>
    </div>
  );
};

export default LandingPage;
