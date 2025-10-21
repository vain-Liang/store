<template>
  <el-header class="app-header">
    <el-row justify="space-between" align="middle" style="width: 100%">
      <!-- Logo and Title -->
      <el-col :span="8">
        <div class="logo-area" @click="$router.push('/')">
          <img src="@/asserts/logo.svg" alt="Logo" class="logo-img" />
          <span class="logo-title">网上购物商城</span>
        </div>
      </el-col>

      <!-- Navigation Links -->
      <el-col :span="8" class="text-center">
        <!-- 搜索框可以放在这里，也可以放在首页内容区 -->
      </el-col>

      <!-- User Actions -->
      <el-col :span="8" class="user-actions">
        <template v-if="userStore.isLoggedIn">
          <span>欢迎, {{ userStore.user?.username }}</span>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-avatar :size="30" class="user-avatar">{{
                userStore.user?.username?.charAt(0)
              }}</el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button text @click="$router.push('/login')">登录</el-button>
          <el-button text @click="$router.push('/register')">注册</el-button>
        </template>
      </el-col>
    </el-row>
  </el-header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('您确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      })
      .catch(() => {
        // 取消操作
      })
  } else if (command === 'profile') {
    // router.push('/profile'); // 跳转到个人中心页
    ElMessage.info('个人中心功能待开发')
  } else if (command === 'orders') {
    // router.push('/orders'); // 跳转到订单页
    ElMessage.info('我的订单功能待开发')
  }
}
</script>

<style scoped>
.app-header {
  --el-header-padding: 0 20px;
  --el-header-height: 60px;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo-area {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo-img {
  height: 32px;
  margin-right: 10px;
}

.logo-title {
  font-size: 20px;
  font-weight: bold;
}

.user-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 15px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-avatar {
  margin-left: 8px;
  background-color: var(--el-color-primary);
}

.text-center {
  text-align: center;
}
</style>
