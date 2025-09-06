import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { Mic, MicOff, Video, VideoOff, Play, Pause, Square } from 'lucide-react';
import { useInterview, useStartInterview, useHeartbeat, useInterviewRecording } from '../../hooks/useInterview';
import { Button } from '../ui/button';
import { Card } from '../ui/card';

interface InterviewScreenProps {}

export const InterviewScreen: React.FC<InterviewScreenProps> = () => {
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
    "Расскажите о себе и своем опыте работы",
    "Почему вы хотите работать в нашей компании?",
    "Какие у вас сильные стороны?",
    "Расскажите о сложной задаче, которую вам приходилось решать",
    "Как вы работаете в команде?",
    "Какие у вас планы на развитие?",
  ];

  useEffect(() => {
    if (interview?.status === 'STARTED' && !heartbeatActive) {
      startHeartbeat();
    }
    
    return () => {
      stopHeartbeat();
    };
  }, [interview?.status, heartbeatActive, startHeartbeat, stopHeartbeat]);

  useEffect(() => {
    // Request camera and microphone permissions
    const requestPermissions = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: true,
          audio: true
        });
        setMediaStream(stream);
        
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      } catch (error) {
        console.error('Failed to get media permissions:', error);
        toast.error('Не удалось получить доступ к камере и микрофону');
      }
    };

    requestPermissions();

    return () => {
      if (mediaStream) {
        mediaStream.getTracks().forEach(track => track.stop());
      }
    };
  }, []);

  const handleStartInterview = async () => {
    if (!consentGiven) {
      toast.error('Необходимо дать согласие на обработку данных');
      return;
    }

    try {
      await startInterviewMutation.mutateAsync(interviewId);
      toast.success('Интервью запущено!');
    } catch (error) {
      console.error('Failed to start interview:', error);
    }
  };

  const handleConsentChange = (checked: boolean) => {
    setConsentGiven(checked);
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

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleNextQuestion = () => {
    if (currentQuestion < questions.length - 1) {
      setCurrentQuestion(currentQuestion + 1);
    } else {
      toast.success('Интервью завершено!');
      navigate('/dashboard');
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Загрузка интервью...</p>
        </div>
      </div>
    );
  }

  if (!interview) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Интервью не найдено</h1>
          <Button onClick={() => navigate('/dashboard')}>
            Вернуться к дашборду
          </Button>
        </div>
      </div>
    );
  }

  if (interview.status === 'CREATED') {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
        <Card className="w-full max-w-2xl p-8">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900 mb-6">
              Добро пожаловать на интервью!
            </h1>
            
            <div className="mb-8">
              <h2 className="text-xl font-semibold mb-4">Информация об интервью:</h2>
              <div className="text-left space-y-2">
                <p><strong>Кандидат:</strong> {interview.candidateName}</p>
                <p><strong>Вакансия:</strong> {interview.vacancyTitle}</p>
                <p><strong>Статус:</strong> {interview.status}</p>
              </div>
            </div>

            <div className="mb-8">
              <h2 className="text-xl font-semibold mb-4">Согласие на обработку данных:</h2>
              <div className="text-left space-y-4">
                <label className="flex items-start space-x-3">
                  <input
                    type="checkbox"
                    checked={consentGiven}
                    onChange={(e) => handleConsentChange(e.target.checked)}
                    className="mt-1 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                  />
                  <span className="text-sm text-gray-700">
                    Я даю согласие на запись и обработку моих персональных данных, 
                    включая аудио и видео материалы, для целей проведения интервью 
                    и оценки моих профессиональных качеств.
                  </span>
                </label>
              </div>
            </div>

            <div className="mb-8">
              <h2 className="text-xl font-semibold mb-4">Технические требования:</h2>
              <ul className="text-left space-y-2 text-sm text-gray-600">
                <li>• Стабильное интернет-соединение</li>
                <li>• Рабочая камера и микрофон</li>
                <li>• Рекомендуется использовать Chrome или Firefox</li>
                <li>• Не переключайтесь между вкладками во время интервью</li>
              </ul>
            </div>

            <Button
              onClick={handleStartInterview}
              disabled={!consentGiven || startInterviewMutation.isLoading}
              className="w-full"
              size="lg"
            >
              {startInterviewMutation.isLoading ? 'Запуск...' : 'Начать интервью'}
            </Button>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Header */}
      <div className="bg-gray-800 p-4 flex justify-between items-center">
        <div>
          <h1 className="text-xl font-bold">{interview.candidateName}</h1>
          <p className="text-gray-400">{interview.vacancyTitle}</p>
        </div>
        <div className="flex items-center space-x-4">
          <div className="text-sm">
            <span className="text-gray-400">Вопрос:</span> {currentQuestion + 1} из {questions.length}
          </div>
          <div className="text-sm">
            <span className="text-gray-400">Время записи:</span> {formatTime(recordingTime)}
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={handleFullscreen}
          >
            {isFullscreen ? 'Выйти' : 'Полный экран'}
          </Button>
        </div>
      </div>

      <div className="flex h-[calc(100vh-80px)]">
        {/* Video Section */}
        <div className="flex-1 flex flex-col items-center justify-center p-8">
          <div className="relative w-full max-w-2xl">
            <video
              ref={videoRef}
              autoPlay
              muted
              playsInline
              className="w-full rounded-lg shadow-2xl"
            />
            
            {/* Recording indicator */}
            {isRecording && (
              <div className="absolute top-4 left-4 bg-red-600 text-white px-3 py-1 rounded-full flex items-center space-x-2">
                <div className="w-2 h-2 bg-white rounded-full animate-pulse"></div>
                <span className="text-sm font-medium">ИДЕТ ЗАПИСЬ</span>
              </div>
            )}
          </div>

          {/* Question */}
          <div className="mt-8 w-full max-w-2xl">
            <Card className="p-6 bg-gray-800 border-gray-700">
              <h2 className="text-xl font-semibold mb-4">Вопрос {currentQuestion + 1}:</h2>
              <p className="text-lg text-gray-300 mb-6">{questions[currentQuestion]}</p>
              
              <div className="flex justify-center space-x-4">
                <Button
                  onClick={isRecording ? stopRecording : startRecording}
                  variant={isRecording ? "destructive" : "default"}
                  size="lg"
                  className="flex items-center space-x-2"
                >
                  {isRecording ? (
                    <>
                      <Square className="w-5 h-5" />
                      <span>Остановить запись</span>
                    </>
                  ) : (
                    <>
                      <Mic className="w-5 h-5" />
                      <span>Начать запись</span>
                    </>
                  )}
                </Button>
                
                {currentQuestion < questions.length - 1 && (
                  <Button
                    onClick={handleNextQuestion}
                    variant="outline"
                    size="lg"
                    disabled={isRecording}
                  >
                    Следующий вопрос
                  </Button>
                )}
              </div>
            </Card>
          </div>
        </div>

        {/* Sidebar */}
        <div className="w-80 bg-gray-800 p-6">
          <h3 className="text-lg font-semibold mb-4">Статус интервью</h3>
          
          <div className="space-y-4">
            <div className="bg-gray-700 p-4 rounded-lg">
              <h4 className="font-medium mb-2">Технические параметры</h4>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Камера:</span>
                  <span className="text-green-400">✓ Активна</span>
                </div>
                <div className="flex justify-between">
                  <span>Микрофон:</span>
                  <span className="text-green-400">✓ Активен</span>
                </div>
                <div className="flex justify-between">
                  <span>Heartbeat:</span>
                  <span className={heartbeatActive ? "text-green-400" : "text-red-400"}>
                    {heartbeatActive ? "✓ Активен" : "✗ Неактивен"}
                  </span>
                </div>
              </div>
            </div>

            <div className="bg-gray-700 p-4 rounded-lg">
              <h4 className="font-medium mb-2">Прогресс</h4>
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span>Вопросы:</span>
                  <span>{currentQuestion + 1} / {questions.length}</span>
                </div>
                <div className="w-full bg-gray-600 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${((currentQuestion + 1) / questions.length) * 100}%` }}
                  ></div>
                </div>
              </div>
            </div>

            <div className="bg-gray-700 p-4 rounded-lg">
              <h4 className="font-medium mb-2">Инструкции</h4>
              <ul className="text-sm space-y-1 text-gray-300">
                <li>• Говорите четко и громко</li>
                <li>• Смотрите в камеру</li>
                <li>• Не переключайте вкладки</li>
                <li>• Отвечайте развернуто</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};