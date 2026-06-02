<template>
  <div class="page user-page">
    <div class="user-page-head">
      <div>
        <h2 class="page-title">我的订单</h2>
        <p class="page-subtitle">查看订单状态和订单内图书明细。</p>
      </div>
    </div>

    <div class="user-filter">
      <el-select v-model="query.status" class="filter-control" placeholder="订单状态" clearable @change="load">
        <el-option label="待确认" value="PENDING" />
        <el-option label="已确认" value="CONFIRMED" />
        <el-option label="已发货" value="DELIVERED" />
      </el-select>
    </div>

    <div v-if="orders.length" class="order-list">
      <article v-for="order in orders" :key="order.id || order.orderNo" class="order-card">
        <div class="order-card-head">
          <div>
            <div class="order-no">{{ order.orderNo }}</div>
            <div class="order-meta">{{ formatTime(order.createdAt) }} · 收货人 {{ order.receiverName || '-' }}</div>
          </div>
          <el-tag :type="statusMeta(order.status).type">{{ statusMeta(order.status).label }}</el-tag>
        </div>
        <div class="order-total">￥{{ formatPrice(order.totalAmount) }}</div>
        <el-table class="order-items-table" :data="order.items || []" border>
          <el-table-column prop="bookTitle" label="图书" min-width="180" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }">￥{{ formatPrice(row.bookPrice) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="110">
            <template #default="{ row }">￥{{ formatPrice(row.subtotal) }}</template>
          </el-table-column>
        </el-table>
      </article>
    </div>

    <el-empty v-else class="user-empty" description="暂无订单记录">
      <el-button type="primary" @click="$router.push('/books')">去选择图书</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '../../utils/request'

const orders = ref([])
const query = reactive({ status: '', page: 1, pageSize: 20 })

function formatTime(value) {
  return value ? new Date(value).toLocaleString() : ''
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function statusMeta(status) {
  const map = {
    PENDING: { label: '待确认', type: 'warning' },
    CONFIRMED: { label: '已确认', type: 'primary' },
    DELIVERED: { label: '已发货', type: 'success' }
  }
  return map[status] || { label: status || '未知', type: 'info' }
}

async function load() {
  const data = await request.get('/api/orders/mine', { params: query })
  orders.value = data.list || []
}

onMounted(load)
</script>
