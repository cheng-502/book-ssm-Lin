<template>
  <div class="page">
    <h2 class="page-title">图书分类管理</h2>
    <div class="toolbar"><el-button type="primary" @click="open()">新增分类</el-button></div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="sortOrder" label="排序" width="90" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">修改</el-button>
          <el-button type="danger" size="small" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="visible" title="分类" width="420px">
      <el-form :model="formModel" label-width="80px">
        <el-form-item label="名称"><el-input v-model="formModel.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="formModel.description" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="formModel.sortOrder" /></el-form-item>
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
import { ElMessage } from 'element-plus'
import request, { form } from '../../utils/request'

const list = ref([])
const visible = ref(false)
const formModel = reactive({ id: null, name: '', description: '', sortOrder: 0 })

async function load() {
  list.value = await request.get('/api/categories')
}

function open(row) {
  Object.assign(formModel, row || { id: null, name: '', description: '', sortOrder: 0 })
  visible.value = true
}

async function save() {
  await request.post(formModel.id ? '/api/categories/update' : '/api/categories/create', form(formModel))
  ElMessage.success('保存成功')
  visible.value = false
  load()
}

async function remove(row) {
  await request.post('/api/categories/delete', form({ id: row.id }))
  load()
}

onMounted(load)
</script>
