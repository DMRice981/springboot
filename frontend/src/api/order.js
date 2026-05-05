import request from './index'

export const getOrderList = () => request.get('/api/order/list')

export const addOrder = (data) => request.post('/api/order/add', data)

export const updateOrder = (data) => request.put('/api/order/update', data)

export const deleteOrder = (id) => request.delete(`/api/order/delete/${id}`)
