import { Link } from 'react-router-dom'
import DashboardHeader from '../components/DashboardHeader'

export default function ViewMetricsPage() {
  return (
    <div className="dash-layout">
      <DashboardHeader />
      <main className="dash-main">
        <Link to="/dashboard" className="dash-back-link">
          ← Back to dashboard
        </Link>
        <h1>View metrics</h1>
        <p className="dash-placeholder">Nutrition metrics coming soon.</p>
      </main>
    </div>
  )
}
