import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Slider } from '@/components/ui/slider';
import { PlusCircle, Trash2, Save } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getCompetencyMatrix, saveCompetencyMatrix } from '@/services/new_features_api';
import { Loader } from '@/components/ui/loader';
import toast from 'react-hot-toast';

interface Competency {
  id: number;
  name: string;
  weight: number;
}

const CompetencyMatrixPage: React.FC = () => {
  const queryClient = useQueryClient();
  const { data: initialCompetencies, isLoading } = useQuery<Competency[]>({
    queryKey: ['competencies'],
    queryFn: getCompetencyMatrix,
  });

  const [competencies, setCompetencies] = useState<Competency[]>([]);
  const [newCompetencyName, setNewCompetencyName] = useState('');

  useEffect(() => {
    if (initialCompetencies) {
      setCompetencies(initialCompetencies);
    }
  }, [initialCompetencies]);

  const mutation = useMutation({
    mutationFn: () => saveCompetencyMatrix(competencies),
    onSuccess: () => {
      toast.success('Матрица компетенций успешно сохранена!');
      queryClient.invalidateQueries({ queryKey: ['competencies'] });
    },
    onError: () => {
      toast.error('Не удалось сохранить матрицу компетенций.');
    }
  });

  const handleWeightChange = (id: number, newWeight: number[]) => {
    setCompetencies(
      competencies.map(c => (c.id === id ? { ...c, weight: newWeight[0] } : c))
    );
  };

  const handleAddCompetency = () => {
    if (newCompetencyName.trim() === '') return;
    const newCompetency: Competency = {
      id: Math.max(0, ...competencies.map(c => c.id)) + 1,
      name: newCompetencyName,
      weight: 50,
    };
    setCompetencies([...competencies, newCompetency]);
    setNewCompetencyName('');
  };

  const handleDeleteCompetency = (id: number) => {
    setCompetencies(competencies.filter(c => c.id !== id));
  };

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
          <h1 className="text-4xl font-bold text-white">Матрица компетенций</h1>
          <p className="text-white/60 mt-1">Настройте ключевые компетенции и их веса для оценки кандидатов.</p>
        </div>
        <Button className="neon-button-pink" onClick={() => mutation.mutate()} disabled={mutation.isPending}>
            {mutation.isPending ? <Loader /> : <><Save className="mr-2 h-5 w-5" />Сохранить матрицу</>}
        </Button>
      </div>

      <Card className="glass-card shadow-neon-glow-purple">
        <CardHeader>
          <CardTitle>Редактирование компетенций</CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          {isLoading ? <Loader /> : competencies.map(comp => (
            <div key={comp.id} className="grid grid-cols-1 md:grid-cols-4 items-center gap-4">
              <div className="md:col-span-1 font-semibold text-white/90">{comp.name}</div>
              <div className="md:col-span-2 flex items-center gap-4">
                <Slider
                  value={[comp.weight]}
                  onValueChange={(value) => handleWeightChange(comp.id, value)}
                  max={100}
                  step={5}
                />
                <span className="text-brand-highlight-aqua font-bold w-12 text-right">{comp.weight}%</span>
              </div>
              <div className="md:col-span-1 flex justify-end">
                <Button variant="ghost" size="icon" onClick={() => handleDeleteCompetency(comp.id)}>
                  <Trash2 className="h-5 w-5 text-red-500" />
                </Button>
              </div>
            </div>
          ))}

          <hr className="border-white/10 my-6" />

          {/* Add new competency */}
          <div className="flex items-center gap-4">
            <Input
              placeholder="Новая компетенция..."
              value={newCompetencyName}
              onChange={(e) => setNewCompetencyName(e.target.value)}
              className="flex-grow neon-input"
            />
            <Button onClick={handleAddCompetency}>
              <PlusCircle className="mr-2 h-5 w-5" />
              Добавить
            </Button>
          </div>
        </CardContent>
      </Card>
    </motion.div>
  );
};

export default CompetencyMatrixPage;
