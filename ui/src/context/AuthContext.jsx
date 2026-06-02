import { createContext, useCallback, useContext, useMemo, useState } from 'react'
import api from '../api/axios'
import {
  clearTokens,
  getAccessToken,
  saveTokens,
} from '../api/authStorage'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(!!getAccessToken())

  const loadProfile = useCallback(async () => {
    const token = getAccessToken()
    if (!token) {
      setUser(null)
      setLoading(false)
      return
    }
    try {
      const { data } = await api.get('/users/me')
      setUser(data)
    } catch {
      clearTokens()
      setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  const signup = async (payload) => {
    const { data } = await api.post('/auth/signup', payload)
    saveTokens(data, payload.rememberMe)
    setUser(data.user)
    return data
  }

  const login = async (payload) => {
    const { data } = await api.post('/auth/login', {
      email: payload.email,
      password: payload.password,
    })
    saveTokens(data, payload.rememberMe)
    setUser(data.user)
    return data
  }

  const logout = async () => {
    try {
      await api.post('/auth/logout')
    } catch {
      // still clear local session
    }
    clearTokens()
    setUser(null)
  }

  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: !!user,
      loadProfile,
      signup,
      login,
      logout,
    }),
    [user, loading, loadProfile, signup, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return ctx
}
