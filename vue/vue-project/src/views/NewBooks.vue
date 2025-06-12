<template>
  <div class="new-books-container">
    <div class="header">
      <h2>新书推荐</h2>
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
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            size="small"
            type="primary"
            :disabled="row.status !== '0'"
            @click="openBorrowDialog(row)"
          >
            借阅
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 借阅对话框 -->
    <el-dialog v-model="borrowDialogVisible" title="图书借阅" width="500px">
      <el-form :model="borrowForm" label-width="100px">
        <el-form-item label="图书名称">
          <el-input v-model="currentBook.name" disabled />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="currentBook.isbn" disabled />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="currentBook.press" disabled />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="currentBook.author" disabled />
        </el-form-item>
        <el-form-item label="归还时间" required>
          <el-date-picker
            v-model="borrowForm.returnTime"
            type="date"
            placeholder="选择归还日期"
            :disabled-date="disabledDate"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBorrow">确认借阅</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import bookApi from '@/api/book'

export default {
  components: { Search },
  setup() {
    const books = ref([])
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchKeyword = ref('')
    
    const borrowDialogVisible = ref(false)
    const currentBook = ref({})
    const borrowForm = ref({
      returnTime: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000) // 默认14天后
    })

    const statusTagType = (status) => {
      const map = { '0': 'success', '1': 'warning', '2': 'info', '3': 'danger' }
      return map[status] || ''
    }
    
    const bookStatusText = (status) => {
      const map = {
        '0': '可借阅',
        '1': '已借阅',
        '2': '归还中',
        '3': '已下架'
      }
      return map[status] || status
    }
    
    const disabledDate = (date) => {
      return date < new Date(Date.now() - 24 * 60 * 60 * 1000)
    }
    
    const fetchBooks = async () => {
      loading.value = true
      try {
        const response = await bookApi.getNewBooks({
          page: currentPage.value,
          pageSize: pageSize.value,
          keyword: searchKeyword.value
        })
        
        console.log('API响应:', response)
        console.log('响应数据类型:', Array.isArray(response) ? '数组' : typeof response)

      // 提取实际数据数组
      books.value = response.data?.data || response.data || []
    
      // 提取总数
      total.value = response.total || response.data?.total || 0
        
      } catch (error) {
        ElMessage.error('获取图书失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    const openBorrowDialog = (book) => {
      currentBook.value = book
      borrowForm.value.returnTime = new Date(Date.now() + 14 * 24 * 60 * 60 * 1000)
      borrowDialogVisible.value = true
    }
    
    const handleBorrow = async () => {
      try {
        await bookApi.borrowBook({
          bookId: currentBook.value.id,
          returnTime: borrowForm.value.returnTime
        })
        ElMessage.success('借阅成功')
        borrowDialogVisible.value = false
        fetchBooks()
      } catch (error) {
        ElMessage.error('借阅失败: ' + error.message)
      }
    }
    
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchBooks()
    }
    
    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchBooks()
    }
    
    onMounted(fetchBooks)
    
    return {
      books,
      loading,
      currentPage,
      pageSize,
      total,
      searchKeyword,
      borrowDialogVisible,
      currentBook,
      borrowForm,
      statusTagType,
      bookStatusText,
      disabledDate,
      fetchBooks,
      openBorrowDialog,
      handleBorrow,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.new-books-container {
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