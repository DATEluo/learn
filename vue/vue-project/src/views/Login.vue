<template>
    <div class="login-container">
      <div class="login-box">
        <h2 class="title">云借阅-图书管理系统</h2>
        <div class="login-form">
          <el-form :model="form" label-width="80px">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入企业邮箱" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLogin">登录</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue'
  import { ElMessage } from 'element-plus'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  
  export default {
    setup() {
      const form = ref({ email: '', password: '' })
      const store = useStore()
      const router = useRouter()
  
      const handleLogin = async () => {
        try {
          await store.dispatch('login', form.value)
          router.push('/')
        } catch (error) {
          ElMessage.error('登录失败: ' + error.message)
        }
      }
  
      return { form, handleLogin }
    }
  }
  </script>
  
  <style scoped>
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  }
  
  .login-box {
    width: 450px;
    padding: 40px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  }
  
  .title {
    text-align: center;
    color: #2c3e50;
    margin-bottom: 30px;
    font-weight: 500;
  }
  
  .login-form {
    padding: 0 20px;
  }
  </style>