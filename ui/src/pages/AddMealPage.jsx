import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import DashboardHeader from '../components/DashboardHeader'
import Toast from '../components/Toast'

const emptyForm = () => ({
  mealName: '',
  date: new Date().toISOString().split('T')[0],
  protein: '',
  carbs: '',
  fat: '',
  calories: '',
})

function formatDate(dateStr) {
  const [year, month, day] = dateStr.split('-')
  return new Date(year, month - 1, day).toLocaleDateString(undefined, {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

export default function AddMealPage() {
  const [form, setForm] = useState(emptyForm)
  const [pendingMeal, setPendingMeal] = useState(null)
  const [error, setError] = useState('')
  const [toast, setToast] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const updateField = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }))
    setError('')
  }

  const validate = () => {
    if (!form.mealName.trim()) {
      return 'Enter a meal name.'
    }
    if (!form.date) {
      return 'Select a date.'
    }
    const macros = [
      { key: 'protein', label: 'Protein' },
      { key: 'carbs', label: 'Carbs' },
      { key: 'fat', label: 'Fat' },
      { key: 'calories', label: 'Calories' },
    ]
    for (const { key, label } of macros) {
      const value = form[key]
      if (value === '' || Number.isNaN(Number(value))) {
        return `Enter a valid ${label.toLowerCase()} value.`
      }
      if (Number(value) < 0) {
        return `${label} cannot be negative.`
      }
    }
    return ''
  }

  const handleReview = (e) => {
    e.preventDefault()
    const validationError = validate()
    if (validationError) {
      setError(validationError)
      return
    }
    setPendingMeal({
      mealName: form.mealName.trim(),
      date: form.date,
      protein: Number(form.protein),
      carbs: Number(form.carbs),
      fat: Number(form.fat),
      calories: Number(form.calories),
    })
  }

  const handleConfirm = async () => {
    setSubmitting(true)
    try {
      const { data } = await api.post('/meals', pendingMeal)
      if (data.success) {
        setPendingMeal(null)
        setForm(emptyForm())
        setError('')
        setToast({ message: data.message, type: 'success' })
      } else {
        setToast({ message: data.message, type: 'error' })
      }
    } catch {
      setToast({ message: 'Could not add meal. Try again.', type: 'error' })
    } finally {
      setSubmitting(false)
    }
  }

  const handleEdit = () => {
    setPendingMeal(null)
  }

  return (
    <div className="dash-layout">
      <DashboardHeader />
      <main className="dash-main">
        <Link to="/dashboard" className="dash-back-link">
          ← Back to dashboard
        </Link>
        <h1>Add meal</h1>
        <p className="dash-subtitle">Log macros for a meal.</p>

        {!pendingMeal ? (
          <form className="meal-form" onSubmit={handleReview}>
            {error && <p className="meal-form-error">{error}</p>}

            <label>
              Meal name
              <input
                type="text"
                value={form.mealName}
                onChange={updateField('mealName')}
                placeholder="e.g. Post-workout lunch"
                required
              />
            </label>

            <label>
              Date
              <input type="date" value={form.date} onChange={updateField('date')} required />
            </label>

            <div className="meal-form-macros">
              <label>
                Protein (g)
                <input
                  type="number"
                  min="0"
                  step="0.1"
                  value={form.protein}
                  onChange={updateField('protein')}
                  required
                />
              </label>
              <label>
                Carbs (g)
                <input
                  type="number"
                  min="0"
                  step="0.1"
                  value={form.carbs}
                  onChange={updateField('carbs')}
                  required
                />
              </label>
              <label>
                Fat (g)
                <input
                  type="number"
                  min="0"
                  step="0.1"
                  value={form.fat}
                  onChange={updateField('fat')}
                  required
                />
              </label>
              <label>
                Calories
                <input
                  type="number"
                  min="0"
                  step="1"
                  value={form.calories}
                  onChange={updateField('calories')}
                  required
                />
              </label>
            </div>

            <button type="submit" className="dash-action-btn dash-action-btn--primary">
              Review meal
            </button>
          </form>
        ) : (
          <section className="meal-confirm">
            <h2>Confirm meal</h2>
            <dl className="meal-confirm-list">
              <div>
                <dt>Meal name</dt>
                <dd>{pendingMeal.mealName}</dd>
              </div>
              <div>
                <dt>Date</dt>
                <dd>{formatDate(pendingMeal.date)}</dd>
              </div>
              <div>
                <dt>Protein</dt>
                <dd>{pendingMeal.protein} g</dd>
              </div>
              <div>
                <dt>Carbs</dt>
                <dd>{pendingMeal.carbs} g</dd>
              </div>
              <div>
                <dt>Fat</dt>
                <dd>{pendingMeal.fat} g</dd>
              </div>
              <div>
                <dt>Calories</dt>
                <dd>{pendingMeal.calories}</dd>
              </div>
            </dl>
            <div className="meal-confirm-actions">
              <button type="button" className="dash-action-btn" onClick={handleEdit}>
                Edit
              </button>
              <button
                type="button"
                className="dash-action-btn dash-action-btn--primary"
                onClick={handleConfirm}
                disabled={submitting}
              >
                {submitting ? 'Saving…' : 'Confirm'}
              </button>
            </div>
          </section>
        )}
      </main>

      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  )
}
