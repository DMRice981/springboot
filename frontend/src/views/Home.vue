<template>
  <div class="home">
    <div v-if="banners.length > 0" class="banner-section">
      <div class="banner">
        <img :src="banners[currentBanner]?.imgUrl || 'https://via.placeholder.com/1200x400/667eea/ffffff?text=商城首页'" alt="Banner" />
      </div>
    </div>

    <div class="category-section">
      <h2 class="section-title">商品分类</h2>
      <div class="category-grid">
        <div 
          v-for="category in categories" 
          :key="category.id"
          class="category-card"
          :class="{ active: selectedCategory === category.id }"
          @click="selectedCategory = selectedCategory === category.id ? null : category.id"
        >
          <div class="category-icon">📦</div>
          <div class="category-name">{{ category.name }}</div>
        </div>
      </div>
    </div>

    <div class="goods-section">
      <h2 class="section-title">热门商品</h2>
      <div class="goods-grid">
        <div v-for="goods in filteredGoods" :key="goods.id" class="goods-card">
          <div class="goods-img">
            <img :src="goods.goodsImg || 'https://via.placeholder.com/300x300/eee/999?text=商品'" :alt="goods.goodsName" />
          </div>
          <div class="goods-info">
            <h3 class="goods-name">{{ goods.goodsName }}</h3>
            <p class="goods-desc">{{ goods.goodsDesc || '暂无描述' }}</p>
            <div class="goods-price-row">
              <span class="price">¥{{ goods.price }}</span>
              <span class="sales">销量: {{ goods.sales }}</span>
            </div>
            <div class="goods-actions">
              <span class="stock">库存: {{ goods.stock }}</span>
              <button class="btn-primary" @click="addToCart(goods)">加入购物车</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getGoodsList } from '../api/goods'
import { getCategoryList } from '../api/category'
import { getBannerList } from '../api/banner'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const goods = ref([])
const categories = ref([])
const banners = ref([])
const selectedCategory = ref(null)
const currentBanner = ref(0)

const filteredGoods = computed(() => {
  if (!selectedCategory.value) return goods.value
  return goods.value.filter(g => g.categoryId === selectedCategory.value)
})

onMounted(async () => {
  try {
    goods.value = await getGoodsList()
    categories.value = await getCategoryList()
    banners.value = await getBannerList()
  } catch (error) {
    console.error('加载数据失败:', error)
  }

  setInterval(() => {
    if (banners.value.length > 1) {
      currentBanner.value = (currentBanner.value + 1) % banners.value.length
    }
  }, 5000)
})

const addToCart = (item) => {
  cartStore.addToCart(item)
  alert('已添加到购物车！')
}
</script>

<style scoped>
.home {
  animation: fadeIn 0.5s;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.banner-section {
  margin-bottom: 2rem;
}

.banner {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.banner img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.section-title {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
  position: relative;
  padding-left: 1rem;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.category-section {
  margin-bottom: 3rem;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
}

.category-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.category-card:hover, .category-card.active {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.category-card.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.category-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.category-name {
  font-weight: 500;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.goods-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: all 0.3s;
}

.goods-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.goods-img img {
  width: 100%;
  height: 280px;
  object-fit: cover;
}

.goods-info {
  padding: 1.2rem;
}

.goods-name {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.goods-desc {
  font-size: 0.9rem;
  color: #888;
  margin-bottom: 0.8rem;
}

.goods-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.8rem;
}

.price {
  font-size: 1.4rem;
  font-weight: bold;
  color: #ff4757;
}

.sales {
  font-size: 0.9rem;
  color: #888;
}

.goods-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.8rem;
  border-top: 1px solid #eee;
}

.stock {
  color: #888;
  font-size: 0.9rem;
}
</style>
