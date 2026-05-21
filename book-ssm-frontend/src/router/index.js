import { createRouter, createWebHistory } from 'vue-router'
import { getUser } from '../utils/auth'

const routes = [
  { path: '/', redirect: '/books' },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  {
    path: '/',
    component: () => import('../layouts/UserLayout.vue'),
    children: [
      { path: 'books', component: () => import('../views/user/BookList.vue') },
      { path: 'cart', component: () => import('../views/user/Cart.vue') },
      { path: 'orders', component: () => import('../views/user/MyOrders.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      { path: '', component: () => import('../views/admin/AdminHome.vue') },
      { path: 'categories', component: () => import('../views/admin/CategoryManage.vue') },
      { path: 'books', component: () => import('../views/admin/BookManage.vue') },
      { path: 'stock-in', component: () => import('../views/admin/StockIn.vue') },
      { path: 'batches', component: () => import('../views/admin/StockBatches.vue') },
      { path: 'records', component: () => import('../views/admin/StockRecords.vue') },
      { path: 'orders', component: () => import('../views/admin/OrderManage.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(to => {
  if (to.meta.role === 'ADMIN') {
    const user = getUser()
    if (!user || user.role !== 'ADMIN') return '/login'
  }
  return true
})

export default router
