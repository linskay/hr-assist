import * as React from "react"
import { Slot } from "@radix-ui/react-slot"
import { cva, type VariantProps } from "class-variance-authority"
import { LoaderCircle } from "lucide-react"

import { cn } from "@/lib/utils"

const buttonVariants = cva(
  "relative inline-flex items-center justify-center whitespace-nowrap rounded-xl text-sm font-bold uppercase tracking-wider ring-offset-background transition-all duration-300 ease-out focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50",
  {
    variants: {
      variant: {
        default: "text-fuchsia-300 bg-transparent overflow-hidden hover:shadow-neon-glow-accent",
        destructive: "border-2 border-brand-highlight-pink text-white hover:shadow-neon-glow-pink group",
        outline: "border-2 border-white/50 bg-transparent text-white/80 hover:border-white hover:text-white",
        secondary: "border-2 border-brand-highlight-aqua bg-transparent text-brand-highlight-aqua hover:shadow-neon-glow-aqua",
        ghost: "hover:bg-white/10 hover:text-white",
        link: "text-brand-highlight-aqua underline-offset-4 hover:underline",
      },
      size: {
        default: "h-11 px-6 py-3",
        sm: "h-9 rounded-md px-4",
        lg: "h-12 rounded-md px-8 text-base",
        icon: "h-10 w-10",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
)

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  asChild?: boolean
  isLoading?: boolean
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, asChild = false, isLoading = false, children, ...props }, ref) => {
    const Comp = asChild ? Slot : "button"

    if (variant === "default") {
      return (
        <Comp
          className={cn(buttonVariants({ variant, size, className }))}
          ref={ref}
          disabled={isLoading || props.disabled}
          {...props}
        >
          <span className="relative z-10">
            {isLoading ? <LoaderCircle className="mr-2 h-5 w-5 animate-spin" /> : null}
            {children}
          </span>
          <span className="absolute inset-0 rounded-xl pointer-events-none">
            <span className="absolute inset-0 rounded-xl border-2 border-transparent border-image-gradient animate-border-path" />
          </span>
          <span className="absolute inset-[2px] rounded-xl bg-black/60 backdrop-blur-sm"></span>
        </Comp>
      )
    }

    return (
      <Comp
        className={cn(buttonVariants({ variant, size, className }))}
        ref={ref}
        disabled={isLoading || props.disabled}
        {...props}
      >
        {isLoading ? <LoaderCircle className="mr-2 h-5 w-5 animate-spin" /> : null}
        {children}
      </Comp>
    )
  }
)
Button.displayName = "Button"

export { Button, buttonVariants }
