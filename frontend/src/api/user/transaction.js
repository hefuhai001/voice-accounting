import request from '@/utils/request.js'

/**
 * 用户端-记账记录API
 */
export const transactionApi = {
  /**
   * 分页查询记账记录
   * @param {Object} params - 查询参数
   * @param {number} params.current - 当前页码，默认1
   * @param {number} params.size - 每页大小，默认10
   * @param {string} params.userId - 用户ID（必填，字符串类型的雪花ID）
   * @param {string} [params.bookId] - 账本ID（字符串类型的雪花ID）
   * @param {string} [params.categoryId] - 分类ID（字符串类型的雪花ID）
   * @param {number} [params.type] - 类型：1-支出 2-收入
   * @param {string} [params.startDate] - 开始日期（yyyy-MM-dd）
   * @param {string} [params.endDate] - 结束日期（yyyy-MM-dd）
   */
  getPage(params) {
    return request.get('/api/transaction/page', { params })
  },

  /**
   * 根据ID查询记账记录详情
   * @param {string} id - 记录ID（字符串类型的雪花ID）
   */
  getById(id) {
    return request.get(`/api/transaction/${id}`)
  },

  /**
   * 新增记账记录
   * @param {Object} data - 记账数据
   * @param {string} data.bookId - 账本ID（字符串类型的雪花ID）
   * @param {string} data.categoryId - 分类ID（字符串类型的雪花ID）
   * @param {number} data.amount - 金额
   * @param {number} data.type - 类型：1-支出 2-收入
   * @param {string} [data.remark] - 备注
   * @param {string} [data.voiceText] - 语音识别原文
   * @param {string} data.transactionDate - 交易日期（yyyy-MM-dd）
   * @param {string} [data.imageUrl] - 凭证图片地址
   */
  save(data) {
    return request.post('/api/transaction', data)
  },

  /**
   * 语音记账（传入语音识别文本）
   * @param {Object} data - 记账数据（包含voiceText字段）
   */
  voiceRecord(data) {
    return request.post('/api/transaction/voice', data)
  },

  /**
   * 修改记账记录
   * @param {string} id - 记录ID（字符串类型的雪花ID）
   * @param {Object} data - 记账数据
   */
  update(id, data) {
    return request.put(`/api/transaction/${id}`, data)
  },

  /**
   * 删除记账记录
   * @param {string} id - 记录ID（字符串类型的雪花ID）
   */
  delete(id) {
    return request.delete(`/api/transaction/${id}`)
  },
}
