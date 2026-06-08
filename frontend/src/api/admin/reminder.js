import request from '@/utils/request'

/**
 * 管理端-提醒管理API
 */
export const adminReminderApi = {
  /**
   * 分页查询所有提醒列表
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {string} [params.userId] - 用户ID（字符串类型的雪花ID）
   * @param {number} [params.status] - 状态：0-待提醒 1-已提醒 2-已关闭
   * @param {string} [params.title] - 提醒标题（模糊搜索）
   */
  getPage(params) {
    return request.get('/admin/reminder/page', { params })
  },

  /**
   * 根据ID查询提醒详情
   * @param {string} id - 提醒ID（字符串类型的雪花ID）
   */
  getById(id) {
    return request.get(`/admin/reminder/${id}`)
  },

  /**
   * 修改提醒
   * @param {string} id - 提醒ID（字符串类型的雪花ID）
   * @param {Object} data - 提醒数据
   */
  update(id, data) {
    return request.put(`/admin/reminder/${id}`, data)
  },

  /**
   * 关闭提醒
   * @param {string} id - 提醒ID（字符串类型的雪花ID）
   */
  close(id) {
    return request.put(`/admin/reminder/${id}/close`)
  },

  /**
   * 删除提醒
   * @param {string} id - 提醒ID（字符串类型的雪花ID）
   */
  delete(id) {
    return request.delete(`/admin/reminder/${id}`)
  },
}
