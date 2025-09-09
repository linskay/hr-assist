import React from 'react'
import { Card, CardContent } from '@/components/ui/card'
import { cn } from '@/lib/utils'

type IconType = React.ComponentType<React.SVGProps<SVGSVGElement>>

type StatCardProps = {
  title: string
  value: string | number
  icon?: IconType
  color?: 'aqua' | 'pink' | 'purple' | 'accent' | 'none'
  className?: string
}

const colorToGlow: Record<NonNullable<StatCardProps['color']>, string> = {
  aqua: 'shadow-neon-glow-aqua',
  pink: 'shadow-neon-glow-pink',
  purple: 'shadow-neon-glow-accent',
  accent: 'shadow-neon-glow-accent',
  none: ''
}

const StatCard: React.FC<StatCardProps> = ({ title, value, icon: Icon, color = 'aqua', className }) => {
  return (
    <Card className={cn('glass-card', colorToGlow[color], className)}>
      <CardContent className="flex items-center justify-between">
        <div>
          <div className="text-sm text-white/60 mb-1">{title}</div>
          <div className="text-3xl font-bold text-white">{value}</div>
        </div>
        {Icon ? <Icon className="h-8 w-8 text-white/70" /> : null}
      </CardContent>
    </Card>
  )
}

export default StatCard


