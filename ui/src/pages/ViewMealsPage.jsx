import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import DashboardHeader from '../components/DashboardHeader'

function todayIso() {
  return new Date().toISOString().split('T')[0]
}

function weekAgoIso() {
  const d = new Date()
  d.setDate(d.getDate() - 7)
  return d.toISOString().split('T')[0]
}

function formatDate(dateStr) {
  const [year, month, day] = dateStr.split('-')
  return new Date(year, month - 1, day).toLocaleDateString(undefined, {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

export default function ViewMealsPage() {
  const [startDate, setStartDate] = useState(weekAgoIso())
  const [endDate, setEndDate] = useState(todayIso())
  const [meals, setMeals] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleApplyFilter = async (e) => {
    e.preventDefault()
    setError('')

    if (!startDate || !endDate) {
      setError('Select both start and end dates.')
      return
    }
    if (startDate > endDate) {
      setError('End date must be on or after start date.')
      return
    }

    setLoading(true)
    try {
      const { data } = await api.get('/meals', {
        params: { startDate, endDate },
      })
      setMeals(data)
    } catch (err) {
      const msg =
        err.response?.data?.message || 'Could not load meals. Try again.'
      setError(msg)
      setMeals(null)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="dash-layout">
      <DashboardHeader />
      <main className="dash-main">
        <Link to="/dashboard" className="dash-back-link">
          ← Back to dashboard
        </Link>
        <h1>View meals</h1>
        <p className="dash-subtitle">Filter meals by date range.</p>

        <form className="meal-form meals-filter" onSubmit={handleApplyFilter}>
          {error && <p className="meal-form-error">{error}</p>}

          <label>
            Start date
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              required
            />
          </label>

          <label>
            End date
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              required
            />
          </label>

          <button
            type="submit"
            className="dash-action-btn dash-action-btn--primary"
            disabled={loading}
          >
            {loading ? 'Loading…' : 'Apply filter'}
          </button>
        </form>

        {meals !== null && (
          <section className="meals-results">
            <h2>Results</h2>
            {meals.length === 0 ? (
              <p className="dash-placeholder">No meals in this range.</p>
            ) : (
              <ul className="meals-list">
                {meals.map((meal) => (
                  <li key={meal.id} className="meal-card">
                    <div className="meal-card-header">
                      <h3>{meal.mealName}</h3>
                      <span className="meal-card-date">{formatDate(meal.date)}</span>
                    </div>
                    <dl className="meal-card-macros">
                      <div>
                        <dt>Protein</dt>
                        <dd>{meal.protein} g</dd>
                      </div>
                      <div>
                        <dt>Carbs</dt>
                        <dd>{meal.carbs} g</dd>
                      </div>
                      <div>
                        <dt>Fat</dt>
                        <dd>{meal.fat} g</dd>
                      </div>
                      <div>
                        <dt>Calories</dt>
                        <dd>{meal.calories}</dd>
                      </div>
                    </dl>
                  </li>
                ))}
              </ul>
            )}
          </section>
        )}
      </main>
    </div>
  )
}
