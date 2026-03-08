<template>
  <div class="post-detail-page">
    <div v-if="loading" class="page-loading">
      <a-spin size="large" />
    </div>

    <div v-else-if="postDetail" class="post-detail-container">
      <div class="post-title">{{ postDetail.title }}</div>

      <div class="post-user-info">
        <template v-if="isLoggedIn">
          <div class="user-avatar">
            <a-avatar :src="postDetail.user?.userAvatar" :size="48" />
          </div>
          <div class="user-info-right">
            <div class="user-name">{{ postDetail.user?.userName || '匿名用户' }}</div>
            <div class="user-meta">
              <span class="post-time">{{ formatDate(postDetail.createTime) }}</span>
              <a-tag v-if="postDetail.categoryName" color="blue">{{ postDetail.categoryName }}</a-tag>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="user-info-blur">
            <div class="blur-content">
              <div class="blur-avatar">
                <LockOutlined />
              </div>
              <div class="blur-text">登录后可见</div>
            </div>
          </div>
        </template>
      </div>

      <div class="post-content" v-html="postDetail.content"></div>

      <div class="post-actions">
        <div v-if="canEditOrDelete" class="action-button edit-button" @click="handleEdit">
          <EditOutlined />
          <span>编辑</span>
        </div>
        <div class="like-button" :class="{ liked: postDetail.isLiked }" @click="handleLike">
          <HeartFilled v-if="postDetail.isLiked" />
          <HeartOutlined v-else />
          <span class="like-count">{{ postDetail.likeCount || 0 }}</span>
        </div>
        <div v-if="canEditOrDelete" class="action-button delete-button" @click="handleDelete">
          <DeleteOutlined />
          <span>删除</span>
        </div>
      </div>
    </div>

    <a-empty v-else description="帖子不存在" />

    <div v-if="postDetail" class="comment-section">
      <CommentList :post-id="postDetail.id" ref="commentListRef" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { HeartOutlined, HeartFilled, LockOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { getPostDetail, likePost, unlikePost, deletePost, type PostDetailVO } from '@/api/postController'
import { useLoginUserStore } from '@/stores/loginUser'
import CommentList from '@/components/CommentList.vue'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const postDetail = ref<PostDetailVO | null>(null)
const loading = ref<boolean>(true)
const liking = ref<boolean>(false)
const deleting = ref<boolean>(false)
const commentListRef = ref()

const isLoggedIn = computed(() => {
  return !!loginUserStore.loginUser?.id
})

const canEditOrDelete = computed(() => {
  if (!isLoggedIn.value || !postDetail.value) return false
  const loginUserId = String(loginUserStore.loginUser?.id)
  const postUserId = postDetail.value.userId
  const isAdmin = loginUserStore.loginUser?.userRole === 'admin'
  const isOwner = loginUserId === postUserId
  return isAdmin || isOwner
})

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const loadPostDetail = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('帖子ID无效')
    return
  }

  loading.value = true
  try {
    const res = await getPostDetail(id)
    if (res.code === 0) {
      postDetail.value = res.data
    } else {
      message.error('获取帖子详情失败：' + res.message)
    }
  } catch (e: any) {
    message.error('获取帖子详情失败：' + (e.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  if (!isLoggedIn.value) {
    message.warning('请先登录')
    return
  }

  if (!postDetail.value || liking.value) return

  liking.value = true
  try {
    if (postDetail.value.isLiked) {
      const res = await unlikePost({ postId: postDetail.value.id })
      if (res.code === 0) {
        postDetail.value.isLiked = false
        postDetail.value.likeCount = Math.max(0, (postDetail.value.likeCount || 0) - 1)
      } else {
        message.error('取消点赞失败：' + res.message)
      }
    } else {
      const res = await likePost({ postId: postDetail.value.id })
      if (res.code === 0) {
        postDetail.value.isLiked = true
        postDetail.value.likeCount = (postDetail.value.likeCount || 0) + 1
      } else {
        message.error('点赞失败：' + res.message)
      }
    }
  } catch (e: any) {
    message.error('操作失败：' + (e.message || '未知错误'))
  } finally {
    liking.value = false
  }
}

const handleEdit = () => {
  if (!postDetail.value) return
  const url = router.resolve(`/post/edit/${postDetail.value.id}`).href
  window.open(url, '_blank')
}

const handleDelete = () => {
  if (!postDetail.value || deleting.value) return

  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这篇帖子吗？删除后无法恢复。',
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      deleting.value = true
      try {
        const res = await deletePost(postDetail.value!.id)
        if (res.code === 0) {
          message.success('删除成功')
          router.push('/community')
        } else {
          message.error('删除失败：' + res.message)
        }
      } catch (e: any) {
        message.error('删除失败：' + (e.message || '未知错误'))
      } finally {
        deleting.value = false
      }
    }
  })
}

onMounted(() => {
  loadPostDetail()
})
</script>

<style scoped>
.post-detail-page {
  min-height: calc(100vh - 88px);
  background: #f5f5f5;
  padding-top: 24px;
  padding-bottom: 24px;
}

.page-loading {
  min-height: calc(100vh - 88px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.post-detail-container {
  max-width: 1450px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 32px;
}

.post-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  line-height: 1.4;
}

.post-user-info {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.user-avatar {
  margin-right: 16px;
}

.user-info-right {
  flex: 1;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.post-time {
  font-size: 14px;
  color: #999;
}

.user-info-blur {
  width: 100%;
  height: 60px;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.blur-content {
  position: absolute;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
}

.blur-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #999;
}

.blur-text {
  font-size: 14px;
  color: #999;
}

.post-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 48px;
}

.post-content :deep(img) {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 4px;
  margin: 16px 0;
}

.post-content :deep(p) {
  margin-bottom: 16px;
}

.post-content :deep(h1),
.post-content :deep(h2),
.post-content :deep(h3) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
}

.post-content :deep(ul),
.post-content :deep(ol) {
  padding-left: 24px;
  margin-bottom: 16px;
}

.post-content :deep(blockquote) {
  border-left: 4px solid #1890ff;
  padding-left: 16px;
  margin: 16px 0;
  color: #666;
}

.post-content :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}

.post-content :deep(pre) {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 16px 0;
}

.post-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding-top: 24px;
}

.like-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  border: 2px solid #d9d9d9;
  border-radius: 24px;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.3s ease;
  user-select: none;
}

.like-button:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.like-button.liked {
  border-color: #ff4d4f;
  background: #fff1f0;
  color: #ff4d4f;
}

.like-button.liked:hover {
  background: #ffccc7;
}

.like-count {
  font-weight: 500;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  border: 2px solid #d9d9d9;
  border-radius: 24px;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.3s ease;
  user-select: none;
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.edit-button:hover {
  border-color: #1890ff;
  color: #1890ff;
  background: #e6f7ff;
}

.delete-button:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
  background: #fff1f0;
}

.comment-section {
  max-width: 1450px;
  margin: 24px auto 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 32px;
}
</style>
