<template>
  <div class="comment-list">
    <div class="comment-header">
      <div class="header-left">
        <span class="comment-title">评论 · {{ comments.length }}</span>
      </div>
      <div class="header-right">
        <a-radio-group v-model:value="sortType" @change="handleSortChange">
          <a-radio-button value="latest">最新</a-radio-button>
          <a-radio-button value="hot">最热</a-radio-button>
        </a-radio-group>
      </div>
    </div>

    <CommentInput :post-id="postId" @submit="handleCommentSubmit" />

    <div v-if="loading" class="loading-container">
      <a-spin />
    </div>

    <div v-else-if="comments.length === 0" class="empty-comments">
      <a-empty description="暂无评论，快来发表第一条评论吧！" />
    </div>

    <div v-else class="comments-container">
      <div v-for="comment in comments" :key="comment.id" class="comment-wrapper">
        <CommentItem
          :comment="comment"
          @reply="handleReply"
          @like="handleLike"
          @delete="handleDelete"
        />

        <div v-if="comment.replyCount && comment.replyCount > 0" class="reply-toggle">
          <a-button type="link" @click="toggleReplies(comment)" :loading="loadingReplies[comment.id]">
            <template v-if="!expandedComments[comment.id]">
              <DownOutlined /> 有 {{ comment.replyCount }} 条回复
            </template>
            <template v-else>
              <UpOutlined /> 收起回复
            </template>
          </a-button>
        </div>

        <div v-if="expandedComments[comment.id]" class="replies-container">
          <div v-for="reply in replies[comment.id]" :key="reply.id">
            <CommentItem
              :comment="reply"
              :is-reply="true"
              @reply="handleReply"
              @like="handleLike"
              @delete="handleDelete"
            />
            <CommentEditor
              v-if="activeReplyId === reply.id"
              :post-id="postId"
              :parent-comment-id="comment.id"
              :reply-user="replyTargetUser"
              @submit="(content) => handleSubmitReply(reply.id, content)"
              @cancel="handleCancelReply"
            />
          </div>
        </div>

        <CommentEditor
          v-if="activeReplyId === comment.id"
          :post-id="postId"
          :parent-comment-id="comment.id"
          :reply-user="replyTargetUser"
          @submit="(content) => handleSubmitReply(comment.id, content)"
          @cancel="handleCancelReply"
          ref="commentEditorRef"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DownOutlined, UpOutlined } from '@ant-design/icons-vue'
import CommentItem from './CommentItem.vue'
import CommentEditor from './CommentEditor.vue'
import CommentInput from './CommentInput.vue'
import {
  getCommentsByPostId,
  getRepliesByCommentId,
  createComment,
  deleteComment,
  type CommentVO
} from '@/api/commentController'
import { likeComment, unlikeComment } from '@/api/commentLikeController'
import { useLoginUserStore } from '@/stores/loginUser'

interface Props {
  postId: string
}

const props = defineProps<Props>()

const loginUserStore = useLoginUserStore()

const comments = ref<CommentVO[]>([])
const loading = ref<boolean>(false)
const replies = ref<Record<string, CommentVO[]>>({})
const expandedComments = ref<Record<string, boolean>>({})
const loadingReplies = ref<Record<string, boolean>>({})
const activeReplyId = ref<string | null>(null)
const replyTargetUser = ref<CommentVO['user'] | null>(null)
const commentEditorRef = ref()
const sortType = ref<string>('latest')

