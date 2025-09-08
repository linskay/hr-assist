import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import NeonButton from './NeonButton';
import { Link } from 'react-router-dom';

const STORAGE_KEY = 'cookie-consent-accepted';

export default function CookieConsent() {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const accepted = localStorage.getItem(STORAGE_KEY);
    if (!accepted) setVisible(true);
  }, []);

  const accept = () => {
    localStorage.setItem(STORAGE_KEY, 'true');
    setVisible(false);
  };

  return (
    <AnimatePresence>
      {visible && (
        <motion.div
          initial={{ opacity: 0, y: 80, x: 0 }}
          animate={{ opacity: 1, y: 0, x: 0 }}
          exit={{ opacity: 0, y: 80, x: 0 }}
          transition={{ duration: 0.4, ease: 'easeOut' }}
          className="fixed bottom-3 right-3 left-3 sm:left-auto sm:bottom-4 sm:right-4 z-50"
        >
          <div className="glass card-neon neon-glow p-3 sm:p-4 border-2 border-neon-purple/40 rounded-xl w-full max-w-xs sm:max-w-sm">
            <h3 className="text-base sm:text-lg font-bold glow-white mb-1.5">Печеньки</h3>
            <p className="text-[12px] sm:text-sm leading-snug opacity-90 mb-2">
              Пользуясь нашим сайтом, вы соглашаетесь с тем, что мы используем cookies 🍪
              <Link to="/cookies" className="ml-1 underline text-neon-cyan">с тыкательной ссылкой на куки</Link>.
            </p>
            <div className="flex justify-end">
              <NeonButton variant="cyan" onClick={accept} className="py-1 px-3 text-xs sm:text-sm">Окей</NeonButton>
            </div>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
