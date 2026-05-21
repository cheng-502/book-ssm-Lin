<template>
  <div class="page">
    <h2 class="page-title">库存流水</h2>
    <div class="toolbar">
      <el-select v-model="query.type" placeholder="类型" clearable style="width:140px" @change="load">
        <el-option label="入库 IN" value="IN" />
        <el-option label="出库 OUT" value="OUT" />
      </el-select>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="type" label="类型" width="90" />
      <el-table-column prop="bookTitle" label="图书" min-width="160" />
      <el-table-column prop="batchNo" label="批次号" width="210" />
      <el-table-column prop="quantity" label="变动数量" width="100" />
      <el-table-column prop="beforeStock" label="变动前" width="90" />
      <el-table-column prop="afterStock" label="变动后" width="90" />
      <el-table-column prop="orderId" label="订单ID" width="90" />
      <el-table-column prop="remark" label="备注" min-width="160" />
      <el-table-column prop="createdAt" label="时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '../../utils/request'

const query = reactive({ type: '', page: 1, pageSize: 100 })
const list = ref([])
function formatTime(value) {
  return value ? new Date(value).toLocaleString() : ''
}
async function load() {
  const data = await request.get('/api/stocks/records', { params: query })
  list.value = data.list || []
}
onMounted(load)
</script>
