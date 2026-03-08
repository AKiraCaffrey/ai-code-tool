<template>
  <div class="community-page">
    <div class="community-container">
      <div class="community-header">
        <div class="sort-tabs">
          <a-radio-group v-model:value="sortType" button-style="solid" @change="handleSortChange">
            <a-radio-button value="latest">最新</a-radio-button>
            <a-radio-button value="hot">最热</a-radio-button>
          </a-radio-group>
        </div>
        <div class="search-box">
          <a-input-search
            v-model:value="keyword"
            placeholder="搜索帖子标题"
            enter-button
            allow-clear
            @search="handleSearch"
            @pressEnter="handleSearch"
            style="width: 300px"
          />
        </div>
        <div class="create-post-btn">
          <a-button type="primary" @click="goToCreatePost">
            <template #icon><PlusOutlined /></template>
            发帖
          </a-button>
        </div>
        <div class="category-select">
          <a-select
            v-model:value="selectedCategoryId"
            placeholder="全部分类"
            allow-clear
            style="width: 150px"
            @change="handleCategoryChange"
          >
            <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </div>
      </div>

      <div class="post-list-wrapper">
        <div class="post-list">
          <template v-if="posts.length > 0">
            <div
              v-for="post in posts"
              :key="post.id"
              class="post-card"
              @click="goToPostDetail(post.id)"
            >
              <div class="post-content" :class="{ 'full-width': !post.firstImageUrl }">
                <div class="post-title">
                  <span class="title-text">{{ post.title }}</span>
                  <a-tag v-if="post.isTop === 1" color="#52c41a" class="top-tag">置顶</a-tag>
                </div>
                <div class="post-summary">{{ post.content }}</div>
                <div class="post-footer">
                  <div class="post-meta">
                    <a-avatar :src="post.user?.userAvatar" :size="24" />
                    <span class="user-name">{{ post.user?.userName || '匿名用户' }}</span>
                    <span class="post-time">{{ formatTime(post.createTime) }}</span>
                    <a-tag v-if="post.categoryName" color="blue" size="small">{{ post.categoryName }}</a-tag>
                  </div>
                  <div class="post-stats">
                    <span class="stat-item">
                      <LikeOutlined />
                      {{ post.likeCount || 0 }}
                    </span>
                    <span class="stat-item">
                      <MessageOutlined />
                      {{ post.commentCount || 0 }}
                    </span>
                  </div>
                </div>
              </div>
              <div v-if="post.firstImageUrl" class="post-image">
                <img :src="post.firstImageUrl" alt="帖子首图" />
              </div>
            </div>
          </template>

          <a-empty v-else-if="!loading" description="暂无帖子" class="empty-state" />

          <div v-if="loading" class="loading-more">
            <a-spin />
            <span>加载中...</span>
          </div>

          <div v-if="!hasMore && posts.length > 0" class="no-more">
            没有更多了
          </div>
        </div>

        <div ref="loadMoreTrigger" class="load-more-trigger"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { LikeOutlined, MessageOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { getPostCursorPage, type PostVO } from '@/api/postController'
import { listPostCategory, type PostCategoryVO } from '@/api/postCategoryController'
import { formatRelativeTime } from '@/utils/time'

const router = useRouter()

const posts = ref<PostVO[]>([])
const categories = ref<PostCategoryVO[]>([])
const sortType = ref<string>('latest')
const keyword = ref<string>('')
const selectedCategoryId = ref<number | undefined>(undefined)
const cursor = ref<string | undefined>(undefined)
const hasMore = ref<boolean>(true)
const loading = ref<boolean>(false)

const loadMoreTrigger = ref<HTMLElement | null>(null)
let observer: IntersectionObserver | null = null

const formatTime = (time: string) => {
  if (!time) return ''
  return formatRelativeTime(time)
}

const loadCategories = async () => {
  try {
    const res = await listPostCategory()
    if (res.code === 0) {
      categories.value = res.data || []
    }
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

const loadPosts = async (isRefresh = false) => {
  if (loading.value) return
  if (!isRefresh && !hasMore.value) return

  loading.value = true

  if (isRefresh) {
    cursor.value = undefined
    posts.value = []
    hasMore.value = true
  }

  try {
    const res = await getPostCursorPage({
      cursor: cursor.value,
      pageSize: 10,
      categoryId: selectedCategoryId.value,
      keyword: keyword.value || undefined,
      sortType: sortType.value,
    })

    if (res.code === 0) {
      const data = res.data
      if (isRefresh) {
        posts.value = data.records || []
      } else {
        posts.value = [...posts.value, ...(data.records || [])]
      }
      cursor.value = data.nextCursor
      hasMore.value = data.hasMore
    }
  } catch (e) {
    console.error('加载帖子失败', e)
  } finally {
    loading.value = false
  }
}

const handleSortChange = () => {
  loadPosts(true)
}

const handleSearch = () => {
  loadPosts(true)
}

const handleCategoryChange = () => {
  loadPosts(true)
}

const goToPostDetail = (id: string) => {
  const url = router.resolve(`/post/${id}`).href
  window.open(url, '_blank')
}

const goToCreatePost = () => {
  const url = router.resolve('/post/create').href
  window.open(url, '_blank')
}

const setupIntersectionObserver = () => {
  if (observer) {
    observer.disconnect()
  }

  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].isIntersecting && hasMore.value && !loading.value) {
        loadPosts()
      }
    },
    { threshold: 0.1 }
  )

  nextTick(() => {
    if (loadMoreTrigger.value) {
      observer?.observe(loadMoreTrigger.value)
    }
  })
}

onMounted(async () => {
  await loadCategories()
  await loadPosts(true)
  setupIntersectionObserver()
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
.community-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 88px;
}

.community-container {
  max-width: 1450px;
  margin: 0 auto;
  padding: 24px;
  position: relative;
}

.community-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 16px 24px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.sort-tabs {
  flex-shrink: 0;
}

.search-box {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 24px;
}

.category-select {
  flex-shrink: 0;
  margin-left: 16px;
}

.create-post-btn {
  flex-shrink: 0;
}

.post-list-wrapper {
  position: relative;
}

.post-list {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.post-card {
  display: flex;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 140px;
}

.post-card:hover {
  background: #fafafa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.post-card:last-child {
  border-bottom: none;
}

.post-content {
  flex: 4;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.post-content.full-width {
  flex: 1;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.top-tag {
  flex-shrink: 0;
  font-size: 12px;
}

.post-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 16px;
}

.post-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
}

.post-image {
  flex: 1;
  margin-left: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 200px;
}

.post-image img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.empty-state {
  padding: 80px 0;
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px;
  color: #999;
}

.no-more {
  text-align: center;
  padding: 24px;
  color: #999;
  font-size: 14px;
}

.load-more-trigger {
  height: 1px;
}
</style>
