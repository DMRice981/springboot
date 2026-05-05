import request from './index'

export const getCartList = () => request.get('/cart/list')

export const addCart = (data) => request.post('/cart/add', data)

export const updateCart = (data) => request.put('/cart/update', data)

export const deleteCart = (id) => request.delete(`/cart/delete/${id}`)
