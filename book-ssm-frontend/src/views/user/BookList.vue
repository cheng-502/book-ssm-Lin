<template>
  <div class="page user-page">
    <div class="user-page-head">
      <div>
        <h2 class="page-title">图书列表</h2>
        <p class="page-subtitle">按书名、分类和价格筛选可购买图书。</p>
      </div>
    </div>

    <div class="user-filter">
      <el-input v-model="query.title" class="filter-control" placeholder="书名关键词" clearable />
      <el-select v-model="query.categoryId" class="filter-control" placeholder="分类" clearable>
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-input-number v-model="minPrice" class="filter-number" :min="0" placeholder="最低价" />
      <el-input-number v-model="maxPrice" class="filter-number" :min="0" placeholder="最高价" />
      <el-button type="primary" @click="load">查询图书</el-button>
    </div>

    <div v-if="filteredBooks.length" class="book-grid">
      <article v-for="book in filteredBooks" :key="book.id" class="book-card">
        <div class="book-card-main">
          <div class="book-title">{{ book.title }}</div>
          <div class="book-meta">{{ book.author }} · {{ book.publisher }}</div>
          <div class="book-tags">
            <el-tag type="info">{{ book.categoryName || '未分类' }}</el-tag>
            <el-tag :type="book.stock > 0 ? 'success' : 'danger'">库存 {{ book.stock || 0 }}</el-tag>
          </div>
          <div class="book-isbn">ISBN {{ book.isbn }}</div>
        </div>
        <div class="book-card-action">
          <div class="price-text">￥{{ formatPrice(book.price) }}</div>
          <div class="quantity-row">
            <el-input-number
              v-model="book.buyQuantity"
              class="quantity-control"
              :disabled="!book.stock"
              :min="1"
              :max="book.stock || 1"
              size="small"
            />
            <el-button
              type="primary"
              size="small"
              :disabled="!book.stock"
              @click="addCart(book)"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </article>
    </div>

    <el-empty v-else class="user-empty" description="暂无符合条件的图书">
      <el-button type="primary" @click="resetFilters">清空筛选</el-button>
    </el-empty>

    <el-pagination
      v-if="page.total"
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

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function resetFilters() {
  query.title = ''
  query.categoryId = ''
  minPrice.value = null
  maxPrice.value = null
  load()
}

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
