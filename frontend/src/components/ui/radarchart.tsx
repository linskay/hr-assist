import * as React from "react"
import { Radar, RadarChart as RechartsRadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer } from "recharts"

const mockData = [
  { subject: 'Tech Skills', A: 85, fullMark: 100 },
  { subject: 'Soft Skills', A: 75, fullMark: 100 },
  { subject: 'Teamwork', A: 90, fullMark: 100 },
  { subject: 'Leadership', A: 60, fullMark: 100 },
  { subject: 'Problem Solving', A: 80, fullMark: 100 },
  { subject: 'Communication', A: 70, fullMark: 100 },
];

export function RadarChart() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <RechartsRadarChart cx="50%" cy="50%" outerRadius="80%" data={mockData}>
        <defs>
          <radialGradient id="radarGradient">
            <stop offset="5%" stopColor="rgba(0, 234, 255, 0.5)" />
            <stop offset="95%" stopColor="rgba(127, 0, 255, 0.5)" />
          </radialGradient>
        </defs>
        <PolarGrid stroke="rgba(255, 255, 255, 0.2)" />
        <PolarAngleAxis dataKey="subject" tick={{ fill: 'rgba(255, 255, 255, 0.7)' }} />
        <PolarRadiusAxis angle={30} domain={[0, 100]} tick={{ fill: 'rgba(255, 255, 255, 0.5)' }} />
        <Radar
          name="Candidate"
          dataKey="A"
          stroke="url(#radarGradient)"
          fill="url(#radarGradient)"
          fillOpacity={0.6}
        />
      </RechartsRadarChart>
    </ResponsiveContainer>
  )
}
