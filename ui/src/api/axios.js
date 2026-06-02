import axios from 'axios'
import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  isRememberMe,
  saveTokens,
} from './authStorage'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT to every request when present.
api.interceptors.request.use((config) => {
  const token = getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refreshing = null

// On 401, try refresh once; otherwise clear auth and send user to login.
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config
    if (error.response?.status !== 401 || original._retry) {
      return Promise.reject(error)
    }

    const refreshToken = getRefreshToken()
    if (!refreshToken) {
      clearTokens()
      window.location.href = '/login'
      return Promise.reject(error)
    }

    original._retry = true

    if (!refreshing) {
      refreshing = axios
        .post('/api/auth/refresh', { refreshToken })
        .then((res) => {
          saveTokens(res.data, isRememberMe())
          return res.data.token
        })
        .catch(() => {
          clearTokens()
          window.location.href = '/login'
          throw error
        })
        .finally(() => {
          refreshing = null
        })
    }

    const newToken = await refreshing
    original.headers.Authorization = `Bearer ${newToken}`
    return api(original)
  },
)

export default api
