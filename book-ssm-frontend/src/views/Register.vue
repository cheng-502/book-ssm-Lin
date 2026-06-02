<template>
  <div class="auth-page">
    <div class="auth-box">
      <div class="auth-brand">
        <div class="auth-brand-mark">书</div>
        <div>
          <div class="auth-brand-title">图书进销存管理系统</div>
          <div class="auth-brand-subtitle">创建账号后即可进入图书检索与订单流程</div>
        </div>
      </div>

      <h1 class="auth-title">用户注册</h1>

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
        @keyup.enter="register"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="model.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="model.password"
            autocomplete="new-password"
            placeholder="请输入密码"
            show-password
            type="password"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model.trim="model.nickname" autocomplete="name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model.trim="model.phone" autocomplete="tel" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model.trim="model.email" autocomplete="email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-button class="auth-submit" type="primary" :loading="submitting" @click="register">注册</el-button>
        <div class="auth-switch">
          已有账号？
          <el-button link type="primary" @click="$router.push('/login')">返回登录</el-button>
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

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const authError = ref('')
const model = reactive({ username: '', password: '', nickname: '', phone: '', email: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入 11 位手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入有效邮箱', trigger: 'blur' }]
}

async function register() {
  authError.value = ''
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    submitting.value = true
    await request.post('/api/auth/register', form(model))
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    authError.value = error?.message || error?.response?.data?.message || '注册失败，请检查填写信息'
  } finally {
    submitting.value = false
  }
}
</script>
