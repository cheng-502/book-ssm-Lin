<template>
  <div class="page">
    <h2 class="page-title">库存批次</h2>
    <el-table :data="list" border>
      <el-table-column prop="batchNo" label="批次号" width="210" />
      <el-table-column prop="bookTitle" label="图书" min-width="160" />
      <el-table-column prop="isbn" label="ISBN" width="145" />
      <el-table-column prop="quantity" label="入库数量" width="90" />
      <el-table-column prop="remainQuantity" label="剩余数量" width="90" />
      <el-table-column prop="costPrice" label="进货价" width="90" />
      <el-table-column prop="supplier" label="供应商" width="160" />
      <el-table-column prop="createdAt" label="入库时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../../utils/request'

const list = ref([])
function formatTime(value) {
  return value ? new Date(value).toLocaleString() : ''
}
async function load() {
  const data = await request.get('/api/stocks/batches', { params: { page: 1, pageSize: 100 } })
  list.value = data.list || []
}
onMounted(load)
</script>
