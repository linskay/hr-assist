import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Paintbrush, Languages } from 'lucide-react';

const SettingsPage: React.FC = () => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="p-4 md:p-8 space-y-8"
    >
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-white">Настройки</h1>
        <p className="text-white/60 mt-1">Настройте приложение под себя.</p>
      </div>

      {/* Settings Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <Card glow="aqua">
          <CardContent className="p-6">
            <div className="flex items-center gap-4 mb-4">
              <Paintbrush className="h-8 w-8 text-brand-highlight-aqua" />
              <h2 className="text-2xl font-bold">Тема</h2>
            </div>
            <p className="text-white/70 mb-4">Выберите предпочитаемую визуальную тему.</p>
            <div className="flex gap-2">
              <Button variant="default">Киберпанк</Button>
              <Button variant="outline" disabled>Светлая</Button>
              <Button variant="outline" disabled>Темная</Button>
            </div>
             <p className="text-xs text-white/40 mt-4">Скоро появятся новые темы!</p>
          </CardContent>
        </Card>

        <Card glow="pink">
          <CardContent className="p-6">
            <div className="flex items-center gap-4 mb-4">
              <Languages className="h-8 w-8 text-brand-highlight-pink" />
              <h2 className="text-2xl font-bold">Язык</h2>
            </div>
            <p className="text-white/70 mb-4">Выберите язык приложения.</p>
            <div className="flex gap-2">
              <Button variant="outline">English</Button>
              <Button variant="default">Русский</Button>
            </div>
             <p className="text-xs text-white/40 mt-4">Выбор языка в разработке.</p>
          </CardContent>
        </Card>
      </div>
    </motion.div>
  );
};

export default SettingsPage;
