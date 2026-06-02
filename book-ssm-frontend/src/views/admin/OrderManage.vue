<template>
  <div class="page admin-page">
    <div class="admin-page-head">
      <div>
        <h2 class="page-title">订单管理</h2>
        <p class="page-subtitle">按状态和用户名筛选订单，确认订单后可继续发货。</p>
      </div>
    </div>

    <div class="admin-toolbar">
      <el-select v-model="query.status" class="admin-filter-control" placeholder="状态" clearable @change="load">
        <el-option label="待确认" value="PENDING" />
        <el-option label="已确认" value="CONFIRMED" />
        <el-option label="已发货" value="DELIVERED" />
      </el-select>
      <el-input v-model="query.username" class="admin-filter-control" placeholder="用户名" clearable />
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table class="admin-table" :data="orders" border empty-text="暂无订单数据">
      <el-table-column type="expand">
        <template #default="{ row }">
          <el-table class="nested-table" :data="row.items || []" border>
            <el-table-column prop="bookTitle" label="图书" min-width="180" />
            <el-table-column label="单价" width="100">
              <template #default="{ row: item }">￥{{ formatPrice(item.bookPrice) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="小计" width="110">
              <template #default="{ row: item }">￥{{ formatPrice(item.subtotal) }}</template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="订单号" width="210" />
      <el-table-column prop="username" label="用户" width="110" />
      <el-table-column label="金额" width="110">
        <template #default="{ row }"><span class="price-text">￥{{ formatPrice(row.totalAmount) }}</span></template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusMeta(row.status).type">{{ statusMeta(row.status).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <div class="table-actions">
            <el-button size="small" type="primary" :disabled="row.status !== 'PENDING'" @click="confirm(row)">确认</el-button>
            <el-button size="small" type="success" :disabled="row.status !== 'CONFIRMED'" @click="ship(row)">发货</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const orders = ref([])
const query = reactive({ status: '', username: '', page: 1, pageSize: 50 })

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
  const data = await request.get('/api/admin/orders', { params: query })
  orders.value = data.list || []
}

async function confirm(row) {
  await request.put(`/api/admin/orders/${row.id}/confirm`)
  ElMessage.success('订单已确认')
  load()
}

async function ship(row) {
  await request.put(`/api/admin/orders/${row.id}/ship`)
  ElMessage.success('订单已发货')
  load()
}

onMounted(load)
</script>
