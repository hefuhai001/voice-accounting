import request from '@/utils/request.js'

/**
 * 用户端-提醒管理API
 */
export const reminderApi = {
  /**
   * 分页查询我的提醒列表
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {number} params.userId - 用户ID（必填）
   * @param {number} [params.status] - 状态：0-待提醒 1-已提醒 2-已关闭
   */
  getPage(params) {
    return request.get('/api/reminder/page', { params })
  },

  /**
   * 根据ID查询提醒详情
   * @param {number} id - 提醒ID
   */
  getById(id) {
    return request.get(`/api/reminder/${id}`)
  },

  /**
   * 新增提醒
   * @param {Object} data - 提醒数据
   * @param {number} data.userId - 用户ID
   * @param {string} data.title - 提醒标题
   * @param {number} data.amount - 提醒金额
   * @param {string} data.remindDate - 首次提醒日期（yyyy-MM-dd）
   * @param {number} data.frequency - 重复频率：1-一次性 2-每天 3-每周 4-每月 5-每年
   * @param {string} [data.remark] - 备注
   */
  save(data) {
    return request.post('/api/reminder', data)
  },

  /**
   * 修改提醒
   * @param {number} id - 提醒ID
   * @param {Object} data - 提醒数据
   */
  update(id, data) {
    return request.put(`/api/reminder/${id}`, data)
  },

  /**
   * 标记提醒已读
   * @param {number} id - 提醒ID
   */
  markRead(id) {
    return request.put(`/api/reminder/${id}/read`)
  },

  /**
   * 关闭提醒
   * @param {number} id - 提醒ID
   */
  close(id) {
    return request.put(`/api/reminder/${id}/close`)
  },

  /**
   * 删除提醒
   * @param {number} id - 提醒ID
   */
  delete(id) {
    return request.delete(`/api/reminder/${id}`)
  },
}
