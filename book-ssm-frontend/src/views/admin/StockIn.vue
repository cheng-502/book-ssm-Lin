<template>
  <div class="page admin-page">
    <div class="admin-page-head">
      <div>
        <h2 class="page-title">图书入库</h2>
        <p class="page-subtitle">选择图书并登记数量、进货单价和供应商，提交后生成库存批次。</p>
      </div>
    </div>

    <div class="stock-in-layout">
      <section class="stock-form-panel">
        <el-form
          ref="formRef"
          class="admin-form"
          :model="model"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="图书" prop="bookId">
            <el-select v-model="model.bookId" class="form-control-full" filterable placeholder="请选择图书">
              <el-option v-for="b in books" :key="b.id" :label="`${b.title} / ${b.isbn}`" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="入库数量" prop="quantity">
            <el-input-number v-model="model.quantity" :min="1" />
          </el-form-item>
          <el-form-item label="进货单价" prop="costPrice">
            <el-input-number v-model="model.costPrice" :min="0.01" :precision="2" />
          </el-form-item>
          <el-form-item label="供应商" prop="supplier">
            <el-input v-model.trim="model.supplier" placeholder="请输入供应商名称" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model.trim="model.remark" placeholder="可填写采购单号或说明" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submit">确认入库</el-button>
          </el-form-item>
        </el-form>
      </section>

      <aside class="stock-preview-panel">
        <div class="stock-preview-title">入库确认</div>
        <div class="stock-preview-list">
          <div class="stock-preview-row">
            <span>图书</span>
            <strong>{{ selectedBook?.title || '未选择' }}</strong>
          </div>
          <div class="stock-preview-row">
            <span>ISBN</span>
            <strong>{{ selectedBook?.isbn || '-' }}</strong>
          </div>
          <div class="stock-preview-row">
            <span>数量</span>
            <strong>{{ model.quantity }}</strong>
          </div>
          <div class="stock-preview-row">
            <span>进货单价</span>
            <strong class="price-text">￥{{ formatPrice(model.costPrice) }}</strong>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request, { form } from '../../utils/request'

const books = ref([])
const formRef = ref(null)
const model = reactive({ bookId: '', quantity: 1, costPrice: 1, supplier: '', remark: '' })
const rules = {
  bookId: [{ required: true, message: '请选择图书', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  costPrice: [{ required: true, message: '请输入进货单价', trigger: 'blur' }],
  supplier: [{ required: true, message: '请输入供应商', trigger: 'blur' }]
}

const selectedBook = computed(() => books.value.find(book => book.id === model.bookId))

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

async function loadBooks() {
  const data = await request.get('/api/books', { params: { page: 1, pageSize: 100 } })
  books.value = data.list || []
}

async function submit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  await ElMessageBox.confirm(
    `确认将「${selectedBook.value?.title || '所选图书'}」入库 ${model.quantity} 本？`,
    '确认入库',
    { type: 'warning', confirmButtonText: '确认入库', cancelButtonText: '取消' }
  )
  await request.post('/api/stocks/in', form(model))
  ElMessage.success('入库成功')
}

onMounted(loadBooks)
</script>
