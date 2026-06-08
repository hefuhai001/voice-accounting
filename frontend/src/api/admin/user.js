import request from '@/utils/request'

/**
 * 管理端-用户管理API
 */
export const adminUserApi = {
  /**
   * 分页查询用户列表
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {string} [params.username] - 用户名（模糊搜索）
   * @param {string} [params.email] - 邮箱（模糊搜索）
   * @param {number} [params.status] - 状态：0-禁用 1-正常
   * @param {number} [params.role] - 角色：0-普通用户 1-管理员
   */
  getPage(params) {
    return request.get('/admin/user/page', { params })
  },

  /**
   * 根据ID查询用户详情
   * @param {string} id - 用户ID（字符串类型的雪花ID）
   */
  getById(id) {
    return request.get(`/admin/user/${id}`)
  },

  /**
   * 新增用户
   * @param {Object} data - 用户数据
   */
  save(data) {
    return request.post('/admin/user', data)
  },

  /**
   * 修改用户信息
   * @param {string} id - 用户ID（字符串类型的雪花ID）
   * @param {Object} data - 用户数据
   */
  update(id, data) {
    return request.put(`/admin/user/${id}`, data)
  },

  /**
   * 分配角色
   * @param {string} id - 用户ID（字符串类型的雪花ID）
   * @param {number} role - 角色：0-普通用户 1-管理员
   */
  assignRole(id, role) {
    return request.put(`/admin/user/${id}/role`, null, { params: { role } })
  },

  /**
   * 禁用/启用用户
   * @param {string} id - 用户ID（字符串类型的雪花ID）
   * @param {number} status - 状态：0-禁用 1-正常
   */
  updateStatus(id, status) {
    return request.put(`/admin/user/${id}/status`, null, { params: { status } })
  },

  /**
   * 删除用户
   * @param {string} id - 用户ID（字符串类型的雪花ID）
   */
  delete(id) {
    return request.delete(`/admin/user/${id}`)
  },
}
