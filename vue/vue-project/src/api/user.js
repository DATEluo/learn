import request from '@/api/request'

export default {
  // 用户登录
  login(data) {
    return request({
      url: '/users/login',
      method: 'post',
      data
    })
  },
  
  // 用户登出
  logout() {
    return request({
      url: '/users/logout',
      method: 'post'
    })
  },
  
  // 获取当前用户信息
  getUserInfo() {
    return request({
      url: '/users/me',
      method: 'get'
    })
  }
}