<template>
  <div class="page">
    <h2 class="page-title">购物车</h2>
    <el-table :data="items" border @selection-change="selected = $event">
      <el-table-column type="selection" width="48" />
      <el-table-column prop="bookTitle" label="图书" min-width="180" />
      <el-table-column prop="isbn" label="ISBN" width="150" />
      <el-table-column prop="price" label="单价" width="90" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="数量" width="160">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" size="small" @change="update(row)" />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="100">
        <template #default="{ row }">{{ (row.price * row.quantity).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:16px">
      <el-button type="primary" :disabled="!selected.length" @click="dialogVisible = true">提交订单</el-button>
    </div>

    <el-dialog v-model="dialogVisible" title="创建订单" width="460px">
      <el-form :model="orderForm" label-width="90px">
        <el-form-item label="收货人"><el-input v-model="orderForm.receiverName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="orderForm.receiverPhone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="orderForm.receiverAddress" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="orderForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createOrder">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request, { form } from '../../utils/request'

const items = ref([])
const selected = ref([])
const dialogVisible = ref(false)
const orderForm = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })

async function load() {
  items.value = await request.get('/api/cart')
}

async function update(row) {
  await request.post('/api/cart/update', form({ id: row.id, quantity: row.quantity }))
  ElMessage.success('数量已更新')
}

async function remove(row) {
  await request.post('/api/cart/delete', form({ id: row.id }))
  await load()
}

async function createOrder() {
  await request.post('/api/orders/create', form({
    cartItemIds: selected.value.map(item => item.id).join(','),
    ...orderForm
  }))
  ElMessage.success('订单创建成功')
  dialogVisible.value = false
  await load()
}

onMounted(load)
</script>
