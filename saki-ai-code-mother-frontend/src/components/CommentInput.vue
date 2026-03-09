<template>
  <div class="comment-input">
    <template v-if="!isLoggedIn">
      <div class="input-placeholder blur" @click="handleClick">
        <div class="blur-overlay">
          <LockOutlined class="lock-icon" />
          <span class="blur-text">登录后可操作</span>
        </div>
      </div>
    </template>
    <template v-else>
      <div v-if="!showEditor" class="input-placeholder" @click="showEditor = true">
        <EditOutlined class="edit-icon" />
        <span class="placeholder-text">写下你的评论...</span>
      </div>
      <CommentEditor
        v-else
        :post-id="postId"
        @submit="handleSubmit"
        @cancel="handleCancel"
        ref="editorRef"
        :submitting="submitting"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { LockOutlined, EditOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import CommentEditor from './CommentEditor.vue'
import { createComment } from '@/api/commentController'

interface Props {
  postId: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  submit: []
}>()

const loginUserStore = useLoginUserStore()
const showEditor = ref<boolean>(false)
const editorRef = ref()
const submitting = ref<boolean>(false)

const isLoggedIn = computed(() => {
  return !!loginUserStore.loginUser?.id
})

const handleClick = () => {
  if (!isLoggedIn.value) {
    message.warning('请先登录')
  }
}

const handleSubmit = async (content: string) => {
  submitting.value = true
  try {
    const res = await createComment({
      postId: props.postId,
      content: content
    })
    if (res.code === 0) {
      message.success('评论成功')
      showEditor.value = false
      emit('submit')
    } else {
      message.error('评论失败：' + res.message)
    }
  } catch (e: any) {
    message.error('评论失败：' + (e.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  showEditor.value = false
}
</script>

<style scoped>
.comment-input {
  margin-bottom: 24px;
}

.input-placeholder {
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 12px;
}

.input-placeholder:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
}

.input-placeholder.blur {
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.blur-overlay {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #999;
}

.lock-icon {
  font-size: 20px;
}

.blur-text {
  font-size: 14px;
}

.edit-icon {
  font-size: 18px;
  color: #999;
}

.placeholder-text {
  font-size: 14px;
  color: #999;
}
</style>
