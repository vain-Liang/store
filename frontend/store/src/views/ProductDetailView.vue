<template>
  <el-container class="detail-container">
    <el-main>
      <div v-loading="loading" class="content-card">
        <div v-if="product">
          <el-row :gutter="30">
            <el-col :xs="24" :sm="12">
              <img
                :src="product.imageUrl || 'https://via.placeholder.com/600x450.png?text=No+Image'"
                class="detail-image"
                @error="handleImageError"
              />
            </el-col>
            <el-col :xs="24" :sm="12" class="product-details">
              <h1>{{ product.name }}</h1>
              <p class="description">{{ product.description }}</p>
              <div class="price-section">
                <span class="label">价格：</span>
                <span class="price">¥ {{ Number(product.price).toFixed(2) }}</span>
              </div>
              <div class="stock-section">
                <span class="label">库存：</span>
                <span>{{ product.stock }}</span>
              </div>
              <div class="quantity-section">
                <span class="label">数量：</span>
                <el-input-number v-model="quantity" :min="1" :max="product.stock" />
              </div>
              <div class="actions">
                <el-button type="primary" size="large" :disabled="product.stock === 0"
                  >立即购买</el-button
                >
                <el-button type="warning" size="large" :disabled="product.stock === 0"
                  >加入购物车</el-button
                >
              </div>
            </el-col>
          </el-row>
        </div>
        <el-empty v-if="!loading && !product" description="商品不存在或已下架" />
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductById } from '@/api/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(true)
const product = ref(null)
const quantity = ref(1)

onMounted(async () => {
  const productId = route.params.id
  try {
    const response = await getProductById(productId)
    // 根据API文档，数据在 response.data.data 中
    product.value = response.data.data
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error(error.response?.data?.message || '获取商品详情失败')
  } finally {
    loading.value = false
  }
})

const handleImageError = (event) => {
  event.target.src = 'https://via.placeholder.com/600x450.png?text=No+Image'
}
</script>

<style scoped>
.detail-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}
.content-card {
  max-width: 1000px;
  width: 100%;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
}
.detail-image {
  width: 100%;
  border-radius: 4px;
}
.product-details h1 {
  font-size: 24px;
  margin-top: 0;
}
.description {
  color: #606266;
  font-size: 14px;
  margin-bottom: 20px;
}
.price-section,
.stock-section,
.quantity-section {
  font-size: 16px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}
.label {
  color: #909399;
  width: 60px;
}
.price {
  color: #ff4d4f;
  font-size: 22px;
  font-weight: bold;
}
.actions {
  margin-top: 30px;
}
</style>
