import { Link } from 'react-router-dom'
import DashboardHeader from '../components/DashboardHeader'
import { useAuth } from '../context/AuthContext'

export default function DashboardPage() {
  const { user } = useAuth()

  return (
    <div className="dash-layout">
      <DashboardHeader />
      <main className="dash-main">
        <h1>Welcome back, {user?.username}</h1>
        <p className="dash-subtitle">What would you like to do today?</p>

        <div className="dash-actions">
          <Link to="/meals/add" className="dash-action-btn dash-action-btn--primary">
            Add Meal
          </Link>
          <Link to="/meals" className="dash-action-btn">
            View Meals
          </Link>
          <Link to="/metrics" className="dash-action-btn">
            View Metrics
          </Link>
        </div>
      </main>
    </div>
  )
}
