import React from 'react';
import { motion } from 'framer-motion';
import NeonButton from '../ui/NeonButton';
import { Link } from 'react-router-dom';

export default function Landing() {
  return (
    <div className="min-h-screen flex items-center justify-center gradient-bg particles relative overflow-hidden px-4">
      <div className="absolute inset-0 -z-10">
        <div className="absolute -top-32 -left-32 w-96 h-96 bg-neon-purple/20 rounded-full blur-3xl" />
        <div className="absolute -bottom-32 -right-32 w-[28rem] h-[28rem] bg-neon-cyan/20 rounded-full blur-3xl" />
      </div>
      <div className="text-center card-neon neon-glow-purple border border-neon-purple/30 rounded-lg w-full max-w-md h-[220px] flex flex-col items-center justify-center space-y-4 p-4">
        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="text-3xl md:text-4xl font-black text-gradient drop-shadow-[0_0_20px_rgba(127,0,255,0.6)]"
        >
          AI HR Assistant
        </motion.h1>
        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.1 }}
          className="text-sm md:text-base opacity-80 px-2"
        >
          Кибер-неоновая платформа для проведения интервью с видео, голосом и умным анализом.
        </motion.p>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
          className="flex gap-3 justify-center"
        >
          <Link to="/interviews">
            <NeonButton variant="purple" className="text-sm px-4 py-2">Пройти</NeonButton>
          </Link>
          <Link to="/login">
            <NeonButton variant="cyan" className="text-sm px-4 py-2">Войти как HR</NeonButton>
          </Link>
        </motion.div>
      </div>
    </div>
  );
}


