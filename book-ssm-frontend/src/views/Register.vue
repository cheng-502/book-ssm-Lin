<template>
  <div class="auth-page">
    <div class="auth-box">
      <h1 class="auth-title">用户注册</h1>
      <el-form :model="model" label-width="70px">
        <el-form-item label="用户名"><el-input v-model="model.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="model.password" type="password" show-password /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="model.nickname" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="model.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="model.email" /></el-form-item>
        <el-button type="primary" style="width:100%" @click="register">注册</el-button>
        <el-button link style="width:100%;margin-top:10px" @click="$router.push('/login')">返回登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request, { form } from '../utils/request'

const router = useRouter()
const model = reactive({ username: '', password: '', nickname: '', phone: '', email: '' })

async function register() {
  await request.post('/api/auth/register', form(model))
  ElMessage.success('注册成功')
  router.push('/login')
}
</script>
