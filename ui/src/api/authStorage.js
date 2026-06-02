const TOKEN_KEY = 'nutritrack_access_token'
const REFRESH_KEY = 'nutritrack_refresh_token'
const REMEMBER_KEY = 'nutritrack_remember_me'

function storage(persistent) {
  return persistent ? localStorage : sessionStorage
}

export function isRememberMe() {
  return localStorage.getItem(REMEMBER_KEY) === 'true'
}

export function setRememberMe(remember) {
  if (remember) {
    localStorage.setItem(REMEMBER_KEY, 'true')
  } else {
    localStorage.removeItem(REMEMBER_KEY)
  }
}

export function getAccessToken() {
  return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_KEY) || sessionStorage.getItem(REFRESH_KEY)
}

export function saveTokens({ token, refreshToken }, remember) {
  clearTokens()
  setRememberMe(remember)
  const store = storage(remember)
  store.setItem(TOKEN_KEY, token)
  if (refreshToken) {
    store.setItem(REFRESH_KEY, refreshToken)
  }
}

export function clearTokens() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(REFRESH_KEY)
}
