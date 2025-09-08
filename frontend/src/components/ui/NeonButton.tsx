import React from 'react';
import { motion } from 'framer-motion';

interface NeonButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'purple' | 'cyan' | 'pink' | 'blue';
  fullWidth?: boolean;
}

export const NeonButton: React.FC<NeonButtonProps> = ({
  children,
  variant = 'purple',
  fullWidth,
  className = '',
  ...props
}) => {
  const colorClassMap: Record<string, string> = {
    purple: 'border-neon-purple text-neon-purple hover:bg-neon-purple',
    cyan: 'border-neon-cyan text-neon-cyan hover:bg-neon-cyan',
    pink: 'border-neon-pink text-neon-pink hover:bg-neon-pink',
    blue: 'border-neon-blue text-neon-blue hover:bg-neon-blue',
  };

  return (
    <motion.button
      whileHover={{ scale: 1.03, boxShadow: '0 0 24px #7f00ff' }}
      whileTap={{ scale: 0.97 }}
      className={`relative px-6 py-3 rounded-xl font-bold uppercase tracking-wider overflow-hidden transition-all duration-300 ease-in-out border-2 bg-transparent ${
        colorClassMap[variant]
      } ${fullWidth ? 'w-full' : ''} ${className}`}
      {...props}
    >
      <span className="relative z-10 flex items-center justify-center gap-2">{children}</span>
      <span className="absolute inset-[2px] rounded-xl bg-black/40 backdrop-blur-sm"></span>
      <span className="absolute inset-0 rounded-xl pointer-events-none">
        <span className="absolute inset-0 rounded-xl border-2 border-transparent animate-borderPathGradient"></span>
      </span>
    </motion.button>
  );
};

export default NeonButton;

