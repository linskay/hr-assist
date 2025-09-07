import React from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Paintbrush, Languages, UserCircle } from 'lucide-react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent } from '@/components/ui/card';
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";

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
               <p className="text-white/70">Настройте визуальное оформление приложения.</p>
               <div className="space-y-4 pt-4">
                <div className="flex items-center space-x-2">
                  <Switch id="cyberpunk-mode" defaultChecked />
                  <Label htmlFor="cyberpunk-mode">Тема "Киберпанк"</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Switch id="dark-mode" disabled />
                  <Label htmlFor="dark-mode" className="text-white/50">Темная тема (в разработке)</Label>
                </div>
               </div>
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
