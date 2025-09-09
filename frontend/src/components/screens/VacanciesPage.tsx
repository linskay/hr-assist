import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { PlusCircle, Briefcase, MapPin, DollarSign } from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { useQuery } from '@tanstack/react-query';
import { apiService } from '@/services/api';
import { Loader } from '@/components/ui/loader';

const VacanciesPage: React.FC = () => {
  const { data: vacancies, isLoading, error } = useQuery({
    queryKey: ['vacancies'],
    queryFn: () => apiService.getVacancies(),
  });

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
          <h1 className="text-4xl font-bold text-white">Вакансии</h1>
          <p className="text-white/60 mt-1">Управляйте открытыми и закрытыми вакансиями.</p>
        </div>
        <Button className="neon-button-pink">
          <PlusCircle className="mr-2 h-5 w-5" />
          Создать вакансию
        </Button>
      </div>

      {/* Vacancies Grid */}
      {isLoading && <Loader />}
      {error && <p className="text-red-500">Не удалось загрузить вакансии.</p>}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {vacancies?.map((vacancy) => (
          <motion.div key={vacancy.id} whileHover={{ scale: 1.03 }} transition={{ type: 'spring', stiffness: 400 }}>
            <Card className="glass-card h-full flex flex-col">
              <CardHeader>
                <CardTitle className="text-xl text-brand-highlight-aqua">{vacancy.title}</CardTitle>
                <div className="flex items-center text-white/60 text-sm pt-1">
                  <MapPin className="h-4 w-4 mr-2" /> {vacancy.location}
                </div>
              </CardHeader>
              <CardContent className="flex-grow">
                <div className="flex items-center text-white/80">
                  <DollarSign className="h-4 w-4 mr-2 text-brand-highlight-pink" />
                  <span>{vacancy.salaryMin} - {vacancy.salaryMax}</span>
                </div>
                <div className="flex items-center text-white/80 mt-2">
                  <Briefcase className="h-4 w-4 mr-2 text-brand-highlight-aqua" />
                  <span>{vacancy.employmentType}</span>
                </div>
              </CardContent>
              <CardFooter className="flex justify-between items-center">
                <Badge variant={vacancy.status === 'ACTIVE' ? 'default' : 'destructive'}>{vacancy.status}</Badge>
                <Button variant="outline" size="sm">Подробнее</Button>
              </CardFooter>
            </Card>
          </motion.div>
        ))}
      </div>
    </motion.div>
  );
};

export default VacanciesPage;
