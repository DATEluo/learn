<template>
  <div class="book-manage-container">
    <div class="header">
      <h2>图书借阅</h2>
      <div class="operation-bar">
        <el-input
          v-model="searchParams.name"
          placeholder="图书名称"
          clearable
          style="width: 200px; margin-right: 10px;"
        />
        <el-input
          v-model="searchParams.author"
          placeholder="作者"
          clearable
          style="width: 200px; margin-right: 10px;"
        />
        <el-input
          v-model="searchParams.press"
          placeholder="出版社"
          clearable
          style="width: 200px; margin-right: 10px;"
        />
        <el-select
          v-model="searchParams.status"
          placeholder="状态"
          clearable
          style="width: 120px; margin-right: 10px;"
        >
          <el-option label="可借阅" value="0" />
          <el-option label="已借阅" value="1" />
          <el-option label="归还中" value="2" />
          <el-option label="已下架" value="3" />
        </el-select>
        <el-button type="primary" @click="fetchBooks">搜索</el-button>
        <el-button type="success" @click="openAddDialog" v-if="isAdmin">新增图书</el-button>
      </div>
    </div>

    <el-table :data="books" v-loading="loading" style="width: 100%">
      <el-table-column prop="name" label="图书名称" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="press" label="出版社" width="150" />
      <el-table-column prop="isbn" label="ISBN" width="160" />
      <el-table-column prop="price" label="价格" width="100" align="right">
        <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">
            {{ bookStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            size="small"
            type="primary"
            :disabled="row.status === '1' || row.status === '2'"
            @click="openEditDialog(row)"
            v-if="isAdmin"
          >
            编辑
          </el-button>
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
      @size-change="fetchBooks"
      @current-change="fetchBooks"
    />

    <!-- 新增/编辑图书对话框 -->
    <el-dialog v-model="bookDialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="bookForm" label-width="100px" ref="bookFormRef" :rules="rules">
        <el-form-item label="图书名称" prop="name">
          <el-input v-model="bookForm.name" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="出版社" prop="press">
          <el-input v-model="bookForm.press" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="页数" prop="pagination">
          <el-input-number v-model="bookForm.pagination" :min="1" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="bookForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="上架状态" v-if="isEdit">
          <el-radio-group v-model="bookForm.status">
            <el-radio label="0">上架</el-radio>
            <el-radio label="3">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBookForm">确定</el-button>
      </template>
    </el-dialog>

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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import bookApi from '@/api/book'

export default {
  setup() {
    const store = useStore()
    const books = ref([])
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchParams = ref({
      name: '',
      author: '',
      press: '',
      status: ''
    })
    
    const isAdmin = computed(() => store.state.user?.role === 'ADMIN')
    
    const bookDialogVisible = ref(false)
    const borrowDialogVisible = ref(false)
    const currentBook = ref({})
    const isEdit = ref(false)
    const bookForm = ref({
      name: '',
      isbn: '',
      press: '',
      author: '',
      pagination: undefined,
      price: undefined,
      status: '0'
    })
    
    const borrowForm = ref({
      returnTime: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000) // 默认14天后
    })
    
    const rules = ref({
      name: [{ required: true, message: '请输入图书名称', trigger: 'blur' }],
      isbn: [
        { required: true, message: '请输入ISBN', trigger: 'blur' },
        { pattern: /^\d{13}$/, message: 'ISBN必须是13位数字', trigger: 'blur' }
      ],
      press: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
      author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
      price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
    })
    
    const dialogTitle = computed(() => isEdit.value ? '编辑图书' : '新增图书')
    
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
        const response = await bookApi.searchBooks({
          page: currentPage.value,
          pageSize: pageSize.value,
          ...searchParams.value
        })
        books.value = response.data?.data || response.data || []
        total.value = response.total || response.data?.total || 0
      } catch (error) {
        ElMessage.error('获取图书失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    const openAddDialog = () => {
      isEdit.value = false
      bookForm.value = {
        name: '',
        isbn: '',
        press: '',
        author: '',
        pagination: undefined,
        price: undefined,
        status: '0'
      }
      bookDialogVisible.value = true
    }
    
    const openEditDialog = (book) => {
      isEdit.value = true
      currentBook.value = book
      bookForm.value = { ...book }
      bookDialogVisible.value = true
    }
    
    const openBorrowDialog = (book) => {
      currentBook.value = book
      borrowForm.value.returnTime = new Date(Date.now() + 14 * 24 * 60 * 60 * 1000)
      borrowDialogVisible.value = true
    }
    
    const submitBookForm = async () => {
      try {
        if (isEdit.value) {
          await bookApi.updateBook({
            ...bookForm.value,
            id: currentBook.value.id
          })
          ElMessage.success('更新成功')
        } else {
          await bookApi.addBook(bookForm.value)
          ElMessage.success('添加成功')
        }
        bookDialogVisible.value = false
        fetchBooks()
      } catch (error) {
        ElMessage.error('操作失败: ' + error.message)
      }
    }
    
    const handleBorrow = async () => {
      try {
        await bookApi.borrowBook({
          bookId: currentBook.value.id,
          returnTime: borrowForm.value.returnTime.toISOString()
        })
        ElMessage.success('借阅成功')
        borrowDialogVisible.value = false
        fetchBooks()
      } catch (error) {
        ElMessage.error('借阅失败: ' + error.message)
      }
    }
    
    onMounted(fetchBooks)
    
    return {
      books,
      loading,
      currentPage,
      pageSize,
      total,
      searchParams,
      isAdmin,
      bookDialogVisible,
      borrowDialogVisible,
      currentBook,
      isEdit,
      bookForm,
      borrowForm,
      rules,
      dialogTitle,
      statusTagType,
      bookStatusText,
      disabledDate,
      fetchBooks,
      openAddDialog,
      openEditDialog,
      openBorrowDialog,
      submitBookForm,
      handleBorrow
    }
  }
}
</script>

<style scoped>
.book-manage-container {
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

.operation-bar {
  display: flex;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>