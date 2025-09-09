import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Video, Download } from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { useQuery } from '@tanstack/react-query';
import { apiService } from '@/services/api';
import { Loader } from '@/components/ui/loader';
import { Interview } from '@/types/api';

const CallArchivePage: React.FC = () => {
    const { data: interviews, isLoading, error } = useQuery<Interview[]>({
        queryKey: ['interviews'],
        queryFn: () => apiService.getInterviews(),
      });

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="p-4 md:p-8 space-y-8"
    >
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-white">Архив звонков</h1>
        <p className="text-white/60 mt-1">Просмотр записей и результатов прошедших собеседований.</p>
      </div>

      {/* Calls Grid */}
      {isLoading && <Loader />}
      {error && <p className="text-red-500">Не удалось загрузить архив звонков.</p>}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {interviews?.map((call) => (
          <motion.div key={call.id} whileHover={{ y: -5 }} transition={{ type: 'spring', stiffness: 300 }}>
            <Card className="glass-card shadow-neon-glow-accent h-full flex flex-col">
              <CardHeader>
                <CardTitle className="flex justify-between items-center">
                  <span className="text-brand-highlight-aqua">{call.candidateName}</span>
                  <Badge variant={call.status === 'COMPLETED' ? 'default' : 'outline'}>{call.status}</Badge>
                </CardTitle>
                <CardDescription className="text-white/50">{call.vacancyTitle}</CardDescription>
              </CardHeader>
              <CardContent className="flex-grow space-y-3">
                <div className="flex items-center text-sm text-white/70">
                  <p>Interview Date: {new Date(call.createdAt).toLocaleDateString()}</p>
                </div>
              </CardContent>
              <CardContent className="flex gap-2">
                 <Button variant="outline" className="w-full">
                    <Video className="mr-2 h-4 w-4" />
                    Смотреть запись
                </Button>
                <Button variant="ghost" size="icon">
                    <Download className="h-5 w-5"/>
                </Button>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>
    </motion.div>
  );
};

export default CallArchivePage;
