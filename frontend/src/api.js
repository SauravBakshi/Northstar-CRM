import axios from 'axios'
const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api' })
api.interceptors.request.use(config => { const token = localStorage.getItem('northstar_token') || sessionStorage.getItem('northstar_token'); if (token) config.headers.Authorization = `Bearer ${token}`; return config })
api.interceptors.response.use(r => r, error => { if (error.response?.status === 401) { localStorage.removeItem('northstar_token'); localStorage.removeItem('northstar_user'); sessionStorage.removeItem('northstar_token'); sessionStorage.removeItem('northstar_user'); window.dispatchEvent(new Event('northstar:logout')) }; return Promise.reject(error) })
export default api
