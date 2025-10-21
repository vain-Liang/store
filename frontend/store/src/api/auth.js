import axios from '@/api/request' // 注意：这里我们使用即将创建的带拦截器的实例

/**
 * 用户登录
 * @param {object} data - { username, password }
 */
export const login = (data) => {
  return axios.post('/api/auth/login', data)
}

/**
 * 用户注册
 * @param {object} data - { username, password, email, phone... }
 */
export const register = (data) => {
  return axios.post('/api/auth/register', data)
}

// 刷新token的接口
export const refreshToken = (data) => {
  return axios.post('/api/auth/refresh', data)
}
