import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/lib/utils"

const badgeVariants = cva(
  "inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2",
  {
    variants: {
      variant: {
        default:
          "border-brand-accent/50 bg-brand-accent/10 text-brand-accent hover:shadow-neon-glow-accent",
        secondary:
          "border-brand-highlight-aqua/50 bg-brand-highlight-aqua/10 text-brand-highlight-aqua hover:shadow-neon-glow-aqua",
        destructive:
          "border-brand-highlight-pink/50 bg-brand-highlight-pink/10 text-brand-highlight-pink hover:shadow-neon-glow-pink",
        outline: "text-white/70 border-white/30",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof badgeVariants> {}

function Badge({ className, variant, ...props }: BadgeProps) {
  return (
    <div className={cn(badgeVariants({ variant }), className)} {...props} />
  )
}

export { Badge, badgeVariants }
