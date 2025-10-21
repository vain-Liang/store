<template>
  <div class="app-container" :style="backgroundStyle">
    <AppHeader />
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import widenBg from '@/asserts/img/wide.webp'
import narrowBg from '@/asserts/img/narrow.webp'

const isWideScreen = ref(window.innerWidth > 768)

const updateScreenSize = () => {
  isWideScreen.value = window.innerWidth > 768
}

onMounted(() => {
  window.addEventListener('resize', updateScreenSize)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateScreenSize)
})

const backgroundStyle = computed(() => ({
  backgroundImage: `url(${isWideScreen.value ? widenBg : narrowBg})`,
}))
</script>

<style>
/* 全局样式 */
html,
body,
#app {
  height: 100%;
  margin: 0;
  padding: 0;
}

/* 响应式背景容器 */
.app-container {
  min-height: 100%;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
