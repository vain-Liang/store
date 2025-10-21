import request from './request' // 引入新的实例

/**
 * 分页获取商品列表
 * @param {object} params - ...
 */
export const getProducts = (params) => {
  return request.get('/api/products', { params })
}

/**
 * 获取单个商品详情
 * @param {number} id - 商品ID
 */
export const getProductById = (id) => {
  return request.get(`/api/products/${id}`)
}
