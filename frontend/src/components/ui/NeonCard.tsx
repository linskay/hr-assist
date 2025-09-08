import React from 'react';
import { motion } from 'framer-motion';

interface NeonCardProps {
  children: React.ReactNode;
  className?: string;
}

export const NeonCard: React.FC<NeonCardProps> = ({ children, className = '' }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className={`card-neon border border-neon-purple/30 neon-glow-purple ${className}`}
    >
      {children}
    </motion.div>
  );
};

export default NeonCard;

