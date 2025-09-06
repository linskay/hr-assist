import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/lib/utils"

const loaderVariants = cva(
  "relative inline-block h-16 w-16 rounded-full animate-pulse",
  {
    variants: {
      variant: {
        accent: "bg-brand-accent shadow-neon-glow-accent",
        aqua: "bg-brand-highlight-aqua shadow-neon-glow-aqua",
        pink: "bg-brand-highlight-pink shadow-neon-glow-pink",
      },
    },
    defaultVariants: {
      variant: "accent",
    },
  }
)

export interface LoaderProps extends React.HTMLAttributes<HTMLDivElement>, VariantProps<typeof loaderVariants> {}

const Loader = React.forwardRef<HTMLDivElement, LoaderProps>(
  ({ className, variant, ...props }, ref) => {
    return (
      <div className="flex items-center justify-center p-4">
        <div
          ref={ref}
          className={cn(loaderVariants({ variant, className }))}
          {...props}
        />
      </div>
    )
  }
)

Loader.displayName = "Loader"

export { Loader }
