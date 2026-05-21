<template>
  <div class="page">
    <h2 class="page-title">图书管理</h2>
    <div class="toolbar">
      <el-input v-model="query.title" placeholder="书名" style="width:160px" clearable />
      <el-input v-model="query.author" placeholder="作者" style="width:140px" clearable />
      <el-select v-model="query.categoryId" placeholder="分类" style="width:150px" clearable>
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="open()">新增图书</el-button>
    </div>
    <el-table :data="books" border>
      <el-table-column prop="isbn" label="ISBN" width="145" />
      <el-table-column prop="title" label="书名" min-width="150" />
      <el-table-column prop="author" label="作者" width="110" />
      <el-table-column prop="publisher" label="出版社" width="150" />
      <el-table-column prop="categoryName" label="分类" width="110" />
      <el-table-column prop="price" label="售价" width="90" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">{{ row.status === 1 ? '上架' : '下架' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">修改</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggle(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button type="danger" size="small" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" title="图书" width="620px">
      <el-form :model="formModel" label-width="90px">
        <el-form-item label="ISBN"><el-input v-model="formModel.isbn" /></el-form-item>
        <el-form-item label="书名"><el-input v-model="formModel.title" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="formModel.author" /></el-form-item>
        <el-form-item label="出版社"><el-input v-model="formModel.publisher" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="formModel.categoryId" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价"><el-input-number v-model="formModel.price" :min="0" /></el-form-item>
        <el-form-item label="成本价"><el-input-number v-model="formModel.costPrice" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="formModel.status" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="formModel.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request, { form } from '../../utils/request'

const categories = ref([])
const books = ref([])
const visible = ref(false)
const query = reactive({ title: '', author: '', categoryId: '', page: 1, pageSize: 50 })
const emptyBook = { id: null, isbn: '', title: '', author: '', publisher: '', categoryId: '', price: 0, costPrice: 0, status: 1, description: '' }
const formModel = reactive({ ...emptyBook })

async function loadCategories() {
  categories.value = await request.get('/api/categories')
}

async function load() {
  const data = await request.get('/api/books', { params: query })
  books.value = data.list || []
}

function open(row) {
  Object.assign(formModel, row ? { ...row, costPrice: row.costPrice || row.price } : emptyBook)
  visible.value = true
}

async function save() {
  await request.post(formModel.id ? '/api/books/update' : '/api/books/create', form(formModel))
  visible.value = false
  load()
}

async function toggle(row) {
  await request.post(row.status === 1 ? '/api/books/off-sale' : '/api/books/on-sale', form({ id: row.id }))
  load()
}

async function remove(row) {
  await request.post('/api/books/delete', form({ id: row.id }))
  load()
}

onMounted(() => {
  loadCategories()
  load()
})
</script>
