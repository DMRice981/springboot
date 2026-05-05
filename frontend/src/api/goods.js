import request from './index'

export const getGoodsList = () => request.get('/api/goods/list')

export const addGoods = (data) => request.post('/api/goods/add', data)

export const updateGoods = (data) => request.put('/api/goods/update', data)

export const deleteGoods = (id) => request.delete(`/api/goods/delete/${id}`)

export const updateGoodsStatus = (id, status) => request.post('/api/goods/status', null, {
  params: { id, status }
})
