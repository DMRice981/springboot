<template>
  <div id="app">
    <nav class="navbar">
      <div class="container">
        <div class="nav-brand" @click="$router.push('/')">🏪 商城</div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/cart" class="nav-link">
            🛒 购物车
            <span v-if="cartStore.cartTotal > 0" class="badge">{{ cartStore.cartTotal }}</span>
          </router-link>
          <router-link to="/orders" class="nav-link">订单</router-link>
          <router-link to="/admin" class="nav-link">管理</router-link>
        </div>
      </div>
    </nav>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useCartStore } from './stores/cart'
import { onMounted } from 'vue'

const cartStore = useCartStore()

onMounted(() => {
  cartStore.fetchCart()
})
</script>

<style scoped>
.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand {
  font-size: 1.5rem;
  font-weight: bold;
  cursor: pointer;
}

.nav-links {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-link:hover {
  opacity: 0.8;
}

.nav-link.router-link-active {
  text-decoration: underline;
}

.badge {
  background: #ff4757;
  color: white;
  border-radius: 50%;
  padding: 0.2rem 0.5rem;
  font-size: 0.8rem;
  min-width: 1.5rem;
  text-align: center;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  min-height: calc(100vh - 80px);
}
</style>
