<template>
  <div class="page">
    <h2 class="page-title">图书入库</h2>
    <el-form :model="model" label-width="100px" style="max-width:560px">
      <el-form-item label="图书">
        <el-select v-model="model.bookId" filterable style="width:100%">
          <el-option v-for="b in books" :key="b.id" :label="`${b.title} / ${b.isbn}`" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="入库数量"><el-input-number v-model="model.quantity" :min="1" /></el-form-item>
      <el-form-item label="进货单价"><el-input-number v-model="model.costPrice" :min="0.01" :precision="2" /></el-form-item>
      <el-form-item label="供应商"><el-input v-model="model.supplier" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="model.remark" /></el-form-item>
      <el-form-item><el-button type="primary" @click="submit">确认入库</el-button></el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request, { form } from '../../utils/request'

const books = ref([])
const model = reactive({ bookId: '', quantity: 1, costPrice: 1, supplier: '', remark: '' })

async function loadBooks() {
  const data = await request.get('/api/books', { params: { page: 1, pageSize: 100 } })
  books.value = data.list || []
}

async function submit() {
  await request.post('/api/stocks/in', form(model))
  ElMessage.success('入库成功')
}

onMounted(loadBooks)
</script>
