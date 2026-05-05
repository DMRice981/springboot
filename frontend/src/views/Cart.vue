<template>
  <div class="cart">
    <h1>🛒 购物车</h1>
    
    <div v-if="cartStore.cartItems.length === 0" class="empty-cart">
      <p>购物车是空的，快去选购商品吧！</p>
      <button class="btn-primary" @click="$router.push('/')">去购物</button>
    </div>
    
    <div v-else>
      <div class="cart-list">
        <div v-for="item in cartStore.cartItems" :key="item.id" class="cart-item card">
          <div class="item-info">
            <div class="item-img">
              <img src="https://via.placeholder.com/100x100/eee/999?text=商品" alt="商品" />
            </div>
            <div class="item-details">
              <h3>商品 #{{ item.goodsId }}</h3>
              <p class="item-id">商品ID: {{ item.goodsId }}</p>
            </div>
          </div>
          <div class="item-quantity">
            <button class="btn-secondary" @click="updateQuantity(item, -1)">-</button>
            <span class="quantity">{{ item.num || 1 }}</span>
            <button class="btn-secondary" @click="updateQuantity(item, 1)">+</button>
          </div>
          <button class="btn-danger" @click="cartStore.removeFromCart(item.id)">删除</button>
        </div>
      </div>
      
      <div class="cart-footer">
        <button class="btn-primary" @click="checkout">结算</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '../stores/cart'
import { addOrder } from '../api/order'

const cartStore = useCartStore()

const updateQuantity = (item, delta) => {
  const newNum = (item.num || 1) + delta
  if (newNum < 1) return
  cartStore.updateCartItem({ ...item, num: newNum })
}

const checkout = async () => {
  if (confirm('确定要结算吗？')) {
    try {
      const order = {
        userId: 1,
        createTime: new Date().toISOString()
      }
      await addOrder(order)
      for (const item of cartStore.cartItems) {
        await cartStore.removeFromCart(item.id)
      }
      alert('订单创建成功！')
    } catch (error) {
      console.error('结算失败:', error)
      alert('结算失败，请重试')
    }
  }
}
</script>

<style scoped>
.cart {
  animation: fadeIn 0.5s;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

h1 {
  margin-bottom: 2rem;
  color: #333;
}

.empty-cart {
  text-align: center;
  padding: 4rem 2rem;
  color: #888;
}

.empty-cart p {
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.cart-list {
  margin-bottom: 2rem;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.item-info {
  flex: 1;
  display: flex;
  gap: 1rem;
  align-items: center;
}

.item-img img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
}

.item-details h3 {
  margin-bottom: 0.5rem;
  color: #333;
}

.item-id {
  color: #888;
  font-size: 0.9rem;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.quantity {
  font-size: 1.2rem;
  font-weight: 600;
  min-width: 2rem;
  text-align: center;
}

.cart-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
