<template>
  <div class="page">
    <h2 class="page-title">管理员首页</h2>
    <el-row :gutter="16">
      <el-col v-for="item in items" :key="item.path" :span="6">
        <el-card>
          <h3>{{ item.title }}</h3>
          <el-button type="primary" @click="$router.push(item.path)">进入</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
const items = [
  { title: '分类管理', path: '/admin/categories' },
  { title: '图书管理', path: '/admin/books' },
  { title: '图书入库', path: '/admin/stock-in' },
  { title: '订单管理', path: '/admin/orders' }
]
</script>
