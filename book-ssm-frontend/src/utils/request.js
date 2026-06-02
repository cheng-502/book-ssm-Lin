import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8084/book_ssm_war',
  timeout: 10000,
  withCredentials: true
})

request.interceptors.response.use(
  response => {
    const body = response.data
    if (body && typeof body.code !== 'undefined') {
      if (body.code === 200) {
        return body.data
      }
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(body)
    }
    return body
  },
  error => {
    ElMessage.error(error.response?.data?.message || error.message || '网络异常')
    return Promise.reject(error)
  }
)

export function form(data) {
  const params = new URLSearchParams()
  Object.keys(data || {}).forEach(key => {
    const value = data[key]
    if (value !== undefined && value !== null && value !== '') {
      params.append(key, value)
    }
  })
  return params
}

export default request
