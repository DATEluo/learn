import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 5000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 如果存在token，则在请求头中添加token
    if (store.state.token) {
      config.headers['Authorization'] = `Bearer ${store.state.token}`
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    if (response.status >= 200 && response.status < 300) {
      // 如果响应已经是分页格式，直接返回
      if (response.data && response.data.data && Array.isArray(response.data.data)) {
        return {
          data: response.data.data,
          total: response.data.total
        }
      }
      // 如果是普通数组，转换为分页格式
      if (Array.isArray(response.data)) {
        return {
          data: response.data,
          total: response.data.length
        }
      }
      return response.data
    } else {
      const errorMsg = response.data?.message || 'Error'
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
  },
  error => {
    console.log('err' + error)
    ElMessage({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service