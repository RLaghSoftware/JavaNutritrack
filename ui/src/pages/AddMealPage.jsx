import { Link } from 'react-router-dom'
import DashboardHeader from '../components/DashboardHeader'

export default function AddMealPage() {
  return (
    <div className="dash-layout">
      <DashboardHeader />
      <main className="dash-main">
        <Link to="/dashboard" className="dash-back-link">
          ← Back to dashboard
        </Link>
        <h1>Add meal</h1>
        <p className="dash-placeholder">Meal logging coming soon.</p>
      </main>
    </div>
  )
}
