<template>
  <div class="page user-page">
    <div class="user-page-head">
      <div>
        <h2 class="page-title">购物车</h2>
        <p class="page-subtitle">选择图书后提交订单，数量会同步更新到购物车。</p>
      </div>
      <div class="cart-summary">
        <span>已选 {{ selected.length }} 件</span>
        <strong>￥{{ formatPrice(selectedTotal) }}</strong>
      </div>
    </div>

    <template v-if="items.length">
      <el-table class="desktop-table" :data="items" border @selection-change="selected = $event">
        <el-table-column type="selection" width="48" />
        <el-table-column prop="bookTitle" label="图书" min-width="180" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column label="单价" width="100">
          <template #default="{ row }"><span class="price-text">￥{{ formatPrice(row.price) }}</span></template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="数量" width="170">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              class="quantity-control"
              :min="1"
              size="small"
              @change="update(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }"><span class="price-text">￥{{ formatPrice(row.price * row.quantity) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" width="96">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mobile-list">
        <article v-for="item in items" :key="item.id" class="cart-card">
          <div class="cart-card-head">
            <el-checkbox :model-value="isSelected(item)" @change="checked => toggleSelected(item, checked)" />
            <div class="cart-card-title">{{ item.bookTitle }}</div>
            <el-button type="danger" size="small" plain @click="remove(item)">删除</el-button>
          </div>
          <div class="cart-card-meta">ISBN {{ item.isbn }} · 库存 {{ item.stock }}</div>
          <div class="cart-card-row">
            <span class="price-text">￥{{ formatPrice(item.price) }}</span>
            <el-input-number
              v-model="item.quantity"
              class="quantity-control"
              :min="1"
              size="small"
              @change="update(item)"
            />
          </div>
          <div class="cart-card-total">小计 ￥{{ formatPrice(item.price * item.quantity) }}</div>
        </article>
      </div>

      <div class="cart-action-bar">
        <div>
          <div class="cart-action-total">合计 ￥{{ formatPrice(selectedTotal) }}</div>
          <div class="cart-action-note">请勾选需要下单的图书。</div>
        </div>
        <el-button type="primary" :disabled="!selected.length" @click="dialogVisible = true">提交订单</el-button>
      </div>
    </template>

    <el-empty v-else class="user-empty" description="购物车暂无图书">
      <el-button type="primary" @click="$router.push('/books')">去选择图书</el-button>
    </el-empty>

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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request, { form } from '../../utils/request'

const items = ref([])
const selected = ref([])
const dialogVisible = ref(false)
const orderForm = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })

const selectedTotal = computed(() => selected.value.reduce((total, item) => {
  return total + Number(item.price || 0) * Number(item.quantity || 0)
}, 0))

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function isSelected(row) {
  return selected.value.some(item => item.id === row.id)
}

function toggleSelected(row, checked) {
  if (checked && !isSelected(row)) {
    selected.value = [...selected.value, row]
    return
  }
  if (!checked) {
    selected.value = selected.value.filter(item => item.id !== row.id)
  }
}

async function load() {
  items.value = await request.get('/api/cart')
  selected.value = selected.value
    .map(selectedItem => items.value.find(item => item.id === selectedItem.id))
    .filter(Boolean)
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
  selected.value = []
  await load()
}

onMounted(load)
</script>
