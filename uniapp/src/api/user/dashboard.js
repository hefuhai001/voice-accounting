import request from '@/utils/request'
export const dashboardApi = {
  getStats(userId) { return request.get('/api/dashboard/stats', { params: { userId } }) },
  getRecent(userId, limit = 5) { return request.get('/api/dashboard/recent', { params: { userId, limit } }) },
}
