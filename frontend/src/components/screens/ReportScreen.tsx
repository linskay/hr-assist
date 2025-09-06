import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { RadarChart } from "@/components/ui/radarchart";

export default function ReportScreen() {
  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Отчёт по кандидату</h1>
      <div className="grid md:grid-cols-2 gap-6">
        <Card>
          <CardContent>
            <h2 className="text-xl font-semibold mb-2">Общая оценка</h2>
            <p className="text-4xl font-bold">82%</p>
            <RadarChart data={[ /* компетенции */ ]} />
          </CardContent>
        </Card>
        <Card>
          <CardContent>
            <h2 className="text-xl font-semibold mb-2">Цитаты</h2>
            <ul className="list-disc pl-5 space-y-2">
              <li>«Я координировал проект из 5 человек…»</li>
              <li>«Успешно внедрил Jira…»</li>
            </ul>
          </CardContent>
        </Card>
      </div>
      <div className="mt-6">
        <Button>Экспорт PDF</Button>
      </div>
    </div>
  );
}
