import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Upload, FileText, Check } from 'lucide-react';
import { Textarea } from '@/components/ui/textarea';
import { Progress } from '@/components/ui/progress';
import { useMutation } from '@tanstack/react-query';
import { analyzeResume } from '@/services/new_features_api';
import { Loader } from '@/components/ui/loader';

const ResumeAnalysisPage: React.FC = () => {
  const [resumeText, setResumeText] = React.useState('');
  const [vacancyText, setVacancyText] = React.useState('');

  const mutation = useMutation({
    mutationFn: () => analyzeResume(resumeText, vacancyText),
  });

  const handleAnalyze = () => {
    if (!resumeText || !vacancyText) return;
    mutation.mutate();
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="p-4 md:p-8 space-y-8"
    >
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-white">Анализ резюме</h1>
        <p className="text-white/60 mt-1">Сравните резюме с описанием вакансии для оценки соответствия.</p>
      </div>

      {/* Input Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <Card className="glass-card">
          <CardHeader>
            <CardTitle className="flex items-center"><FileText className="mr-2 h-5 w-5" />Резюме кандидата</CardTitle>
          </CardHeader>
          <CardContent>
            <Textarea
              placeholder="Вставьте текст резюме сюда..."
              className="h-64 neon-input"
              value={resumeText}
              onChange={(e) => setResumeText(e.target.value)}
            />
            <Button className="mt-4 w-full"><Upload className="mr-2 h-4 w-4" />Загрузить файл</Button>
          </CardContent>
        </Card>
        <Card className="glass-card">
          <CardHeader>
            <CardTitle className="flex items-center"><FileText className="mr-2 h-5 w-5" />Описание вакансии</CardTitle>
          </CardHeader>
          <CardContent>
            <Textarea
              placeholder="Вставьте описание вакансии сюда..."
              className="h-64 neon-input"
              value={vacancyText}
              onChange={(e) => setVacancyText(e.target.value)}
            />
            <Button className="mt-4 w-full"><Upload className="mr-2 h-4 w-4" />Загрузить файл</Button>
          </CardContent>
        </Card>
      </div>

      <div className="text-center">
        <Button onClick={handleAnalyze} size="lg" className="neon-button-pink text-lg" disabled={mutation.isPending}>
          {mutation.isPending ? <Loader /> : <><Check className="mr-2 h-6 w-6" />Анализировать</>}
        </Button>
      </div>

      {/* Analysis Result */}
      {mutation.isSuccess && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <Card className="glass-card shadow-neon-glow-aqua">
            <CardHeader>
              <CardTitle>Результаты анализа</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="font-bold text-white/80">Оценка соответствия:</span>
                <span className="text-3xl font-bold text-brand-highlight-aqua">{mutation.data.score}%</span>
              </div>
              <Progress value={mutation.data.score} className="h-4" />
              <div>
                <h3 className="font-bold text-white/80">Краткое резюме:</h3>
                <p className="text-white/70 mt-1">{mutation.data.summary}</p>
              </div>
            </CardContent>
          </Card>
        </motion.div>
      )}
    </motion.div>
  );
};

export default ResumeAnalysisPage;
