<template>
    <div class="current-borrow-container">
      <div class="header">
        <h2>当前借阅</h2>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索图书..."
          style="width: 300px"
          clearable
          @change="fetchBooks"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
  
      <el-table :data="books" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="图书名称" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="press" label="出版社" width="150" />
        <el-table-column prop="isbn" label="ISBN" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ bookStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="borrowTime" label="借阅时间" width="180" />
        <el-table-column prop="returnTime" label="应还时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              size="small"
              type="danger"
              link
              @click="handleReturn(row)"
              v-if="row.status === '1' || (row.status === '2' && isAdmin)"
            >
              {{ row.status === '1' ? '归还' : '确认归还' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper ->, total, prev, pager, next"
        @size-change="fetchBooks"
        @current-change="fetchBooks"
      />
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  import { useStore } from 'vuex'
  import bookApi from '@/api/book'
  
  export default {
    components: { Search },
    setup() {
      const store = useStore()
      const books = ref([])
      const loading = ref(false)
      const currentPage = ref(1)
      const pageSize = ref(10)
      const total = ref(0)
      const searchKeyword = ref('')
      
      const isAdmin = computed(() => store.state.user?.role === 'ADMIN')
      const userId = computed(() => store.state.user?.id)
      
      const statusTagType = (status) => {
        const map = { '1': 'warning', '2': 'info' }
        return map[status] || ''
      }
      
      const bookStatusText = (status) => {
        const map = {
          '1': '借阅中',
          '2': '归还中'
        }
        return map[status] || status
      }
      
      const fetchBooks = async () => {
        loading.value = true
        try {
          const res = await bookApi.getCurrentBorrows({
            page: currentPage.value,
            pageSize: pageSize.value,
            keyword: searchKeyword.value
          })
          books.value = res.data
          total.value = res.total
        } catch (error) {
          ElMessage.error('获取数据失败: ' + error.message)
        } finally {
          loading.value = false
        }
      }
      
      const handleReturn = (book) => {
        const isConfirm = book.status === '2'
        const title = isConfirm ? '确认归还' : '申请归还'
        const message = isConfirm 
          ? '请确认图书已归还并完好无损？'
          : '确定要申请归还这本图书吗？'
          
        ElMessageBox.confirm(message, title, {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          try {
            await bookApi.returnBook(book.id, isConfirm)
            ElMessage.success(`${title}成功`)
            fetchBooks()
          } catch (error) {
            ElMessage.error(`${title}失败: ` + error.message)
          }
        }).catch(() => {})
      }
      
      onMounted(fetchBooks)
      
      return {
        books,
        loading,
        currentPage,
        pageSize,
        total,
        searchKeyword,
        isAdmin,
        statusTagType,
        bookStatusText,
        fetchBooks,
        handleReturn
      }
    }
  }
  </script>
  
  <style scoped>
  .current-borrow-container {
    background: white;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .el-pagination {
    margin-top: 20px;
    justify-content: center;
  }
  </style>