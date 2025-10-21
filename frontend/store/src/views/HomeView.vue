<template>
  <el-container>
    <el-main class="home-main">
      <!-- 搜索区 -->
      <el-row justify="center" class="search-bar">
        <el-col :xs="24" :sm="20" :md="16" :lg="12">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-col>
      </el-row>

      <!-- 商品列表展示区 -->
      <div v-loading="loading" class="product-list-container">
        <el-row v-if="productList.length > 0" :gutter="20">
          <el-col
            v-for="product in productList"
            :key="product.id"
            :xs="24" :sm="12" :md="8" :lg="6"
          >
            <ProductCard :product="product" />
          </el-col>
        </el-row>
        <el-empty v-else description="暂无商品" />
      </div>

      <!-- 分页 -->
      <el-row v-if="total > 0" justify="center" class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next, jumper, ->, total"
          :total="total"
          :current-page="pagination.pageNum"
          :page-size="pagination.pageSize"
          @current-change="handlePageChange"
        />
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { getProducts } from '@/api/product.js';
import ProductCard from '@/components/ProductCard.vue';
import 'element-plus/theme-chalk/el-loading.css';
import 'element-plus/theme-chalk/el-message.css';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

// 加载状态
const loading = ref(false);

// 商品列表数据
const productList = ref([]);

// 搜索关键字
const searchKeyword = ref('');
const activeKeyword = ref(''); // 当前生效的搜索词

// 分页数据
const pagination = reactive({
  pageNum: 1,
  pageSize: 12, // 每页显示12个商品
});
const total = ref(0);

// 获取商品数据的方法
const fetchProducts = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: activeKeyword.value || undefined, // 如果关键字为空，则不传递该参数
    };
    const response = await getProducts(params);
    // 后端返回的数据结构通常是 { code: 0, message: 'ok', data: { records: [...], total: 100 } }
    // 请根据你的后端实际返回结构调整
    const resData = response.data.data;
    productList.value = resData.records;
    total.value = parseInt(resData.total, 10);
  } catch (error) {
    console.error('获取商品列表失败:', error);
    ElMessage.error('加载商品失败，请稍后再试');
    productList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 处理页码改变
const handlePageChange = (currentPage) => {
  pagination.pageNum = currentPage;
  fetchProducts();
};

// 处理搜索
const handleSearch = () => {
  pagination.pageNum = 1; // 搜索时回到第一页
  activeKeyword.value = searchKeyword.value;
  fetchProducts();
};

// 组件挂载时首次加载数据
onMounted(() => {
  fetchProducts();
});
</script>

<style scoped>
.home-main {
  padding: 20px;
}

.search-bar {
  margin-bottom: 30px;
}

.product-list-container {
  min-height: 300px; /* 防止加载时页面抖动 */
}

.el-col {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
}

/* 在小屏幕上让分页组件换行显示，避免溢出 */
@media (max-width: 768px) {
  .el-pagination {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>