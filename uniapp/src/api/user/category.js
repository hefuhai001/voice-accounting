import request from '@/utils/request'
export const categoryApi = {
  getExpenseList(userId) { return request.get('/api/category/expense', { params: userId ? { userId } : {} }) },
  getIncomeList(userId) { return request.get('/api/category/income', { params: userId ? { userId } : {} }) },
  getList(userId) { return request.get('/api/category/list', { params: userId ? { userId } : {} }) },
  getById(id) { return request.get(`/api/category/${id}`) },
  save(data) { return request.post('/api/category', data) },
  update(id, data) { return request.put(`/api/category/${id}`, data) },
  delete(id) { return request.delete(`/api/category/${id}`) },
}
