import { Button } from "@/components/ui/button";
import { useSpeech } from "@/lib/useSpeech";
import { useState, useEffect } from "react";

interface Message {
  author: 'assistant' | 'user';
  text: string;
}

export default function InterviewScreen() {
  const [messages, setMessages] = useState<Message[]>([
    { author: 'assistant', text: 'Здравствуйте! Расскажите про ваш опыт работы в команде?' }
  ]);
  const [isFetchingNextQuestion, setIsFetchingNextQuestion] = useState(false);
  const {
    isListening,
    transcript,
    startListening,
    stopListening,
    speak,
    browserSupportsSpeechRecognition
  } = useSpeech();

  // Speak the initial message
  useEffect(() => {
    speak(messages[0].text);
  }, [speak]);

  const handleToggleListening = () => {
    if (isListening) {
      stopListening();
    } else {
      startListening();
    }
  };

  const handleGenerateReport = async () => {
    try {
      const response = await fetch('/api/interviews/generate-report', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          // TODO: Replace with actual IDs
          vacancyId: 1,
          candidateId: 1,
          messages: messages,
        }),
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const report = await response.json();
      alert(`Отчет создан!\nПроцент соответствия: ${report.matchPercentage}%\nРекомендация: ${report.recommendation}`);
    } catch (error) {
      console.error('Error generating report:', error);
      alert('Произошла ошибка при создании отчета.');
    }
  };

  const getNextQuestion = async (updatedMessages: Message[]) => {
    setIsFetchingNextQuestion(true);
    try {
      const response = await fetch('/api/interviews/next-question', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          // TODO: Replace with actual IDs
          vacancyId: 1,
          candidateId: 1,
          messages: updatedMessages,
        }),
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      const assistantQuestion = { author: 'assistant', text: data.nextQuestion };
      setMessages(prev => [...prev, assistantQuestion]);
      speak(assistantQuestion.text);
    } catch (error) {
      console.error('Error fetching next question:', error);
      const errorMsg = { author: 'assistant', text: 'Извините, у меня возникла проблема. Попробуйте еще раз.' };
      setMessages(prev => [...prev, errorMsg]);
      speak(errorMsg.text);
    } finally {
      setIsFetchingNextQuestion(false);
    }
  };

  // When the user stops speaking, add their message and get the next question
  useEffect(() => {
    if (!isListening && transcript) {
        const userMessage = { author: 'user', text: transcript };
        const updatedMessages = [...messages, userMessage];
        setMessages(updatedMessages);
        getNextQuestion(updatedMessages);
    }
  }, [isListening, transcript]);

  if (!browserSupportsSpeechRecognition) {
    return (
      <div className="flex items-center justify-center h-screen text-red-500">
        <p>Ваш браузер не поддерживает распознавание речи.</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gradient-to-r from-indigo-500 to-purple-600 p-4">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-2xl">
        <h1 className="text-2xl font-bold mb-4 text-center">Интервью</h1>
        <div className="space-y-4 mb-4 h-80 overflow-y-auto p-2 bg-gray-50 rounded-lg">
          {messages.map((msg, index) => (
            <div key={index} className={`flex ${msg.author === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div className={`rounded-lg px-4 py-2 text-white ${msg.author === 'user' ? 'bg-blue-500' : 'bg-gray-600'}`}>
                {msg.text}
              </div>
            </div>
          ))}
          {isListening && (
             <div className="flex justify-end">
              <div className="rounded-lg px-4 py-2 text-white bg-blue-500 opacity-70">
                {transcript || "..."}
              </div>
            </div>
          )}
        </div>
        {messages.at(-1)?.text.includes("На этом у меня все вопросы") ? (
          <Button onClick={handleGenerateReport} className="w-full font-bold py-3 bg-green-600 hover:bg-green-700">
            Сформировать отчет
          </Button>
        ) : (
          <Button onClick={handleToggleListening} className="w-full font-bold py-3" disabled={isFetchingNextQuestion}>
            {isListening ? 'Закончить запись' : 'Начать запись'}
          </Button>
        )}
      </div>
    </div>
  );
}
