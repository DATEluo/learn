import request from '@/api/request'

export default {
  // 获取新书推荐
  getNewBooks(params) {
    return request({
      url: '/books/new',
      method: 'get',
      params
    }).then(response => {
      return {
        data: response,
        total: response.length
      }
    })
  },
  
  // 搜索图书
  searchBooks(params) {
    return request({
      url: '/books',
      method: 'get',
      params
    })
  },
  
  // 借阅图书
  borrowBook(data) {
    return request({
      url: '/books/borrow',
      method: 'post',
      data
    })
  },
  
  // 新增图书
  addBook(data) {
    return request({
      url: '/books',
      method: 'post',
      data
    })
  },
  
  // 更新图书
  updateBook(data) {
    return request({
      url: `/books/${data.id}`,
      method: 'put',
      data
    })
  },
  
  // 获取当前借阅
  getCurrentBorrows(params) {
    return request({
      url: '/books/current',
      method: 'get',
      params
    })
  },
  
  // 归还图书
  returnBook(bookId, confirm) {
    return request({
      url: `/books/return/${bookId}`,
      method: 'post',
      params: { confirm }
    })
  },
  
  // 获取图书详情
  getBookDetail(id) {
    return request({
      url: `/books/${id}`,
      method: 'get'
    })
  }
}