import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import store from '../store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/new-books' },
      { path: 'new-books', component: () => import('../views/NewBooks.vue') },
      { path: 'book-manage', component: () => import('../views/BookManage.vue') },
      { path: 'current-borrow', component: () => import('../views/CurrentBorrow.vue') },
      { path: 'records', component: () => import('../views/Records.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  // 确保在路由守卫中处理可能的异步操作
  const initialize = async () => {
    await store.dispatch('initializeStore')
    
    if (to.meta.requiresAuth) {
      if (store.getters.isAuthenticated) {
        if (!store.state.user) {
          try {
            await store.dispatch('fetchUserInfo')
            next()
          } catch (error) {
            console.error('获取用户信息失败:', error)
            next('/login')
          }
        } else {
          next()
        }
      } else {
        next('/login')
      }
    } else {
      if (to.name === 'Login' && store.getters.isAuthenticated) {
        next('/')
      } else {
        next()
      }
    }
  }
  
  initialize()
})
export default router