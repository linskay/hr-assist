import { Button } from "@/components/ui/button";

export default function InterviewScreen() {
  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gradient-to-r from-indigo-500 to-purple-600">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-lg">
        <h1 className="text-2xl font-bold mb-4">Интервью</h1>
        <div className="mb-4">
          <p className="text-gray-700">Ассистент: Расскажите про ваш опыт работы в команде?</p>
        </div>
        <div className="h-24 bg-gray-100 rounded-lg flex items-center justify-center mb-4">
          <span className="text-gray-500">Голосовая волна</span>
        </div>
        <Button className="w-full">Начать запись</Button>
      </div>
    </div>
  );
}
