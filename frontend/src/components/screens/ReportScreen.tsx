import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { RadarChart } from '@/components/ui/radarchart';
import { Download, Share2, MessageSquareQuote } from 'lucide-react';

const mockReport = {
  candidateName: 'Иван Иванов',
  overallScore: 82,
  scores: [
    { name: 'Гибкие навыки (Soft Skills)', value: 75 },
    { name: 'Технические навыки (Tech Skills)', value: 88 },
    { name: 'Соответствие культуре', value: 80 },
  ],
  strengths: ['Отличные технические навыки', 'Опыт работы в команде', 'Знание современных технологий'],
  areasForDevelopment: ['Лидерские качества', 'Навыки презентации', 'Управление проектами'],
};

const ReportScreen: React.FC = () => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="p-4 md:p-8 space-y-8"
    >
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
        <div>
          <h1 className="text-4xl font-bold text-white">Отчет по кандидату</h1>
          <p className="text-white/60 mt-1">AI-аналитика для {mockReport.candidateName}</p>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary">
            <Share2 className="mr-2 h-5 w-5" />
            Поделиться
          </Button>
          <Button>
            <Download className="mr-2 h-5 w-5" />
            Экспорт в PDF
          </Button>
        </div>
      </div>

      {/* Main Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column - Scores and Breakdown */}
        <div className="lg:col-span-2 space-y-8">
          <Card glow="accent">
            <CardContent className="p-6">
              <h2 className="text-2xl font-bold mb-4 text-brand-highlight-aqua">Общая оценка</h2>
              <div className="text-7xl font-bold text-center my-8">
                {mockReport.overallScore}%
              </div>
              <p className="text-center text-white/60">На основе комплексного анализа собеседования.</p>
            </CardContent>
          </Card>
          <Card glow="aqua">
            <CardContent className="p-6">
              <h2 className="text-2xl font-bold mb-4 text-brand-highlight-aqua">Разбивка по баллам</h2>
              <div className="space-y-4">
                {mockReport.scores.map(score => (
                  <div key={score.name}>
                    <div className="flex justify-between mb-1">
                      <span className="font-semibold">{score.name}</span>
                      <span className="text-brand-highlight-aqua">{score.value}%</span>
                    </div>
                    <Progress value={score.value} />
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column - Competency Chart */}
        <div className="lg:col-span-1">
          <Card glow="pink" className="h-full">
            <CardContent className="p-6">
              <h2 className="text-2xl font-bold mb-4 text-brand-highlight-pink">Радар компетенций</h2>
              <RadarChart />
            </CardContent>
          </Card>
        </div>
      </div>

      {/* AI Summary */}
      <Card>
        <CardContent className="p-6">
          <h2 className="text-2xl font-bold mb-4 flex items-center gap-2">
            <MessageSquareQuote className="text-brand-accent" />
            AI-резюме и рекомендации
          </h2>
          <div className="grid md:grid-cols-2 gap-6 text-sm">
            <div>
              <h3 className="font-bold text-lg text-green-400 mb-2">Сильные стороны</h3>
              <ul className="list-disc list-inside space-y-1 text-white/80">
                {mockReport.strengths.map(s => <li key={s}>{s}</li>)}
              </ul>
            </div>
            <div>
              <h3 className="font-bold text-lg text-yellow-400 mb-2">Зоны роста</h3>
              <ul className="list-disc list-inside space-y-1 text-white/80">
                {mockReport.areasForDevelopment.map(a => <li key={a}>{a}</li>)}
              </ul>
            </div>
          </div>
        </CardContent>
      </Card>
    </motion.div>
  );
};

export default ReportScreen;