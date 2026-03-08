<template>
  <div id="postManagePage">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="帖子ID">
        <a-input v-model:value="searchParams.id" placeholder="输入帖子ID" style="width: 150px" />
      </a-form-item>
      <a-form-item label="标题">
        <a-input v-model:value="searchParams.title" placeholder="输入标题关键词" style="width: 200px" />
      </a-form-item>
      <a-form-item label="用户ID">
        <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" style="width: 150px" />
      </a-form-item>
      <a-form-item label="置顶状态">
        <a-select v-model:value="searchParams.isTop" placeholder="全部" style="width: 120px">
          <a-select-option :value="undefined">全部</a-select-option>
          <a-select-option :value="1">已置顶</a-select-option>
          <a-select-option :value="0">未置顶</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />

    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
      :scroll="{ x: 1400 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'title'">
          <div class="title-cell">
            <a-tag v-if="record.isTop === 1" color="red">置顶</a-tag>
            <a-tooltip :title="record.title">
              <span class="title-text">{{ record.title }}</span>
            </a-tooltip>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'user'">
          <div class="user-cell">
            <a-avatar :src="record.user?.userAvatar" :size="24" />
            <span>{{ record.user?.userName || '匿名用户' }}</span>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'isTop'">
          <a-tag :color="record.isTop === 1 ? 'red' : 'default'">
            {{ record.isTop === 1 ? '已置顶' : '未置顶' }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button
              type="default"
              size="small"
              @click="toggleTop(record)"
              :class="{ 'top-btn': record.isTop === 1 }"
            >
              {{ record.isTop === 1 ? '取消置顶' : '置顶' }}
            </a-button>
            <a-button type="primary" size="small" @click="viewPost(record)">查看</a-button>
            <a-popconfirm title="确定要删除这篇帖子吗？" @confirm="deletePostById(record.id)">
              <a-button danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listPostByPage, setPostTop, deletePost, type PostVO, type PostQueryRequest } from '@/api/postController'
import { formatTime } from '@/utils/time'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 100,
    fixed: 'left',
  },
  {
    title: '标题',
    dataIndex: 'title',
    width: 300,
  },
  {
    title: '作者',
    dataIndex: 'user',
    width: 150,
  },
  {
    title: '分类',
    dataIndex: 'categoryName',
    width: 100,
  },
  {
    title: '浏览',
    dataIndex: 'viewCount',
    width: 80,
  },
  {
    title: '点赞',
    dataIndex: 'likeCount',
    width: 80,
  },
  {
    title: '评论',
    dataIndex: 'commentCount',
    width: 80,
  },
  {
    title: '置顶',
    dataIndex: 'isTop',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

const data = ref<PostVO[]>([])
const total = ref(0)

const searchParams = reactive<PostQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

const fetchData = async () => {
  try {
    const res = await listPostByPage({
      ...searchParams,
    })
    if (res.code === 0) {
      data.value = res.data?.records ?? []
      total.value = res.data?.totalRow ?? 0
    } else {
      message.error('获取数据失败：' + res.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

onMounted(() => {
  fetchData()
})

const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

const toggleTop = async (post: PostVO) => {
  if (!post.id) return

  const newIsTop = post.isTop === 1 ? 0 : 1

  try {
    const res = await setPostTop({
      id: post.id,
      isTop: newIsTop,
    })

    if (res.code === 0) {
      message.success(newIsTop === 1 ? '已置顶' : '已取消置顶')
      fetchData()
    } else {
      message.error('操作失败：' + res.message)
    }
  } catch (error) {
    console.error('操作失败：', error)
    message.error('操作失败')
  }
}

const viewPost = (post: PostVO) => {
  const url = router.resolve(`/post/${post.id}`).href
  window.open(url, '_blank')
}

const deletePostById = async (id: string | undefined) => {
  if (!id) return

  try {
    const res = await deletePost(id)
    if (res.code === 0) {
      message.success('删除成功')
      fetchData()
    } else {
      message.error('删除失败：' + res.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
#postManagePage {
  padding: 24px;
  background: white;
  margin-top: 16px;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.top-btn {
  background: #ff4d4f;
  border-color: #ff4d4f;
  color: white;
}

.top-btn:hover {
  background: #ff7875;
  border-color: #ff7875;
}

:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
}
</style>
