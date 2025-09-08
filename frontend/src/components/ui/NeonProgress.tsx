import React from 'react';

export default function NeonProgress({ value = 0 }: { value?: number }) {
  const v = Math.max(0, Math.min(100, value));
  return (
    <div className="w-full h-3 rounded-full bg-white/10 overflow-hidden">
      <div
        className="h-full rounded-full bg-gradient-to-r from-neon-purple via-neon-pink to-neon-cyan animate-[gradientShift_3s_ease_infinite]"
        style={{ width: `${v}%` }}
      />
    </div>
  );
}
