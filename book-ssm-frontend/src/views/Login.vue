<template>
  <div class="auth-page">
    <div class="auth-box">
      <div class="auth-brand">
        <div class="auth-brand-mark">书</div>
        <div>
          <div class="auth-brand-title">图书进销存管理系统</div>
          <div class="auth-brand-subtitle">登录后进入库存、订单与图书管理工作台</div>
        </div>
      </div>

      <h1 class="auth-title">系统登录</h1>

      <el-alert
        v-if="authError"
        class="auth-alert"
        :title="authError"
        type="error"
        show-icon
        :closable="false"
      />

      <el-form
        ref="formRef"
        class="auth-form"
        :model="model"
        :rules="rules"
        label-position="top"
        @keyup.enter="login"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="model.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="model.password"
            autocomplete="current-password"
            placeholder="请输入密码"
            show-password
            type="password"
          />
        </el-form-item>
        <el-button class="auth-submit" type="primary" :loading="submitting" @click="login">登录</el-button>
        <div class="auth-switch">
          还没有账号？
          <el-button link type="primary" @click="$router.push('/register')">注册账号</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request, { form } from '../utils/request'
import { saveUser } from '../utils/auth'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const authError = ref('')
const model = reactive({ username: 'admin', password: '123456' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function login() {
  authError.value = ''
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    submitting.value = true
    const user = await request.post('/api/auth/login', form(model))
    saveUser(user)
    ElMessage.success('登录成功')
    router.push(user.role === 'ADMIN' ? '/admin' : '/books')
  } catch (error) {
    authError.value = error?.message || error?.response?.data?.message || '登录失败，请检查账号或密码'
  } finally {
    submitting.value = false
  }
}
</script>
