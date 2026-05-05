<template>
  <div class="orders">
    <h1>📦 我的订单</h1>
    
    <div v-if="orders.length === 0" class="empty-orders">
      <p>暂无订单，快去购物吧！</p>
      <button class="btn-primary" @click="$router.push('/')">去购物</button>
    </div>
    
    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card card">
        <div class="order-header">
          <span class="order-id">订单 #{{ order.id }}</span>
          <span class="order-date">{{ formatDate(order.createTime) }}</span>
        </div>
        <div class="order-status">
          <span class="status-badge">待发货</span>
        </div>
        <div class="order-footer">
          <button class="btn-danger" @click="deleteOrderHandler(order.id)">取消订单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList, deleteOrder } from '../api/order'

const orders = ref([])

onMounted(async () => {
  try {
    orders.value = await getOrderList()
  } catch (error) {
    console.error('加载订单失败:', error)
  }
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const deleteOrderHandler = async (id) => {
  if (confirm('确定要取消这个订单吗？')) {
    try {
      await deleteOrder(id)
      orders.value = orders.value.filter(o => o.id !== id)
      alert('订单已取消')
    } catch (error) {
      console.error('取消订单失败:', error)
    }
  }
}
</script>

<style scoped>
.orders {
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

.empty-orders {
  text-align: center;
  padding: 4rem 2rem;
  color: #888;
}

.empty-orders p {
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.orders-list {
  display: grid;
  gap: 1rem;
}

.order-card {
  transition: transform 0.2s;
}

.order-card:hover {
  transform: translateX(4px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.order-id {
  font-weight: 600;
  font-size: 1.1rem;
  color: #333;
}

.order-date {
  color: #888;
}

.order-status {
  margin-bottom: 1rem;
}

.status-badge {
  display: inline-block;
  padding: 0.4rem 1rem;
  background: #fff3cd;
  color: #856404;
  border-radius: 20px;
  font-size: 0.9rem;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}
</style>
