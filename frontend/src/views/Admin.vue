<template>
  <div class="admin">
    <h1>⚙️ 商品管理</h1>
    
    <div class="admin-section">
      <div class="section-header">
        <h2>商品列表</h2>
        <button class="btn-primary" @click="showAddModal = true">+ 添加商品</button>
      </div>
      
      <div class="goods-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>商品名称</th>
              <th>价格</th>
              <th>库存</th>
              <th>分类</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="goods in goodsList" :key="goods.id">
              <td>{{ goods.id }}</td>
              <td>{{ goods.goodsName }}</td>
              <td>¥{{ goods.price }}</td>
              <td>{{ goods.stock }}</td>
              <td>{{ getCategoryName(goods.categoryId) }}</td>
              <td>
                <span :class="['status-tag', goods.status === 1 ? 'active' : 'inactive']">
                  {{ goods.status === 1 ? '上架' : '下架' }}
                </span>
              </td>
              <td class="actions">
                <button class="btn-secondary btn-sm" @click="editGoods(goods)">编辑</button>
                <button class="btn-secondary btn-sm" @click="toggleStatus(goods)">
                  {{ goods.status === 1 ? '下架' : '上架' }}
                </button>
                <button class="btn-danger btn-sm" @click="deleteGoodsItem(goods.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>{{ showEditModal ? '编辑商品' : '添加商品' }}</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>商品名称</label>
            <input v-model="formData.goodsName" required />
          </div>
          <div class="form-group">
            <label>价格</label>
            <input type="number" v-model.number="formData.price" step="0.01" required />
          </div>
          <div class="form-group">
            <label>库存</label>
            <input type="number" v-model.number="formData.stock" required />
          </div>
          <div class="form-group">
            <label>分类</label>
            <select v-model="formData.categoryId" required>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getGoodsList, addGoods, updateGoods, deleteGoods, updateGoodsStatus } from '../api/goods'
import { getCategoryList } from '../api/category'

const goodsList = ref([])
const categories = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const formData = ref({
  id: null,
  goodsName: '',
  price: 0,
  stock: 0,
  categoryId: null
})

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  try {
    goodsList.value = await getGoodsList()
    categories.value = await getCategoryList()
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const getCategoryName = (id) => {
  const cat = categories.value.find(c => c.id === id)
  return cat ? cat.name : '-'
}

const editGoods = (goods) => {
  formData.value = {
    id: goods.id,
    goodsName: goods.goodsName,
    price: goods.price,
    stock: goods.stock,
    categoryId: goods.categoryId
  }
  showEditModal.value = true
}

const toggleStatus = async (goods) => {
  try {
    await updateGoodsStatus(goods.id, goods.status === 1 ? 0 : 1)
    await loadData()
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

const deleteGoodsItem = async (id) => {
  if (confirm('确定要删除这个商品吗？')) {
    try {
      await deleteGoods(id)
      await loadData()
    } catch (error) {
      console.error('删除商品失败:', error)
    }
  }
}

const submitForm = async () => {
  try {
    if (showEditModal.value) {
      await updateGoods(formData.value)
    } else {
      await addGoods(formData.value)
    }
    await loadData()
    closeModal()
    alert('保存成功！')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  formData.value = { id: null, goodsName: '', price: 0, stock: 0, categoryId: null }
}
</script>

<style scoped>
.admin {
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

.admin-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  color: #333;
}

.goods-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f8f9fa;
  font-weight: 600;
  color: #555;
}

tr:hover {
  background: #f8f9fa;
}

.status-tag {
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.85rem;
}

.status-tag.active {
  background: #d4edda;
  color: #155724;
}

.status-tag.inactive {
  background: #f8d7da;
  color: #721c24;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-sm {
  padding: 0.4rem 0.8rem;
  font-size: 0.9rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.2s;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  width: 100%;
  max-width: 500px;
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 1.2rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}
</style>
