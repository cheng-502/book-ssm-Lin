<template>
  <div class="auth-page">
    <div class="auth-box">
      <h1 class="auth-title">系统登录</h1>
      <el-form :model="model" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="model.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="model.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" style="width:100%" @click="login">登录</el-button>
        <el-button link style="width:100%;margin-top:10px" @click="$router.push('/register')">注册账号</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request, { form } from '../utils/request'
import { saveUser } from '../utils/auth'

const router = useRouter()
const model = reactive({ username: 'admin', password: '123456' })

async function login() {
  const user = await request.post('/api/auth/login', form(model))
  saveUser(user)
  ElMessage.success('登录成功')
  router.push(user.role === 'ADMIN' ? '/admin' : '/books')
}
</script>
