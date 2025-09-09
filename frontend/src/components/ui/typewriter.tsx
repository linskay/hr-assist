import { motion } from "framer-motion";
import { FC } from "react";

interface TypewriterProps {
  text: string;
  className?: string;
}

const Typewriter: FC<TypewriterProps> = ({ text, className }) => {
  const words = text.split(" ").map(word => [...word, " "]);

  const variants = {
    hidden: { opacity: 0 },
    visible: (i: number) => ({
      opacity: 1,
      transition: {
        delay: i * 0.05,
      },
    }),
  };

  return (
    <motion.p className={className}>
      {words.flat().map((char, i) => (
        <motion.span key={i} custom={i} variants={variants} initial="hidden" animate="visible">
          {char}
        </motion.span>
      ))}
    </motion.p>
  );
};

export default Typewriter;
