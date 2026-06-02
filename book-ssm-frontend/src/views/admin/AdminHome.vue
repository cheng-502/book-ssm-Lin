<template>
  <div class="page admin-page">
    <div class="admin-page-head">
      <div>
        <h2 class="page-title">管理员首页</h2>
        <p class="page-subtitle">集中处理分类、图书、库存和订单等日常管理任务。</p>
      </div>
    </div>

    <div class="admin-module-grid">
      <article v-for="item in items" :key="item.path" class="admin-module-card">
        <div>
          <div class="admin-module-title">{{ item.title }}</div>
          <div class="admin-module-desc">{{ item.desc }}</div>
        </div>
        <el-button type="primary" size="small" @click="$router.push(item.path)">进入模块</el-button>
      </article>
    </div>
  </div>
</template>

<script setup>
const items = [
  { title: '分类管理', desc: '维护图书分类、描述和排序。', path: '/admin/categories' },
  { title: '图书管理', desc: '新增、编辑、上下架和删除图书。', path: '/admin/books' },
  { title: '图书入库', desc: '登记入库批次并补充库存。', path: '/admin/stock-in' },
  { title: '订单管理', desc: '确认订单并处理发货流程。', path: '/admin/orders' }
]
</script>
