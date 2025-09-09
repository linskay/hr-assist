import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import StatCard from '@/components/ui/StatCard';
import { Briefcase, Users, CheckCircle, XCircle, BarChart2 } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

// Mock data for the chart
const chartData = [
  { name: 'Янв', проведенные: 30, успешные: 20 },
  { name: 'Фев', проведенные: 45, успешные: 32 },
  { name: 'Мар', проведенные: 60, успешные: 45 },
  { name: 'Апр', проведенные: 50, успешные: 35 },
  { name: 'Май', проведенные: 70, успешные: 55 },
  { name: 'Июн', проведенные: 85, успешные: 65 },
];

const Dashboard: React.FC = () => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="p-4 md:p-8 space-y-8"
    >
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-white">Дашборд</h1>
        <p className="text-white/60 mt-1">Ключевые метрики по процессу собеседований.</p>
      </div>

      {/* Stats Grid */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard title="Активных вакансий" value="12" icon={Briefcase} color="aqua" />
        <StatCard title="Всего кандидатов" value="256" icon={Users} color="pink" />
        <StatCard title="Успешно нанято" value="34" icon={CheckCircle} color="purple" />
        <StatCard title="Отклонено" value="128" icon={XCircle} color="purple" />
      </div>

      {/* Interview Trends Chart */}
      <Card className="glass-card shadow-neon-glow-aqua">
        <CardHeader>
          <CardTitle className="flex items-center">
            <BarChart2 className="mr-2 h-5 w-5" />
            Динамика собеседований
          </CardTitle>
        </CardHeader>
        <CardContent className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#00eaff" stopOpacity={0.8}/>
                  <stop offset="95%" stopColor="#00eaff" stopOpacity={0}/>
                </linearGradient>
                <linearGradient id="colorPv" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#ff00ff" stopOpacity={0.8}/>
                  <stop offset="95%" stopColor="#ff00ff" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255, 255, 255, 0.1)" />
              <XAxis dataKey="name" stroke="rgba(255, 255, 255, 0.5)" />
              <YAxis stroke="rgba(255, 255, 255, 0.5)" />
              <Tooltip
                contentStyle={{
                  backgroundColor: 'rgba(13, 11, 51, 0.8)',
                  borderColor: '#00eaff',
                  color: '#fff',
                }}
              />
              <Area type="monotone" dataKey="проведенные" stroke="#00eaff" fillOpacity={1} fill="url(#colorUv)" />
              <Area type="monotone" dataKey="успешные" stroke="#ff00ff" fillOpacity={1} fill="url(#colorPv)" />
            </AreaChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </motion.div>
  );
};

export default Dashboard;