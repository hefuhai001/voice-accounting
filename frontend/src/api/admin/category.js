import request from '@/utils/request'

/**
 * 管理端-分类管理API
 */
export const adminCategoryApi = {
  /**
   * 分页查询所有分类
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {number} [params.type] - 分类类型：1-支出 2-收入
   * @param {string} [params.name] - 分类名称（模糊搜索）
   */
  getPage(params) {
    return request.get('/admin/category/page', { params })
  },

  /**
   * 根据ID查询分类详情
   * @param {number} id - 分类ID
   */
  getById(id) {
    return request.get(`/admin/category/${id}`)
  },

  /**
   * 新增系统分类
   * @param {Object} data - 分类数据
   */
  save(data) {
    return request.post('/admin/category', data)
  },

  /**
   * 修改分类
   * @param {number} id - 分类ID
   * @param {Object} data - 分类数据
   */
  update(id, data) {
    return request.put(`/admin/category/${id}`, data)
  },

  /**
   * 删除分类
   * @param {number} id - 分类ID
   */
  delete(id) {
    return request.delete(`/admin/category/${id}`)
  },
}
