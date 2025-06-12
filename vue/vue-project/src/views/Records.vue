<template>
  <div class="records-container">
    <div class="header">
      <h2>借阅记录</h2>
      <div class="search-bar">
        <el-input
          v-model="searchParams.borrower"
          placeholder="借阅人"
          clearable
          style="width: 200px; margin-right: 10px;"
          :disabled="!isAdmin"
        />
        <el-input
          v-model="searchParams.bookName"
          placeholder="图书名称"
          clearable
          style="width: 200px; margin-right: 10px;"
        />
        <el-button type="primary" @click="fetchRecords">搜索</el-button>
      </div>
    </div>

    <el-table :data="records" v-loading="loading" style="width: 100%">
      <el-table-column prop="borrower" label="借阅人" width="120" />
      <el-table-column prop="bookName" label="图书名称" />
      <el-table-column prop="bookIsbn" label="ISBN" width="160" />
      <el-table-column label="借阅时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.borrowTime) }}
        </template>
      </el-table-column>
      <el-table-column label="归还时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.remandTime) }}
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import recordApi from '@/api/record'
import dayjs from 'dayjs'

export default {
  setup() {
    const store = useStore()
    const records = ref([])
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const searchParams = ref({
      borrower: '',
      bookName: ''
    })
    
    const isAdmin = computed(() => store.state.user?.role === 'ADMIN')
    
    const formatDate = (date) => {
      if (!date) return ''
      return dayjs(date).format('YYYY-MM-DD HH:mm')
    }
    
    const fetchRecords = async () => {
      loading.value = true
      try {
        const res = await recordApi.getRecords({
          page: currentPage.value,
          pageSize: pageSize.value,
          ...searchParams.value
        })
        records.value = res.data
        total.value = res.total
      } catch (error) {
        console.error('获取借阅记录失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const handleSizeChange = (size) => {
      pageSize.value = size
      fetchRecords()
    }
    
    const handlePageChange = (page) => {
      currentPage.value = page
      fetchRecords()
    }
    
    onMounted(fetchRecords)
    
    return {
      records,
      loading,
      currentPage,
      pageSize,
      total,
      searchParams,
      isAdmin,
      formatDate,
      fetchRecords,
      handleSizeChange,
      handlePageChange
    }
  }
}
</script>

<style scoped>
.records-container {
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

.search-bar {
  display: flex;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>