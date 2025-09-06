import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/lib/utils"

const cardVariants = cva(
  "glass-card transition-all duration-300",
  {
    variants: {
      glow: {
        none: "",
        accent: "hover:shadow-neon-glow-accent",
        aqua: "hover:shadow-neon-glow-aqua",
        pink: "hover:shadow-neon-glow-pink",
      },
    },
    defaultVariants: {
      glow: "none",
    },
  }
)

export interface CardProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof cardVariants> {}

const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({ className, glow, ...props }, ref) => (
    <div
      ref={ref}
      className={cn(cardVariants({ glow, className }))}
      {...props}
    />
  )
)
Card.displayName = "Card"

const CardContent = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div ref={ref} className={cn("p-6", className)} {...props} />
))
CardContent.displayName = "CardContent"

export { Card, CardContent, cardVariants }
