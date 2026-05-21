<template>
  <div class="page">
    <h2 class="page-title">我的订单</h2>
    <div class="toolbar">
      <el-select v-model="query.status" placeholder="订单状态" style="width:160px" clearable @change="load">
        <el-option label="待确认" value="PENDING" />
        <el-option label="已确认" value="CONFIRMED" />
        <el-option label="已发货" value="DELIVERED" />
      </el-select>
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
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="receiverName" label="收货人" width="120" />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
    </el-table>
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

async function load() {
  const data = await request.get('/api/orders/mine', { params: query })
  orders.value = data.list || []
}

onMounted(load)
</script>
