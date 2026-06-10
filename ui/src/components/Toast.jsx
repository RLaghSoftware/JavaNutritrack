import { useEffect } from 'react'

export default function Toast({ message, onClose, durationMs = 3000 }) {
  useEffect(() => {
    const timer = setTimeout(onClose, durationMs)
    return () => clearTimeout(timer)
  }, [onClose, durationMs])

  return (
    <div className="toast" role="status" aria-live="polite">
      {message}
    </div>
  )
}
