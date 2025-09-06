import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { useInterview, useStartInterview, useHeartbeat, useInterviewRecording } from '../../hooks/useInterview';
import { motion } from 'framer-motion';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Loader } from '@/components/ui/loader';
import { Check, Mic, MicOff, MoveRight, Square, Video, VideoOff, Wifi, WifiOff, X, Maximize, Minimize } from 'lucide-react';
import Typewriter from '@/components/ui/typewriter';

// A placeholder for the audio visualizer
const AudioVisualizer = () => (
  <div className="flex items-center justify-center h-10 w-full">
    <div className="flex items-end gap-1 h-full">
      <motion.div className="w-2 bg-brand-highlight-aqua" initial={{ height: '10%' }} animate={{ height: ['20%', '80%', '30%', '90%', '40%', '70%', '20%'] }} transition={{ duration: 1.5, repeat: Infinity, ease: 'easeInOut' }} />
      <motion.div className="w-2 bg-brand-highlight-aqua" initial={{ height: '30%' }} animate={{ height: ['40%', '20%', '70%', '50%', '80%', '30%', '40%'] }} transition={{ duration: 1.5, repeat: Infinity, ease: 'easeInOut', delay: 0.2 }} />
      <motion.div className="w-2 bg-brand-highlight-aqua" initial={{ height: '50%' }} animate={{ height: ['70%', '40%', '90%', '60%', '20%', '50%', '70%'] }} transition={{ duration: 1.5, repeat: Infinity, ease: 'easeInOut', delay: 0.4 }} />
      <motion.div className="w-2 bg-brand-highlight-aqua" initial={{ height: '20%' }} animate={{ height: ['30%', '60%', '20%', '80%', '50%', '90%', '30%'] }} transition={{ duration: 1.5, repeat: Infinity, ease: 'easeInOut', delay: 0.6 }} />
    </div>
  </div>
);

