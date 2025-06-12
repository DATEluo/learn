import request from '@/api/request'

export default {
  // 获取借阅记录
  getRecords(params) {
    return request({
      url: '/records',
      method: 'get',
      params
    })
  }
}