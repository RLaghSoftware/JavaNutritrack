import { useEffect, useState } from 'react'
import api from '../api/axios'
import LogoutButton from '../components/LogoutButton'
import { useAuth } from '../context/AuthContext'

export default function DashboardPage() {
  const { user } = useAuth()
  const [dashboard, setDashboard] = useState(null)
  const [hello, setHello] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .get('/dashboard')
      .then((res) => setDashboard(res.data))
      .catch(() => setError('Could not load dashboard data.'))

    api
      .get('/hello')
      .then((res) => setHello(res.data.message))
      .catch(() => setHello('Public API unreachable'))
  }, [])

  return (
    <div className="auth-page dashboard-page">
      <div className="auth-card dashboard-card">
        <header className="dashboard-header">
          <div>
            <h1>Dashboard</h1>
            <p className="auth-muted">
              Signed in as <strong>{user?.username}</strong> ({user?.email})
            </p>
          </div>
          <LogoutButton />
        </header>

        {error && <p className="auth-error">{error}</p>}

        {dashboard && (
          <section className="dashboard-section">
            <h2>Protected API</h2>
            <p>{dashboard.message}</p>
            <pre>{JSON.stringify(dashboard.user, null, 2)}</pre>
          </section>
        )}

        <section className="dashboard-section">
          <h2>Public health check</h2>
          <p>{hello}</p>
        </section>

        <p className="auth-muted">
          Role: {user?.role} · Email verified: {user?.emailVerified ? 'yes' : 'no'}
        </p>
      </div>
    </div>
  )
}
