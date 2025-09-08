import * as React from "react"
import * as TooltipPrimitive from "@radix-ui/react-tooltip"
import { motion } from "framer-motion"
import { cn } from "@/lib/utils"

const TooltipProvider = TooltipPrimitive.Provider

const Tooltip = TooltipPrimitive.Root

const TooltipTrigger = TooltipPrimitive.Trigger

const TooltipContent = React.forwardRef<
  React.ElementRef<typeof TooltipPrimitive.Content>,
  React.ComponentPropsWithoutRef<typeof TooltipPrimitive.Content>
>(({ className, sideOffset = 4, ...props }, ref) => (
  <TooltipPrimitive.Content asChild>
    <motion.div
      ref={ref}
      initial={{ opacity: 0, y: 5 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: 5 }}
      transition={{ duration: 0.15, ease: "easeOut" }}
      sideOffset={sideOffset}
      className={cn(
        "z-50 overflow-hidden rounded-md px-3 py-1.5 text-xs text-white glass-card border-brand-accent",
        className
      )}
      {...props}
    />
  </TooltipPrimitive.Content>
))
TooltipContent.displayName = TooltipPrimitive.Content.displayName

export { Tooltip, TooltipTrigger, TooltipContent, TooltipProvider }
