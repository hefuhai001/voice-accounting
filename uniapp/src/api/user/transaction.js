import request from '@/utils/request'
export const transactionApi = {
  getPage(params) { return request.get('/api/transaction/page', { params }) },
  getById(id) { return request.get(`/api/transaction/${id}`) },
  save(data) { return request.post('/api/transaction', data) },
  voiceRecord(data) { return request.post('/api/transaction/voice', data) },
  update(id, data) { return request.put(`/api/transaction/${id}`, data) },
  delete(id) { return request.delete(`/api/transaction/${id}`) },
}
