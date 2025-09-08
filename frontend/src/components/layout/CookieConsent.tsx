import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Card, CardContent } from '@/components/ui/card';
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
          <Card glow="aqua">
            <CardContent className="p-6">
              <div className="flex items-start gap-4">
                <Cookie className="h-8 w-8 text-brand-highlight-aqua mt-1 flex-shrink-0" />
                <div>
                  <h3 className="text-xl font-bold mb-2">Печеньки</h3>
                  <p className="text-sm text-white/70">
                    Пользуясь нашим сайтом, вы соглашаетесь с тем, что мы используем{' '}
                    <a href="/cookie-policy" target="_blank" rel="noopener noreferrer" className="underline hover:text-brand-highlight-aqua">
                      cookies 🍪
                    </a>
                  </p>
                  <Button onClick={handleAccept} className="mt-4 w-full" variant="secondary">
                    Окей
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default CookieConsent;
