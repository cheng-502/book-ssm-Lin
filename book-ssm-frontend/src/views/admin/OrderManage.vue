<template>
  <div class="page">
    <h2 class="page-title">订单管理</h2>
    <div class="toolbar">
      <el-select v-model="query.status" placeholder="状态" clearable style="width:150px" @change="load">
        <el-option label="待确认" value="PENDING" />
        <el-option label="已确认" value="CONFIRMED" />
        <el-option label="已发货" value="DELIVERED" />
      </el-select>
      <el-input v-model="query.username" placeholder="用户名" style="width:160px" clearable />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="orders" border>
      <el-table-column type="expand">
        <template #default="{ row }">
          <el-table :data="row.items || []" border>
            <el-table-column prop="bookTitle" label="图书" />
            <el-table-column prop="bookPrice" label="单价" width="100" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="subtotal" label="小计" width="100" />
          </el-table>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="订单号" width="210" />
      <el-table-column prop="username" label="用户" width="110" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" type="primary" :disabled="row.status !== 'PENDING'" @click="confirm(row)">确认</el-button>
          <el-button size="small" type="success" :disabled="row.status !== 'CONFIRMED'" @click="ship(row)">发货</el-button>
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
