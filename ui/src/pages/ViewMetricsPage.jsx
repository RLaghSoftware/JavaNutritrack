import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import { downloadBlob } from '../api/download'
import DashboardHeader from '../components/DashboardHeader'
import { formatDate, formatNumber, todayIso, weekAgoIso } from '../utils/dates'

export default function ViewMetricsPage() {
  const [startDate, setStartDate] = useState(weekAgoIso())
  const [endDate, setEndDate] = useState(todayIso())
  const [report, setReport] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [downloading, setDownloading] = useState(false)

  const handleGenerateReport = async (e) => {
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
      const { data } = await api.get('/metrics/report', {
        params: { startDate, endDate },
      })
      setReport(data)
    } catch (err) {
      const msg =
        err.response?.data?.message || 'Could not generate report. Try again.'
      setError(msg)
      setReport(null)
    } finally {
      setLoading(false)
    }
  }

  const handleDownload = async () => {
    if (!report) return

    setDownloading(true)
    setError('')
    try {
      const response = await api.get('/metrics/report/download', {
        params: { startDate: report.startDate, endDate: report.endDate },
        responseType: 'blob',
      })

      const filename = `nutritrack-metrics-${report.startDate}-to-${report.endDate}.xlsx`
      downloadBlob(response.data, filename)
    } catch {
      setError('Could not download report. Try again.')
    } finally {
      setDownloading(false)
    }
  }

  return (
    <div className="dash-layout">
      <DashboardHeader />
      <main className="dash-main">
        <Link to="/dashboard" className="dash-back-link">
          ← Back to dashboard
        </Link>
        <h1>View metrics</h1>
        <p className="dash-subtitle">
          Generate a daily nutrition summary for a date range.
        </p>

        <form className="meal-form metrics-filter" onSubmit={handleGenerateReport}>
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
            {loading ? 'Generating…' : 'Generate report'}
          </button>
        </form>

        {report !== null && (
          <section className="metrics-report">
            <div className="metrics-report-header">
              <h2>
                Report: {formatDate(report.startDate)} – {formatDate(report.endDate)}
              </h2>
              <button
                type="button"
                className="dash-action-btn dash-action-btn--primary"
                onClick={handleDownload}
                disabled={downloading}
              >
                {downloading ? 'Downloading…' : 'Download Excel'}
              </button>
            </div>

            {report.rows.length === 0 ? (
              <p className="dash-placeholder">No data in this range.</p>
            ) : (
              <div className="metrics-table-wrap">
                <table className="metrics-table">
                  <thead>
                    <tr>
                      <th>Date</th>
                      <th>Protein (g)</th>
                      <th>Carbs (g)</th>
                      <th>Fat (g)</th>
                      <th>Calories</th>
                    </tr>
                  </thead>
                  <tbody>
                    {report.rows.map((row) => (
                      <tr key={row.date}>
                        <td>{formatDate(row.date)}</td>
                        <td>{formatNumber(row.protein)}</td>
                        <td>{formatNumber(row.carbs)}</td>
                        <td>{formatNumber(row.fat)}</td>
                        <td>{formatNumber(row.calories)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr>
                      <td>Total</td>
                      <td>{formatNumber(report.totals.protein)}</td>
                      <td>{formatNumber(report.totals.carbs)}</td>
                      <td>{formatNumber(report.totals.fat)}</td>
                      <td>{formatNumber(report.totals.calories)}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            )}
          </section>
        )}
      </main>
    </div>
  )
}
