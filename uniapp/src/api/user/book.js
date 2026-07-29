import request from '@/utils/request'
export const bookApi = {
  getPage(params) { return request.get('/api/book/page', { params }) },
  getDefault(userId) { return request.get('/api/book/default', { params: { userId } }) },
  getById(id) { return request.get(`/api/book/${id}`) },
  save(data) { return request.post('/api/book', data) },
  update(id, data) { return request.put(`/api/book/${id}`, data) },
  delete(id) { return request.delete(`/api/book/${id}`) },
}
