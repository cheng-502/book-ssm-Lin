const USER_KEY = 'book_ssm_user'

export function saveUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user || {}))
}

export function getUser() {
  const text = localStorage.getItem(USER_KEY)
  if (!text) return null
  try {
    return JSON.parse(text)
  } catch (e) {
    return null
  }
}

export function clearUser() {
  localStorage.removeItem(USER_KEY)
}
