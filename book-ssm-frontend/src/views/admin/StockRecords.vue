<template>
  <div class="page admin-page">
    <div class="admin-page-head">
      <div>
        <h2 class="page-title">库存流水</h2>
        <p class="page-subtitle">追踪入库、出库和库存变动前后的数量变化。</p>
      </div>
    </div>

    <div class="admin-toolbar">
      <el-select v-model="query.type" class="admin-filter-control" placeholder="类型" clearable @change="load">
        <el-option label="入库 IN" value="IN" />
        <el-option label="出库 OUT" value="OUT" />
      </el-select>
    </div>

    <el-table class="admin-table" :data="list" border empty-text="暂无库存流水">
      <el-table-column label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type === 'IN' ? 'success' : 'warning'">{{ typeLabel(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="bookTitle" label="图书" min-width="180" />
      <el-table-column prop="batchNo" label="批次号" width="210" />
      <el-table-column label="变动数量" width="110">
        <template #default="{ row }">
          <span :class="row.type === 'IN' ? 'stock-delta-in' : 'stock-delta-out'">
            {{ row.type === 'IN' ? '+' : '-' }}{{ row.quantity }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="beforeStock" label="变动前" width="90" />
      <el-table-column prop="afterStock" label="变动后" width="90" />
      <el-table-column prop="orderId" label="订单ID" width="90" />
      <el-table-column prop="remark" label="备注" min-width="180" />
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

function typeLabel(value) {
  return value === 'IN' ? '入库' : value === 'OUT' ? '出库' : value || '-'
}

async function load() {
  const data = await request.get('/api/stocks/records', { params: query })
  list.value = data.list || []
}

onMounted(load)
</script>
