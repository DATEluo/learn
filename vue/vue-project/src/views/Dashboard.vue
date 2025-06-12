<template>
    <div class="dashboard-container">
      <!-- 顶部导航栏 -->
      <header class="header">
        <div class="logo">云借阅图书管理系统</div>
        <div class="user-info">
          <el-avatar :size="36" :src="userAvatar" />
          <span class="username">{{ user.username }}</span>
          <el-button type="text" @click="handleLogout">退出</el-button>
        </div>
      </header>
      
      <div class="main-container">
        <!-- 侧边栏 -->
        <aside class="sidebar">
          <el-menu
            router
            :default-active="$route.path"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
          >
            <el-menu-item index="/new-books">
              <i class="el-icon-notebook-2"></i>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/book-manage">
              <i class="el-icon-collection"></i>
              <span>图书查阅</span>
            </el-menu-item>
            <el-menu-item index="/current-borrow">
              <i class="el-icon-reading"></i>
              <span>当前借阅</span>
            </el-menu-item>
            <el-menu-item index="/records">
              <i class="el-icon-document"></i>
              <span>借阅记录</span>
            </el-menu-item>
          </el-menu>
        </aside>
        
        <!-- 内容区域 -->
        <main class="content">
          <router-view />
        </main>
      </div>
    </div>
  </template>
  
  <script>
  import { computed } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  
  export default {
    setup() {
      const store = useStore()
      const router = useRouter()
      
      const user = computed(() => store.state.user || { username: '用户' })
      const userAvatar = computed(() => {
        const username = user.value.username || 'User'
        return `https://api.dicebear.com/6.x/initials/svg?seed=${username}`
      })
      const handleLogout = async () => {
        try {
          // 先导航到登录页，再执行登出操作
          await router.replace('/login')
          await store.dispatch('logout')
        } catch (error) {
          console.error('退出失败:', error)
          // 如果导航失败，仍然执行登出
          await store.dispatch('logout')
        }
      }
      
      return { user, userAvatar, handleLogout }
    }
  }
  </script>
  
  <style scoped>
  .dashboard-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    height: 60px;
    background: #304156;
    color: white;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  }
  
  .logo {
    font-size: 18px;
    font-weight: 600;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .username {
    font-size: 14px;
  }
  
  .main-container {
    display: flex;
    flex: 1;
    overflow: hidden;
  }
  
  .sidebar {
    width: 210px;
    background: #304156;
    overflow-y: auto;
  }
  
  .content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    background: #f0f2f5;
  }
  </style>