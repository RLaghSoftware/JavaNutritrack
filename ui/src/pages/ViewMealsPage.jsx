import { Link } from 'react-router-dom'
import DashboardHeader from '../components/DashboardHeader'

export default function ViewMealsPage() {
  return (
    <div className="dash-layout">
      <DashboardHeader />
      <main className="dash-main">
        <Link to="/dashboard" className="dash-back-link">
          ← Back to dashboard
        </Link>
        <h1>View meals</h1>
        <p className="dash-placeholder">Your meal history will appear here.</p>
      </main>
    </div>
  )
}
