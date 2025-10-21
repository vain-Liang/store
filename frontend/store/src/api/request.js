import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000, // 请求超时时间
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 假设后端成功code为0
    if (response.data.code !== 0 && response.data.message) {
      ElMessage.error(response.data.message)
      return Promise.reject(new Error(response.data.message))
    }
    return response
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401: // Token过期或无效
          const userStore = useUserStore()
          userStore.logout()
          router.replace({
            path: '/login',
            query: { redirect: router.currentRoute.value.fullPath },
          })
          ElMessage.warning('登录已过期，请重新登录')
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        // 其他错误处理
        default:
          ElMessage.error(error.response.data.message || '服务器发生错误')
      }
    }
    return Promise.reject(error)
  },
)

export default request
