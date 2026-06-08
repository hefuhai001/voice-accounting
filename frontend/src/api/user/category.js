import request from '@/utils/request.js'

/**
 * 用户端-分类查询API
 */
export const categoryApi = {
  /**
   * 获取支出分类列表（系统默认+自定义）
   * @param {string} [userId] - 用户ID（可选，字符串类型的雪花ID）
   */
  getExpenseList(userId) {
    return request.get('/api/category/expense', { params: userId ? { userId } : {} })
  },

  /**
   * 获取收入分类列表（系统默认+自定义）
   * @param {string} [userId] - 用户ID（可选，字符串类型的雪花ID）
   */
  getIncomeList(userId) {
    return request.get('/api/category/income', { params: userId ? { userId } : {} })
  },

  /**
   * 获取全部分类列表（系统默认+自定义）
   * @param {string} [userId] - 用户ID（可选，字符串类型的雪花ID）
   */
  getList(userId) {
    return request.get('/api/category/list', { params: userId ? { userId } : {} })
  },

  /**
   * 根据ID查询分类详情
   * @param {string} id - 分类ID（字符串类型的雪花ID）
   */
  getById(id) {
    return request.get(`/api/category/${id}`)
  },

  /**
   * 添加自定义分类
   * @param {Object} data - 分类数据
   * @param {string} data.name - 分类名称
   * @param {string} [data.icon] - 分类图标
   * @param {number} data.type - 分类类型：1-支出 2-收入
   * @param {string} data.userId - 用户ID（字符串类型的雪花ID）
   * @param {number} [data.sortOrder] - 排序序号
   */
  save(data) {
    return request.post('/api/category', data)
  },

  /**
   * 修改自定义分类
   * @param {string} id - 分类ID（字符串类型的雪花ID）
   * @param {Object} data - 分类数据
   */
  update(id, data) {
    return request.put(`/api/category/${id}`, data)
  },

  /**
   * 删除自定义分类
   * @param {string} id - 分类ID（字符串类型的雪花ID）
   */
  delete(id) {
    return request.delete(`/api/category/${id}`)
  },
}
