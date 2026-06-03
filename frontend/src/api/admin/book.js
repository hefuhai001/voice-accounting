import request from '@/utils/request'

/**
 * 管理端-账本管理API
 */
export const adminBookApi = {
  /**
   * 分页查询所有账本列表
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {number} [params.userId] - 用户ID（可选）
   * @param {string} [params.name] - 账本名称（模糊搜索）
   * @param {number} [params.type] - 账本类型：1-日常 2-旅行 3-共享
   */
  getPage(params) {
    return request.get('/admin/book/page', { params })
  },

  /**
   * 根据ID查询账本详情
   * @param {number} id - 账本ID
   */
  getById(id) {
    return request.get(`/admin/book/${id}`)
  },

  /**
   * 修改账本信息
   * @param {number} id - 账本ID
   * @param {Object} data - 账本数据
   */
  update(id, data) {
    return request.put(`/admin/book/${id}`, data)
  },

  /**
   * 删除账本
   * @param {number} id - 账本ID
   */
  delete(id) {
    return request.delete(`/admin/book/${id}`)
  },
}
