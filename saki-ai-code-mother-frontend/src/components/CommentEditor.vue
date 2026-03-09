<template>
  <div class="comment-editor">
    <div v-if="replyUser" class="reply-hint">
      回复 <span class="reply-user-name">@{{ replyUser.userName }}</span>
    </div>
    <div class="editor-wrapper">
      <Toolbar
        class="toolbar"
        :editor="editorRef"
        :defaultConfig="toolbarConfig"
        mode="simple"
      />
      <Editor
        class="editor"
        v-model="content"
        :defaultConfig="editorConfig"
        mode="simple"
        @onCreated="handleCreated"
      />
    </div>
    <div class="editor-footer">
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" @click="handleSubmit" :loading="submitting" :disabled="!canSubmit">
        发表评论
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, shallowRef, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import type { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor'
import { uploadImage } from '@/api/fileController'
import type { UserVO } from '@/api/commentController'

interface Props {
  postId: string
  parentCommentId?: string
  replyUser?: UserVO | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  submit: [content: string]
  cancel: []
}>()

const content = ref<string>('')
const submitting = ref<boolean>(false)
const editorRef = shallowRef<IDomEditor>()

const canSubmit = computed(() => {
  return content.value.trim().length > 0
})

const toolbarConfig: Partial<IToolbarConfig> = {
  toolbarKeys: [
    'bold',
    'italic',
    'underline',
    '|',
    'color',
    'bgColor',
    '|',
    'uploadImage',
    '|',
    'undo',
    'redo',
  ],
}

const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入评论内容...',
  MENU_CONF: {
    uploadImage: {
      fieldName: 'file',
      async customUpload(file: File, insertFn: (url: string, alt: string, href: string) => void) {
        try {
          const res = await uploadImage(file)
          if (res.code === 0) {
            insertFn(res.data, file.name, res.data)
          } else {
            message.error('图片上传失败：' + res.message)
          }
        } catch (e: any) {
          message.error('图片上传失败：' + (e.message || '未知错误'))
        }
      },
      maxFileSize: 2 * 1024 * 1024,
      allowedFileTypes: ['image/*'],
    },
  },
}

const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor
}

const handleSubmit = () => {
  if (!canSubmit.value) {
    message.warning('请输入评论内容')
    return
  }
  emit('submit', content.value)
}

const handleCancel = () => {
  content.value = ''
  emit('cancel')
}

const clearContent = () => {
  content.value = ''
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})

defineExpose({
  clearContent,
})
</script>

<style scoped>
.comment-editor {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  margin-top: 12px;
}

.reply-hint {
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.reply-user-name {
  color: #1890ff;
  font-weight: 500;
}

.editor-wrapper {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
}

.toolbar {
  border-bottom: 1px solid #e8e8e8;
}

.editor {
  height: 250px;
  overflow-y: auto;
}

.editor-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}
</style>
