<template>
  <div class="page">
    <h2 class="page-title">图书列表</h2>
    <div class="toolbar">
      <el-input v-model="query.title" placeholder="书名关键词" style="width:180px" clearable />
      <el-select v-model="query.categoryId" placeholder="分类" style="width:160px" clearable>
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-input-number v-model="minPrice" :min="0" placeholder="最低价" />
      <el-input-number v-model="maxPrice" :min="0" placeholder="最高价" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="filteredBooks" border>
      <el-table-column prop="isbn" label="ISBN" width="150" />
      <el-table-column prop="title" label="书名" min-width="160" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="publisher" label="出版社" width="160" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="price" label="售价" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-input-number v-model="row.buyQuantity" :min="1" :max="row.stock || 1" size="small" />
          <el-button type="primary" size="small" @click="addCart(row)">加入</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top:16px"
      layout="prev, pager, next, total"
      :total="page.total"
      :page-size="query.pageSize"
      v-model:current-page="query.page"
      @current-change="load"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request, { form } from '../../utils/request'

const categories = ref([])
const books = ref([])
const minPrice = ref(null)
const maxPrice = ref(null)
const query = reactive({ title: '', categoryId: '', status: 1, page: 1, pageSize: 10 })
const page = reactive({ total: 0 })

const filteredBooks = computed(() => books.value.filter(book => {
  const price = Number(book.price || 0)
  if (minPrice.value !== null && minPrice.value !== undefined && price < minPrice.value) return false
  if (maxPrice.value !== null && maxPrice.value !== undefined && maxPrice.value > 0 && price > maxPrice.value) return false
  return true
}))

async function loadCategories() {
  categories.value = await request.get('/api/categories')
}

async function load() {
  const data = await request.get('/api/books', { params: query })
  books.value = (data.list || []).map(item => ({ ...item, buyQuantity: 1 }))
  page.total = data.total || 0
}

async function addCart(row) {
  await request.post('/api/cart/add', form({ bookId: row.id, quantity: row.buyQuantity || 1 }))
  ElMessage.success('已加入购物车')
}

onMounted(() => {
  loadCategories()
  load()
})
</script>
