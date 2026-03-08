<template>
  <div class="comment-item" :class="{ 'is-reply': isReply }">
    <div class="comment-header">
      <div class="comment-user">
        <a-avatar :src="comment.user?.userAvatar" :size="40" />
        <div class="user-info">
          <div class="user-name">{{ comment.user?.userName || '匿名用户' }}</div>
          <div class="comment-time">{{ formatRelativeTime(comment.createTime) }}</div>
        </div>
      </div>
      <div class="comment-actions">
        <div class="action-btn" @click="handleReply">
          <MessageOutlined />
        </div>
        <div class="action-btn like-btn" :class="{ liked: comment.isLiked }" @click="handleLike">
          <HeartFilled v-if="comment.isLiked" />
          <HeartOutlined v-else />
          <span v-if="comment.likeCount > 0" class="like-count">{{ comment.likeCount }}</span>
        </div>
        <div v-if="canDelete" class="action-btn delete-btn" @click="handleDelete">
          <DeleteOutlined />
        </div>
      </div>
    </div>

    <div class="comment-content">
      <template v-if="comment.replyUser">
        <span class="reply-hint">回复 <span class="reply-user">@{{ comment.replyUser.userName }}</span>：</span>
      </template>
      <div class="content-html" v-html="comment.content"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { HeartOutlined, HeartFilled, MessageOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { formatRelativeTime } from '@/utils/time'
import type { CommentVO } from '@/api/commentController'
import { useLoginUserStore } from '@/stores/loginUser'

interface Props {
  comment: CommentVO
  isReply?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isReply: false
})

const emit = defineEmits<{
  reply: [comment: CommentVO]
  like: [comment: CommentVO]
  delete: [comment: CommentVO]
}>()

const loginUserStore = useLoginUserStore()

const canDelete = computed(() => {
  const loginUser = loginUserStore.loginUser
  if (!loginUser?.id) return false
  const isAdmin = loginUser.userRole === 'admin'
  const isOwner = String(loginUser.id) === props.comment.userId
  return isAdmin || isOwner
})

const handleReply = () => {
  emit('reply', props.comment)
}

const handleLike = () => {
  emit('like', props.comment)
}

const handleDelete = () => {
  emit('delete', props.comment)
}
</script>

<style scoped>
.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item.is-reply {
  padding-left: 56px;
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 16px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
  user-select: none;
}

.action-btn:hover {
  color: #1890ff;
}

.like-btn.liked {
  color: #ff4d4f;
}

.delete-btn:hover {
  color: #ff4d4f;
}

.like-count {
  font-size: 12px;
  font-weight: 500;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.reply-hint {
  color: #999;
  font-size: 14px;
}

.reply-user {
  color: #1890ff;
  cursor: pointer;
}

.reply-user:hover {
  color: #40a9ff;
}

.content-html {
  margin-top: 4px;
}

.content-html :deep(img) {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 4px;
  margin: 8px 0;
}

.content-html :deep(p) {
  margin-bottom: 8px;
}

.content-html :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}
</style>
