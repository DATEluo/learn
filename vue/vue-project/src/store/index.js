import { createStore } from 'vuex'
import userApi from '@/api/user'

export default createStore({
  state: {
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    token: localStorage.getItem('token') || null
  },
  mutations: {
    setUser(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    setToken(state, token) {
      state.token = token
    }
  },
  actions: {
    async login({ commit }, credentials) {
      try {
        const response = await userApi.login(credentials)
        const { user, token } = response

        // 数据验证
        if (!user || !token) {
          throw new Error('服务器返回无效的用户数据')
        }
        
        commit('setUser', user)
        commit('setToken', token)
        
        return user
      } catch (error) {
        const errorMsg = error.response?.data?.message || error.message
        throw new Error(`登录失败: ${errorMsg}`)
      }
    },
    async logout({ commit }) {
      try {
        // 先清除本地状态，避免组件访问已清除的数据
        commit('setUser', null)
        commit('setToken', null)
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        
        // 然后调用 API 登出
        await userApi.logout()
      } catch (error) {
        console.error('登出API调用失败:', error)
      }
    },
    async initializeStore({ commit, state }) {
      if (state.token && !state.user) {
        try {
          const userInfo = await userApi.getUserInfo()
          commit('setUser', userInfo.data)
        } catch (error) {
          console.error('获取用户信息失败', error)
          commit('setToken', null)
          localStorage.removeItem('token')
        }
      }
    },
    async fetchUserInfo({ commit }) {
      try {
        const userInfo = await userApi.getUserInfo()
        commit('setUser', userInfo)
      } catch (error) {
        console.error('获取用户信息失败', error)
        // 清除无效 token
        commit('setToken', null)
        localStorage.removeItem('token')
      }
    }
  },
  getters: {
    isAuthenticated: state => !!state.token,
    isAdmin: state => state.user?.role === 'ADMIN'
  }
})