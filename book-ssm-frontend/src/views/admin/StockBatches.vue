<template>
  <div class="page admin-page">
    <div class="admin-page-head">
      <div>
        <h2 class="page-title">库存批次</h2>
        <p class="page-subtitle">按批次查看入库数量、剩余数量、进货价和供应商，便于比较批次消耗。</p>
      </div>
    </div>

    <el-table class="admin-table" :data="list" border empty-text="暂无库存批次">
      <el-table-column prop="batchNo" label="批次号" width="210" />
      <el-table-column prop="bookTitle" label="图书" min-width="180" />
      <el-table-column prop="isbn" label="ISBN" width="145" />
      <el-table-column prop="quantity" label="入库数量" width="90" />
      <el-table-column label="剩余数量" width="150">
        <template #default="{ row }">
          <div class="batch-remain">
            <strong>{{ row.remainQuantity }}</strong>
            <el-progress
              :percentage="remainPercent(row)"
              :stroke-width="6"
              :show-text="false"
              :status="row.remainQuantity > 0 ? '' : 'exception'"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="批次状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.remainQuantity > 0 ? 'success' : 'info'">
            {{ row.remainQuantity > 0 ? '可用' : '已用完' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="进货价" width="100">
        <template #default="{ row }"><span class="price-text">￥{{ formatPrice(row.costPrice) }}</span></template>
      </el-table-column>
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

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function remainPercent(row) {
  const quantity = Number(row.quantity || 0)
  if (!quantity) return 0
  return Math.round((Number(row.remainQuantity || 0) / quantity) * 100)
}

async function load() {
  const data = await request.get('/api/stocks/batches', { params: { page: 1, pageSize: 100 } })
  list.value = data.list || []
}

onMounted(load)
</script>
