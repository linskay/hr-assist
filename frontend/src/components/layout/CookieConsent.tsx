import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Cookie } from 'lucide-react';

const COOKIE_CONSENT_KEY = 'cookie-consent-given';

const CookieConsent: React.FC = () => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const consentGiven = localStorage.getItem(COOKIE_CONSENT_KEY);
    if (!consentGiven) {
      // Delay showing the banner slightly
      const timer = setTimeout(() => setIsVisible(true), 2000);
      return () => clearTimeout(timer);
    }
  }, []);

  const handleAccept = () => {
    localStorage.setItem(COOKIE_CONSENT_KEY, 'true');
    setIsVisible(false);
  };

  return (
    <AnimatePresence>
      {isVisible && (
        <motion.div
          initial={{ opacity: 0, y: 100, x: 100 }}
          animate={{ opacity: 1, y: 0, x: 0 }}
          exit={{ opacity: 0, y: 100, x: 100 }}
          transition={{ type: 'spring', stiffness: 100, damping: 20 }}
          className="fixed bottom-4 right-4 z-50 w-full max-w-sm"
        >
          <div className="glass card-neon border border-neon-purple/40 neon-glow p-4 rounded-xl bg-black/40 backdrop-blur-md">
            <div className="flex items-start gap-3">
              <Cookie className="h-6 w-6 text-brand-highlight-aqua mt-0.5 flex-shrink-0" />
              <div>
                <h3 className="text-base sm:text-lg font-bold glow-white mb-1">Печеньки</h3>
                <p className="text-[12px] sm:text-sm text-white/80">
                  Пользуясь нашим сайтом, вы соглашаетесь с тем, что мы используем cookies 🍪
                  <a href="/cookies" className="ml-1 underline text-neon-cyan">с тыкательной ссылкой на куки</a>.
                </p>
                <Button onClick={handleAccept} className="mt-3 w-full sm:w-auto sm:px-4 sm:py-1.5" variant="secondary">
                  Окей
                </Button>
              </div>
            </div>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default CookieConsent;
