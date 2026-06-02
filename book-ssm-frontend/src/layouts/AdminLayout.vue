<template>
  <el-container class="layout">
    <el-aside class="layout-sidebar" width="220px">
      <div class="layout-brand">
        <div class="brand">管理后台</div>
        <div class="layout-brand-subtitle">Book Inventory</div>
      </div>
      <el-menu class="layout-menu" :default-active="$route.path" router>
        <el-menu-item index="/admin">首页</el-menu-item>
        <el-menu-item index="/admin/categories">分类管理</el-menu-item>
        <el-menu-item index="/admin/books">图书管理</el-menu-item>
        <el-menu-item index="/admin/stock-in">图书入库</el-menu-item>
        <el-menu-item index="/admin/batches">库存批次</el-menu-item>
        <el-menu-item index="/admin/records">库存流水</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="layout-title">基于 SSM 的图书进销存管理系统</div>
        <div class="layout-account">
          <span class="layout-account-name">{{ user?.username }}</span>
          <el-button size="small" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
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
