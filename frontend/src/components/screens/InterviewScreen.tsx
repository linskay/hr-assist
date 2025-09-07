import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { useInterview, useStartInterview, useHeartbeat, useInterviewRecording } from '../../hooks/useInterview';

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
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-neon-blue mx-auto"></div>
          <p className="mt-4 opacity-80">Загрузка интервью...</p>
        </div>
      </div>
    );
  }

  if (!interview) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">❌ Интервью не найдено</h1>
          <button 
            onClick={() => navigate('/dashboard')}
            className="btn-neon"
          >
            🏠 Вернуться к дашборду
          </button>
        </div>
      </div>
    );
  }

  if (interview.status === 'CREATED') {
    return (
      <div className="min-h-screen flex items-center justify-center p-4">
        <div className="card-neon w-full max-w-2xl p-8">
          <div className="text-center">
            <h1 className="text-5xl font-black text-gradient mb-6">
              🎤 Добро пожаловать на интервью!
            </h1>
            
            <div className="mb-8">
              <h2 className="text-2xl font-bold mb-4">📋 Информация об интервью:</h2>
              <div className="text-left space-y-3 bg-white bg-opacity-5 p-4 rounded-lg">
                <p><strong>👤 Кандидат:</strong> {interview.candidateName}</p>
                <p><strong>💼 Вакансия:</strong> {interview.vacancyTitle}</p>
                <p><strong>📊 Статус:</strong> {interview.status}</p>
              </div>
            </div>

            <div className="mb-8">
              <h2 className="text-2xl font-bold mb-4">📝 Согласие на обработку данных:</h2>
              <div className="text-left space-y-4">
                <label className="flex items-start space-x-3">
                  <input
                    type="checkbox"
                    checked={consentGiven}
                    onChange={(e) => handleConsentChange(e.target.checked)}
                    className="mt-1 h-5 w-5 text-neon-blue focus:ring-neon-blue border-neon-blue rounded"
                  />
                  <span className="text-sm opacity-80">
                    Я даю согласие на запись и обработку моих персональных данных, 
                    включая аудио и видео материалы, для целей проведения интервью 
                    и оценки моих профессиональных качеств.
                  </span>
                </label>
              </div>
            </div>

            <div className="mb-8">
              <h2 className="text-2xl font-bold mb-4">⚙️ Технические требования:</h2>
              <ul className="text-left space-y-2 text-sm opacity-70 bg-white bg-opacity-5 p-4 rounded-lg">
                <li>🌐 Стабильное интернет-соединение</li>
                <li>📹 Рабочая камера и микрофон</li>
                <li>🌍 Рекомендуется использовать Chrome или Firefox</li>
                <li>🚫 Не переключайтесь между вкладками во время интервью</li>
              </ul>
            </div>

            <button
              onClick={handleStartInterview}
              disabled={!consentGiven || startInterviewMutation.isLoading}
              className={`btn-neon w-full text-lg py-4 ${!consentGiven ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              {startInterviewMutation.isLoading ? '🚀 Запуск...' : '🎬 Начать интервью'}
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen text-white">
      {/* Header */}
      <div className="glass border-b border-neon-blue border-opacity-20 p-4 flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gradient">👤 {interview.candidateName}</h1>
          <p className="opacity-70">💼 {interview.vacancyTitle}</p>
        </div>
        <div className="flex items-center space-x-6">
          <div className="text-sm bg-white bg-opacity-5 px-3 py-2 rounded-lg">
            <span className="opacity-70">❓ Вопрос:</span> <span className="text-neon-blue font-bold">{currentQuestion + 1} из {questions.length}</span>
          </div>
          <div className="text-sm bg-white bg-opacity-5 px-3 py-2 rounded-lg">
            <span className="opacity-70">⏱️ Время записи:</span> <span className="text-neon-cyan font-bold">{formatTime(recordingTime)}</span>
          </div>
          <button
            onClick={handleFullscreen}
            className="btn-neon-cyan text-sm"
          >
            {isFullscreen ? '📱 Выйти' : '🖥️ Полный экран'}
          </button>
        </div>
      </div>

      <div className="flex h-screen">
        {/* Video Section */}
        <div className="flex-1 flex flex-col items-center justify-center p-8">
          <div className="relative w-full max-w-2xl">
            <video
              ref={videoRef}
              autoPlay
              muted
              playsInline
              className="w-full rounded-lg shadow-2xl border-2 border-neon-blue border-opacity-30"
            />
            
            {/* Recording indicator */}
            {isRecording && (
              <div className="absolute top-4 left-4 bg-red-600 text-white px-4 py-2 rounded-full flex items-center space-x-2 neon-glow-pink">
                <div className="w-3 h-3 bg-white rounded-full animate-pulse"></div>
                <span className="text-sm font-bold">🔴 ИДЕТ ЗАПИСЬ</span>
              </div>
            )}
          </div>

          {/* Question */}
          <div className="mt-8 w-full max-w-2xl">
            <div className="card-neon p-6">
              <h2 className="text-2xl font-bold mb-4">❓ Вопрос {currentQuestion + 1}:</h2>
              <p className="text-lg opacity-90 mb-6">{questions[currentQuestion]}</p>
              
              <div className="flex justify-center space-x-4">
                <button
                  onClick={isRecording ? stopRecording : startRecording}
                  className={`${isRecording ? 'btn-neon-pink' : 'btn-neon'} text-lg px-6 py-3`}
                >
                  {isRecording ? (
                    <>
                      <span>⏹️</span>
                      <span>Остановить запись</span>
                    </>
                  ) : (
                    <>
                      <span>🎤</span>
                      <span>Начать запись</span>
                    </>
                  )}
                </button>
                
                {currentQuestion < questions.length - 1 && (
                  <button
                    onClick={handleNextQuestion}
                    className="btn-neon-cyan text-lg px-6 py-3"
                    disabled={isRecording}
                  >
                    <span>➡️</span>
                    <span>Следующий вопрос</span>
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="w-80 glass p-6 border-l border-neon-blue border-opacity-20">
          <h3 className="text-2xl font-bold mb-6">📊 Статус интервью</h3>
          
          <div className="space-y-6">
            <div className="card-neon p-4">
              <h4 className="font-bold mb-3 text-lg">⚙️ Технические параметры</h4>
              <div className="space-y-3 text-sm">
                <div className="flex justify-between items-center">
                  <span className="opacity-80">📹 Камера:</span>
                  <span className="text-neon-green font-bold">✅ Активна</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="opacity-80">🎤 Микрофон:</span>
                  <span className="text-neon-green font-bold">✅ Активен</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="opacity-80">💓 Heartbeat:</span>
                  <span className={heartbeatActive ? "text-neon-green font-bold" : "text-red-400 font-bold"}>
                    {heartbeatActive ? "✅ Активен" : "❌ Неактивен"}
                  </span>
                </div>
              </div>
            </div>

            <div className="card-neon p-4">
              <h4 className="font-bold mb-3 text-lg">📈 Прогресс</h4>
              <div className="space-y-3">
                <div className="flex justify-between text-sm">
                  <span className="opacity-80">❓ Вопросы:</span>
                  <span className="text-neon-blue font-bold">{currentQuestion + 1} / {questions.length}</span>
                </div>
                <div className="w-full bg-white bg-opacity-10 rounded-full h-3">
                  <div
                    className="bg-gradient-to-r from-neon-blue to-neon-purple h-3 rounded-full transition-all duration-300"
                    style={{ width: `${((currentQuestion + 1) / questions.length) * 100}%` }}
                  ></div>
                </div>
                <div className="text-center text-sm opacity-70">
                  {Math.round(((currentQuestion + 1) / questions.length) * 100)}% завершено
                </div>
              </div>
            </div>

            <div className="card-neon p-4">
              <h4 className="font-bold mb-3 text-lg">📋 Инструкции</h4>
              <ul className="text-sm space-y-2 opacity-80">
                <li className="flex items-center space-x-2">
                  <span>🗣️</span>
                  <span>Говорите четко и громко</span>
                </li>
                <li className="flex items-center space-x-2">
                  <span>👁️</span>
                  <span>Смотрите в камеру</span>
                </li>
                <li className="flex items-center space-x-2">
                  <span>🚫</span>
                  <span>Не переключайте вкладки</span>
                </li>
                <li className="flex items-center space-x-2">
                  <span>💬</span>
                  <span>Отвечайте развернуто</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}