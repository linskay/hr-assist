import React from 'react';

interface NeonInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export const NeonInput: React.FC<NeonInputProps> = ({ label, className = '', ...props }) => {
  return (
    <label className="block space-y-2">
      {label && <span className="text-sm opacity-80">{label}</span>}
      <input className={`neon-input ${className}`} {...props} />
    </label>
  );
};

export default NeonInput;

