import request from '@/utils/request'

/**
 * 管理端-记账记录管理API
 */
export const adminTransactionApi = {
  /**
   * 分页查询所有记账记录
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {number} [params.bookId] - 账本ID
   * @param {number} [params.categoryId] - 分类ID
   * @param {number} [params.type] - 类型：1-支出 2-收入
   * @param {string} [params.startDate] - 开始日期（yyyy-MM-dd）
   * @param {string} [params.endDate] - 结束日期（yyyy-MM-dd）
   */
  getPage(params) {
    return request.get('/admin/transaction/page', { params })
  },

  /**
   * 根据ID查询记账记录详情
   * @param {number} id - 记录ID
   */
  getById(id) {
    return request.get(`/admin/transaction/${id}`)
  },

  /**
   * 修改记账记录
   * @param {number} id - 记录ID
   * @param {Object} data - 记账数据
   */
  update(id, data) {
    return request.put(`/admin/transaction/${id}`, data)
  },

  /**
   * 删除记账记录
   * @param {number} id - 记录ID
   */
  delete(id) {
    return request.delete(`/admin/transaction/${id}`)
  },
}
