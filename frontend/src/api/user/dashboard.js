import request from '@/utils/request.js'

/**
 * 用户端-首页仪表盘API
 */
export const dashboardApi = {
  /**
   * 获取首页统计数据
   * @param {string} userId - 用户ID（字符串类型的雪花ID）
   * @returns {Object} { monthExpense, monthIncome, recordCount, pendingReminders }
   */
  getStats(userId) {
    return request.get('/api/dashboard/stats', { params: { userId } })
  },

  /**
   * 获取最近记账记录
   * @param {string} userId - 用户ID（字符串类型的雪花ID）
   * @param {number} [limit=5] - 返回条数
   */
  getRecent(userId, limit = 5) {
    return request.get('/api/dashboard/recent', { params: { userId, limit } })
  },
}
