import React from 'react';
import ReactMarkdown from 'react-markdown';

interface MarkdownContentProps {
  content: string;
  className?: string;
}

export const MarkdownContent: React.FC<MarkdownContentProps> = ({ content, className = '' }) => {
  // Sanitize content: replace stray ++ or +++, and trim extra whitespace
  const sanitizedContent = content
    ? content.replace(/\+{2,}/g, '+').trim()
    : '';

  return (
    <div className={`markdown-content text-left leading-relaxed text-inherit ${className}`}>
      <ReactMarkdown
        components={{
          p: ({ children }) => <p className="mb-2 last:mb-0">{children}</p>,
          strong: ({ children }) => <strong className="font-bold text-inherit">{children}</strong>,
          em: ({ children }) => <em className="italic">{children}</em>,
          ul: ({ children }) => <ul className="list-disc list-inside space-y-1 mb-2 last:mb-0 ml-1">{children}</ul>,
          ol: ({ children }) => <ol className="list-decimal list-inside space-y-1 mb-2 last:mb-0 ml-1">{children}</ol>,
          li: ({ children }) => <li className="text-inherit">{children}</li>,
          h1: ({ children }) => <h1 className="text-base font-bold mb-1 mt-2 text-inherit">{children}</h1>,
          h2: ({ children }) => <h2 className="text-sm font-bold mb-1 mt-2 text-inherit">{children}</h2>,
          h3: ({ children }) => <h3 className="text-xs font-bold uppercase tracking-wider mb-1 mt-2 text-inherit">{children}</h3>,
          h4: ({ children }) => <h4 className="text-xs font-bold mb-1 mt-1 text-inherit">{children}</h4>,
          code: ({ children }) => (
            <code className="bg-black/10 dark:bg-white/10 px-1 py-0.5 rounded text-[11px] font-mono">
              {children}
            </code>
          ),
        }}
      >
        {sanitizedContent}
      </ReactMarkdown>
    </div>
  );
};
