<template>
  <el-container class="layout">
    <el-header class="layout-header">
      <div class="brand">图书进销存管理系统</div>
      <el-menu mode="horizontal" :default-active="$route.path" router>
        <el-menu-item index="/books">图书列表</el-menu-item>
        <el-menu-item index="/cart">购物车</el-menu-item>
        <el-menu-item index="/orders">我的订单</el-menu-item>
      </el-menu>
      <div>
        <span style="margin-right:12px">{{ user?.username || '未登录' }}</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </el-header>
    <el-main class="content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { clearUser, getUser } from '../utils/auth'

const router = useRouter()
const user = computed(() => getUser())

function logout() {
  clearUser()
  router.push('/login')
}
</script>
