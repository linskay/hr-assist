import React from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Paintbrush, Languages, UserCircle } from 'lucide-react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent } from '@/components/ui/card';

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

      <Tabs defaultValue="theme" className="w-full">
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="theme">
            <Paintbrush className="mr-2 h-4 w-4" />
            Тема
          </TabsTrigger>
          <TabsTrigger value="language">
            <Languages className="mr-2 h-4 w-4" />
            Язык
          </TabsTrigger>
          <TabsTrigger value="account">
            <UserCircle className="mr-2 h-4 w-4" />
            Аккаунт
          </TabsTrigger>
        </TabsList>

        <TabsContent value="theme">
          <Card>
            <CardContent className="p-6 space-y-4">
               <h3 className="text-2xl font-bold">Тема</h3>
               <p className="text-white/70">Выберите предпочитаемую визуальную тему.</p>
               <div className="flex gap-2">
                 <Button variant="default">Киберпанк</Button>
                 <Button variant="outline" disabled>Светлая</Button>
                 <Button variant="outline" disabled>Темная</Button>
               </div>
               <p className="text-xs text-white/40 mt-4">Скоро появятся новые темы!</p>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="language">
          <Card>
            <CardContent className="p-6 space-y-4">
              <h3 className="text-2xl font-bold">Язык</h3>
              <p className="text-white/70">Выберите язык приложения.</p>
              <div className="flex gap-2">
                <Button variant="outline">English</Button>
                <Button variant="default">Русский</Button>
              </div>
              <p className="text-xs text-white/40 mt-4">Выбор языка в разработке.</p>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="account">
            <Card>
                <CardContent className="p-6 space-y-4">
                    <h3 className="text-2xl font-bold">Настройки аккаунта</h3>
                    <p className="text-white/70">Здесь будут настройки вашего аккаунта.</p>
                </CardContent>
            </Card>
        </TabsContent>

      </Tabs>
    </motion.div>
  );
};

export default SettingsPage;
