import React from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Search, UserPlus, Filter } from 'lucide-react';
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogFooter,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/modal";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

// Mock data for candidates
const mockCandidates = [
  { id: 1, name: 'Иван Иванов', role: 'Frontend-разработчик', status: 'Пройдено', score: 85 },
  { id: 2, name: 'Мария Кузнецова', role: 'Backend-разработчик', status: 'Ожидание', score: null },
  { id: 3, name: 'Петр Сидоров', role: 'UI/UX Дизайнер', status: 'Отклонен', score: 45 },
  { id: 4, name: 'Анна Попова', role: 'Менеджер проекта', status: 'Нанят', score: 92 },
];

const getStatusVariant = (status: string): "secondary" | "default" | "destructive" | "outline" => {
  switch (status) {
    case 'Пройдено': return 'secondary';
    case 'Нанят': return 'default';
    case 'Отклонен': return 'destructive';
    default: return 'outline';
  }
};

const Dashboard: React.FC = () => {
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
          <h1 className="text-4xl font-bold text-white">Панель кандидатов</h1>
          <p className="text-white/60 mt-1">Управляйте и просматривайте всех ваших кандидатов.</p>
        </div>
        <Dialog>
          <DialogTrigger asChild>
            <Button>
              <UserPlus className="mr-2 h-5 w-5" />
              Добавить кандидата
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Добавить нового кандидата</DialogTitle>
              <DialogDescription>
                Заполните информацию ниже, чтобы добавить нового кандидата в систему.
              </DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <Input id="name" placeholder="Имя кандидата" />
              <Input id="role" placeholder="Должность" />
              <Textarea placeholder="Дополнительные заметки..." />
            </div>
            <DialogFooter>
              <Button type="submit">Сохранить</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      {/* Filters and Search */}
      <Card glow="aqua">
        <CardContent className="flex flex-col md:flex-row items-center gap-4 p-4">
          <div className="relative w-full md:flex-1">
            <Input placeholder="Поиск по имени или должности..." className="pl-10" />
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-white/40" />
          </div>
          <Button variant="outline">
            <Filter className="mr-2 h-5 w-5" />
            Фильтры
          </Button>
        </CardContent>
      </Card>

      {/* Candidate Table */}
      <Card>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Имя</TableHead>
              <TableHead>Должность</TableHead>
              <TableHead>Статус</TableHead>
              <TableHead className="text-right">Оценка</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {mockCandidates.map((candidate) => (
              <TableRow key={candidate.id}>
                <TableCell className="font-medium">{candidate.name}</TableCell>
                <TableCell>{candidate.role}</TableCell>
                <TableCell>
                  <Badge variant={getStatusVariant(candidate.status)}>{candidate.status}</Badge>
                </TableCell>
                <TableCell className="text-right font-bold text-brand-highlight-aqua">
                  {candidate.score !== null ? `${candidate.score}%` : 'N/A'}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Card>
    </motion.div>
  );
};

export default Dashboard;