<template>
  <!-- 将根元素换成 div 并添加点击事件 -->
  <div @click="goToDetail">
    <el-card shadow="hover" class="product-card">
      <img
        :src="product.imageUrl || 'https://via.placeholder.com/400x300.png?text=No+Image'"
        class="product-image"
        alt="商品图片"
        @error="handleImageError"
      />
      <div class="product-info">
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-description">{{ product.description }}</p>
        <div class="product-footer">
          <span class="product-price">¥ {{ Number(product.price).toFixed(2) }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

// 接收 props
const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
})

const router = useRouter()

// 跳转到详情页的方法
const goToDetail = () => {
  // 使用 name 和 params 进行路由跳转，更灵活
  router.push({ name: 'product-detail', params: { id: props.product.id } })
}
// 图片处理失败显示默认图片
const handleImageError = (event) => {
  event.target.src = 'https://via.placeholder.com/400x300.png?text=No+Image'
}
</script>

<style scoped>
/* 原有样式保持不变 */
.product-card {
  cursor: pointer;
  transition: all 0.3s;
}
.product-card:hover {
  transform: translateY(-5px);
}
.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}
.product-info {
  padding: 14px;
}
.product-name {
  font-size: 16px;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.product-description {
  font-size: 13px;
  color: #999;
  margin: 0 0 10px 0;
  height: 38px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}
</style>