const loadComments = async () => {
  loading.value = true
  try {
    const res = await getCommentsByPostId(props.postId, sortType.value)
    if (res.code === 0) {
      comments.value = res.data || []
    } else {
      message.error('获取评论失败：' + res.message)
    }
  } catch (e: any) {
    message.error('获取评论失败：' + (e.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const handleSortChange = () => {
  loadComments()
}

const handleCommentSubmit = () => {
  loadComments()
}

const toggleReplies = async (comment: CommentVO) => {
  if (expandedComments.value[comment.id]) {
    expandedComments.value[comment.id] = false
  } else {
    if (!replies.value[comment.id]) {
      loadingReplies.value[comment.id] = true
      try {
        const res = await getRepliesByCommentId(comment.id)
        if (res.code === 0) {
          replies.value[comment.id] = res.data || []
        } else {
          message.error('获取回复失败：' + res.message)
        }
      } catch (e: any) {
        message.error('获取回复失败：' + (e.message || '未知错误'))
      } finally {
        loadingReplies.value[comment.id] = false
      }
    }
    expandedComments.value[comment.id] = true
  }
}

const handleReply = (comment: CommentVO) => {
  if (!loginUserStore.loginUser?.id) {
    message.warning('请先登录')
    return
  }
  activeReplyId.value = comment.id
  replyTargetUser.value = comment.user
}

const handleCancelReply = () => {
  activeReplyId.value = null
  replyTargetUser.value = null
}

const handleSubmitReply = async (commentId: string, content: string) => {
  try {
    let parentCommentId = commentId
    for (const [parentId, repliesList] of Object.entries(replies.value)) {
      const found = repliesList.find(r => r.id === commentId)
      if (found) {
        parentCommentId = parentId
        break
      }
    }

    const res = await createComment({
      postId: props.postId,
      parentCommentId: parentCommentId,
      replyUserId: replyTargetUser.value?.id,
      content: content
    })
    if (res.code === 0) {
      message.success('评论成功')
      activeReplyId.value = null
      replyTargetUser.value = null
      await loadComments()
      if (expandedComments.value[parentCommentId]) {
        const replyRes = await getRepliesByCommentId(parentCommentId)
        if (replyRes.code === 0) {
          replies.value[parentCommentId] = replyRes.data || []
        }
      }
    } else {
      message.error('评论失败：' + res.message)
    }
  } catch (e: any) {
    message.error('评论失败：' + (e.message || '未知错误'))
  }
}

const handleLike = async (comment: CommentVO) => {
  if (!loginUserStore.loginUser?.id) {
    message.warning('请先登录')
    return
  }

  try {
    if (comment.isLiked) {
      const res = await unlikeComment({ commentId: comment.id })
      if (res.code === 0) {
        comment.isLiked = false
        comment.likeCount = Math.max(0, comment.likeCount - 1)
      } else {
        message.error('取消点赞失败：' + res.message)
      }
    } else {
      const res = await likeComment({ commentId: comment.id })
      if (res.code === 0) {
        comment.isLiked = true
        comment.likeCount = comment.likeCount + 1
      } else {
        message.error('点赞失败：' + res.message)
      }
    }
  } catch (e: any) {
    message.error('操作失败：' + (e.message || '未知错误'))
  }
}

const handleDelete = (comment: CommentVO) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这条评论吗？',
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await deleteComment(comment.id)
        if (res.code === 0) {
          message.success('删除成功')
          await loadComments()
          for (const parentId of Object.keys(expandedComments.value)) {
            if (expandedComments.value[parentId]) {
              const replyRes = await getRepliesByCommentId(parentId)
              if (replyRes.code === 0) {
                replies.value[parentId] = replyRes.data || []
              }
            }
          }
        } else {
          message.error('删除失败：' + res.message)
        }
      } catch (e: any) {
        message.error('删除失败：' + (e.message || '未知错误'))
      }
    }
  })
}

onMounted(() => {
  loadComments()
})

defineExpose({
  loadComments
})
</script>

<style scoped>
.comment-list {
  margin-top: 24px;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  flex: 1;
}

.comment-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.empty-comments {
  padding: 40px 0;
}

.comments-container {
  background: #fff;
  border-radius: 8px;
  padding: 0 24px;
}

.comment-wrapper {
  border-bottom: 1px solid #f0f0f0;
}

.comment-wrapper:last-child {
  border-bottom: none;
}

.reply-toggle {
  padding-left: 56px;
  margin-top: -8px;
  margin-bottom: 8px;
}

.replies-container {
  padding-left: 0;
  margin-top: -8px;
  background: #fafafa;
  border-radius: 8px;
  padding: 0 16px;
  margin-left: 56px;
}
</style>
