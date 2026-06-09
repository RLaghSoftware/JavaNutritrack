import { Link } from 'react-router-dom'
import LogoutButton from './LogoutButton'
import { useAuth } from '../context/AuthContext'

export default function DashboardHeader() {
  const { user } = useAuth()

  return (
    <header className="dash-header">
      <Link to="/dashboard" className="dash-brand">
        NutriTrack
      </Link>
      <div className="dash-user-area">
        <span className="dash-username">{user?.username}</span>
        <LogoutButton className="dash-logout-btn" />
      </div>
    </header>
  )
}
