import request from '@/utils/request.js'

/**
 * 用户端-账本管理API
 */
export const bookApi = {
  /**
   * 分页查询我的账本列表
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {number} params.userId - 用户ID（必填）
   * @param {string} [params.name] - 账本名称（模糊搜索）
   * @param {number} [params.type] - 账本类型：1-日常 2-旅行 3-共享
   */
  getPage(params) {
    return request.get('/api/book/page', { params })
  },

  /**
   * 获取我的默认账本
   * @param {number} userId - 用户ID
   */
  getDefault(userId) {
    return request.get('/api/book/default', { params: { userId } })
  },

  /**
   * 根据ID查询账本详情
   * @param {number} id - 账本ID
   */
  getById(id) {
    return request.get(`/api/book/${id}`)
  },

  /**
   * 新建账本
   * @param {Object} data - 账本数据
   * @param {number} data.userId - 用户ID
   * @param {string} data.name - 账本名称
   * @param {number} [data.type] - 账本类型：1-日常 2-旅行 3-共享
   * @param {string} [data.description] - 描述
   * @param {number} [data.isDefault] - 是否默认：0-否 1-是
   * @param {string} [data.icon] - 图标
   * @param {number} [data.sortOrder] - 排序序号
   */
  save(data) {
    return request.post('/api/book', data)
  },

  /**
   * 修改账本
   * @param {number} id - 账本ID
   * @param {Object} data - 账本数据
   */
  update(id, data) {
    return request.put(`/api/book/${id}`, data)
  },

  /**
   * 删除账本
   * @param {number} id - 账本ID
   */
  delete(id) {
    return request.delete(`/api/book/${id}`)
  },
}
