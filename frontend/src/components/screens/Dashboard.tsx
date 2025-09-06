import { Card, CardContent } from "@/components/ui/card";
import { Chart } from "@/components/ui/chart";

export default function Dashboard() {
  return (
    <div className="p-6 grid gap-6 grid-cols-1 md:grid-cols-3">
      <div className="col-span-2">
        <h1 className="text-2xl font-bold mb-4">Добро пожаловать, HR!</h1>
        <div className="grid gap-4 grid-cols-1 md:grid-cols-2">
          <Card>
            <CardContent>
              <h2 className="text-xl font-semibold mb-2">Интервью сегодня</h2>
              <p className="text-gray-600">3 запланировано</p>
            </CardContent>
          </Card>
          <Card>
            <CardContent>
              <h2 className="text-xl font-semibold mb-2">Открытые вакансии</h2>
              <p className="text-gray-600">5 активных</p>
            </CardContent>
          </Card>
        </div>
      </div>
      <div>
        <Card>
          <CardContent>
            <h2 className="text-xl font-semibold mb-2">Конверсия кандидатов</h2>
            <div className="h-48"><Chart /></div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
