import { useEffect } from 'react'

export default function Toast({ message, type = 'success', onClose, durationMs = 3000 }) {
  useEffect(() => {
    const timer = setTimeout(onClose, durationMs)
    return () => clearTimeout(timer)
  }, [onClose, durationMs])

  return (
    <div
      className={`toast${type === 'error' ? ' toast--error' : ''}`}
      role={type === 'error' ? 'alert' : 'status'}
      aria-live="polite"
    >
      {message}
    </div>
  )
}
