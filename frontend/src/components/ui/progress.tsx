import * as React from "react"
import * as ProgressPrimitive from "@radix-ui/react-progress"
import { motion } from "framer-motion"

import { cn } from "@/lib/utils"

const Progress = React.forwardRef<
  React.ElementRef<typeof ProgressPrimitive.Root>,
  React.ComponentPropsWithoutRef<typeof ProgressPrimitive.Root>
>(({ className, value, ...props }, ref) => (
  <ProgressPrimitive.Root
    ref={ref}
    className={cn(
      "relative h-4 w-full overflow-hidden rounded-full bg-white/10",
      className
    )}
    {...props}
  >
    <ProgressPrimitive.Indicator asChild>
      <motion.div
        className="h-full w-full flex-1 bg-gradient-to-r from-brand-highlight-aqua to-brand-accent transition-all"
        style={{ width: `${value || 0}%` }}
        initial={{ x: "-100%" }}
        animate={{ x: "0%" }}
        transition={{ duration: 1, ease: "easeInOut" }}
      />
    </ProgressPrimitive.Indicator>
  </ProgressPrimitive.Root>
))
Progress.displayName = ProgressPrimitive.Root.displayName

export { Progress }
