import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartList, addCart, updateCart, deleteCart } from '../api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([])

  const cartTotal = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + (item.num || 1), 0)
  })

  async function fetchCart() {
    try {
      cartItems.value = await getCartList()
    } catch (error) {
      console.error('获取购物车失败:', error)
    }
  }

  async function addToCart(item) {
    try {
      const newItem = {
        userId: 1,
        goodsId: item.id,
        num: 1,
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      }
      await addCart(newItem)
      await fetchCart()
    } catch (error) {
      console.error('添加购物车失败:', error)
    }
  }

  async function updateCartItem(item) {
    try {
      await updateCart(item)
      await fetchCart()
    } catch (error) {
      console.error('更新购物车失败:', error)
    }
  }

  async function removeFromCart(id) {
    try {
      await deleteCart(id)
      await fetchCart()
    } catch (error) {
      console.error('删除购物车项失败:', error)
    }
  }

  return { cartItems, cartTotal, fetchCart, addToCart, updateCartItem, removeFromCart }
})
