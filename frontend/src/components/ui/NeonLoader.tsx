import React from 'react';

export default function NeonLoader({ size = 64 }: { size?: number }) {
  const px = `${size}px`;
  return (
    <div
      className="relative"
      style={{ width: px, height: px }}
      aria-label="Loading"
      role="status"
    >
      <div className="absolute inset-0 rounded-full border-2 border-neon-purple/40 animate-spin" />
      <div className="absolute inset-2 rounded-full border-2 border-neon-cyan/60 animate-pulse" />
      <div className="absolute inset-4 rounded-full bg-gradient-to-r from-neon-purple/20 to-neon-cyan/20 blur" />
    </div>
  );
}


