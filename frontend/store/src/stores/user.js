import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore(
  'user',
  () => {
    // state
    const token = ref('')
    const user = ref(null)

    // getters
    const isLoggedIn = computed(() => !!token.value)

    // actions
    async function login(credentials) {
      try {
        const response = await loginApi(credentials)
        const data = response.data.data // 根据你的API响应结构

        token.value = data.accessToken
        user.value = {
          id: data.id,
          username: data.username,
          email: data.email,
          phone: data.phone,
          balance: data.balance,
          roles: data.roles,
        }
        return true
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
        return false
      }
    }

    function logout() {
      token.value = ''
      user.value = null
      // 可选：在这里调用后端的登出接口
    }

    return { token, user, isLoggedIn, login, logout }
  },
  {
    persist: {
      key: 'user-store', // localStorage 中的 key
      paths: ['token', 'user'], // 持久化的状态
    },
  },
)