export default function InterviewScreen() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const interviewId = parseInt(id || '0');
  
  const { data: interview, isLoading } = useInterview(interviewId);
  const startInterviewMutation = useStartInterview();
  const { startHeartbeat, stopHeartbeat, isActive: heartbeatActive } = useHeartbeat(interviewId);
  const { isRecording, recordingTime, startRecording, stopRecording } = useInterviewRecording(interviewId, 1);
  
  const [consentGiven, setConsentGiven] = useState(false);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [mediaStream, setMediaStream] = useState<MediaStream | null>(null);
  const videoRef = useRef<HTMLVideoElement>(null);

  const questions = [
    "Расскажите о себе и своем опыте работы.",
    "Почему вы хотите работать в нашей компании?",
    "Какие у вас сильные стороны?",
    "Расскажите о сложной задаче, которую вам приходилось решать.",
    "Как вы работаете в команде?",
    "Какие у вас планы на развитие?",
  ];

  useEffect(() => {
    if (interview?.status === 'STARTED' && !heartbeatActive) startHeartbeat();
    return () => { stopHeartbeat(); };
  }, [interview?.status, heartbeatActive, startHeartbeat, stopHeartbeat]);

  useEffect(() => {
    const requestPermissions = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        setMediaStream(stream);
        if (videoRef.current) videoRef.current.srcObject = stream;
      } catch (error) {
        toast.error('Не удалось получить доступ к камере и микрофону.');
      }
    };
    requestPermissions();
    return () => { mediaStream?.getTracks().forEach(track => track.stop()); };
  }, []);

  const handleStartInterview = async () => {
    if (!consentGiven) {
      toast.error('Необходимо дать согласие на обработку данных.');
      return;
    }
    try {
      await startInterviewMutation.mutateAsync(interviewId);
      toast.success('Интервью началось!');
    } catch (error) {
      console.error('Не удалось начать интервью:', error);
    }
  };

  const handleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
      setIsFullscreen(true);
    } else {
      document.exitFullscreen();
      setIsFullscreen(false);
    }
  };

  const formatTime = (seconds: number) => new Date(seconds * 1000).toISOString().substr(14, 5);

  const handleNextQuestion = () => {
    if (currentQuestion < questions.length - 1) {
      setCurrentQuestion(currentQuestion + 1);
    } else {
      toast.success('Интервью завершено!');
      navigate('/app/dashboard');
    }
  };

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <Loader />
        <p className="mt-4 text-white/80">Загрузка интервью...</p>
      </div>
    );
  }

  if (!interview) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen text-center p-4">
        <X size={64} className="text-brand-highlight-pink" />
        <h1 className="text-3xl font-bold mt-4">Интервью не найдено</h1>
        <Button onClick={() => navigate('/app/dashboard')} className="mt-6">
          Назад к панели
        </Button>
      </div>
    );
  }

  if (interview.status === 'CREATED') {
    return (
      <div className="min-h-screen flex items-center justify-center p-4">
        <Card className="w-full max-w-3xl" glow="aqua">
          <CardContent className="p-8">
            <div className="text-center">
              <h1 className="text-5xl font-bold text-white mb-2">Добро пожаловать на интервью</h1>
              <p className="text-white/60">Кандидат: {interview.candidateName} на вакансию {interview.vacancyTitle}</p>
            </div>

            <div className="my-8 space-y-6">
              <Card>
                <CardContent className="p-4">
                  <h2 className="font-bold text-lg mb-2 text-brand-highlight-aqua">Технические требования</h2>
                  <ul className="text-sm text-white/70 space-y-2">
                    <li className="flex items-center gap-2"><Wifi size={16} /> Стабильное интернет-соединение</li>
                    <li className="flex items-center gap-2"><Video size={16} /> Рабочая камера и микрофон</li>
                    <li className="flex items-center gap-2"><X size={16} /> Не переключайте вкладки во время интервью</li>
                  </ul>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="p-4">
                  <h2 className="font-bold text-lg mb-2 text-brand-highlight-pink">Согласие на обработку данных</h2>
                  <label className="flex items-start gap-3">
                    <input type="checkbox" checked={consentGiven} onChange={(e) => setConsentGiven(e.target.checked)} className="mt-1 h-4 w-4 accent-brand-accent" />
                    <span className="text-sm text-white/70">Я даю согласие на запись и обработку моих персональных данных, включая аудио и видео материалы, для целей проведения интервью и оценки.</span>
                  </label>
                </CardContent>
              </Card>
            </div>

            <Button onClick={handleStartInterview} disabled={!consentGiven || startInterviewMutation.isLoading} isLoading={startInterviewMutation.isLoading} size="lg" className="w-full">
              Начать интервью
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-screen bg-brand-bg text-white">
      {/* Header */}
      <header className="flex-shrink-0">
        <Card className="rounded-none border-x-0 border-t-0" glow="accent">
          <CardContent className="p-3 flex justify-between items-center">
            <div>
              <h1 className="text-xl font-bold">{interview.candidateName}</h1>
              <p className="text-sm text-white/60">{interview.vacancyTitle}</p>
            </div>
            <div className="flex items-center gap-4">
              <div className="text-sm"><span className="text-white/60">Вопрос:</span> <span className="font-bold text-brand-highlight-aqua">{currentQuestion + 1}/{questions.length}</span></div>
              <div className="text-sm"><span className="text-white/60">Время:</span> <span className="font-bold text-brand-highlight-aqua">{formatTime(recordingTime)}</span></div>
              <Button onClick={handleFullscreen} variant="ghost" size="icon">
                {isFullscreen ? <Minimize size={20} /> : <Maximize size={20} />}
              </Button>
            </div>
          </CardContent>
        </Card>
      </header>

      {/* Main Content */}
      <main className="flex-1 grid md:grid-cols-3 gap-4 p-4 overflow-hidden">
        {/* Video & Question */}
        <div className="md:col-span-2 flex flex-col gap-4 h-full">
          <div className="flex-1 relative flex items-center justify-center bg-black/30 rounded-lg">
            <video ref={videoRef} autoPlay muted playsInline className="h-full w-full object-contain rounded-lg" />
            {isRecording && (
              <div className="absolute top-4 left-4 flex items-center gap-2 px-3 py-1 rounded-full bg-red-500/80 text-white animate-pulse">
                <Mic size={16} /> ЗАПИСЬ
              </div>
            )}
          </div>
          <Card glow="accent" className="flex-shrink-0">
            <CardContent className="p-4">
              <h2 className="font-bold text-xl mb-2 text-brand-highlight-aqua">Вопрос {currentQuestion + 1}</h2>
              <Typewriter key={currentQuestion} text={questions[currentQuestion]} className="text-lg" />
            </CardContent>
          </Card>
        </div>

        {/* Sidebar */}
        <div className="flex flex-col gap-4 overflow-y-auto">
           <Card className="flex-shrink-0">
            <CardContent className="p-4 space-y-3">
              <h3 className="font-bold">Управление</h3>
              <div className="flex gap-2">
                <Button onClick={isRecording ? stopRecording : startRecording} variant={isRecording ? 'destructive' : 'default'} className="flex-1" isLoading={false}>
                  {isRecording ? <Square className="mr-2" size={18} /> : <Mic className="mr-2" size={18} />}
                  {isRecording ? 'Стоп' : 'Запись'}
                </Button>
                <Button onClick={handleNextQuestion} variant="secondary" className="flex-1" disabled={isRecording}>
                  Далее <MoveRight className="ml-2" size={18} />
                </Button>
              </div>
              {isRecording && <AudioVisualizer />}
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 space-y-3">
              <h3 className="font-bold">Статус</h3>
              <div className="text-sm space-y-2">
                <div className="flex justify-between items-center">{mediaStream?.getVideoTracks()[0]?.enabled ? <><Video size={16} /> Камера</> : <><VideoOff size={16} /> Камера</>} <span className={mediaStream?.getVideoTracks()[0]?.enabled ? 'text-green-400' : 'text-red-400'}>Активна</span></div>
                <div className="flex justify-between items-center">{mediaStream?.getAudioTracks()[0]?.enabled ? <><Mic size={16} /> Микрофон</> : <><MicOff size={16} /> Микрофон</>} <span className={mediaStream?.getAudioTracks()[0]?.enabled ? 'text-green-400' : 'text-red-400'}>Активен</span></div>
                <div className="flex justify-between items-center">{heartbeatActive ? <><Wifi size={16} /> Соединение</> : <><WifiOff size={16} /> Соединение</>} <span className={heartbeatActive ? 'text-green-400' : 'text-red-400'}>{heartbeatActive ? 'Стабильно' : 'Потеряно'}</span></div>
              </div>
            </CardContent>
          </Card>
           <Card>
            <CardContent className="p-4">
              <h3 className="font-bold">Прогресс</h3>
              <div className="w-full bg-white/10 rounded-full h-2.5 mt-2">
                <motion.div className="bg-brand-accent h-2.5 rounded-full" initial={{width: '0%'}} animate={{width: `${((currentQuestion + 1) / questions.length) * 100}%`}} />
              </div>
              <p className="text-xs text-right mt-1 text-white/60">{currentQuestion + 1} из {questions.length} отвечено</p>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}