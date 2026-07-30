import request from '@/utils/request'
export const reminderApi = {
  getPage(params) { return request.get('/api/reminder/page', { params }) },
  getById(id) { return request.get(`/api/reminder/${id}`) },
  save(data) { return request.post('/api/reminder', data) },
  update(id, data) { return request.put(`/api/reminder/${id}`, data) },
  markRead(id) { return request.put(`/api/reminder/${id}/read`) },
  close(id) { return request.put(`/api/reminder/${id}/close`) },
  reopen(id) { return request.put(`/api/reminder/${id}/reopen`) },
  delete(id) { return request.delete(`/api/reminder/${id}`) },
}
