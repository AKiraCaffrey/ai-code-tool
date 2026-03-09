<template>
  <div class="post-edit-page">
    <div class="nav-container">
      <div class="nav-content">
        <div class="nav-left">
          <img src="@/assets/ZeroCode-TextAndLogo.png" alt="Logo" class="logo" @click="goBack" />
        </div>
        <div class="nav-right">
          <a-button @click="handlePreview" :disabled="!canPreview">预览</a-button>
          <a-button type="primary" @click="handleUpdate" :loading="updating" :disabled="!canPublish">保存</a-button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="page-loading">
      <a-spin size="large" />
    </div>

    <div v-else class="editor-container">
      <div class="editor-content">
        <div class="title-input">
          <a-input
            v-model:value="title"
            placeholder="请输入标题..."
            :bordered="false"
            :maxlength="512"
            show-count
          />
        </div>

        <div class="category-select">
          <a-select
            v-model:value="categoryId"
            placeholder="选择分类"
            style="width: 200px"
          >
            <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </div>

        <div class="editor-wrapper">
          <Toolbar
            class="toolbar"
            :editor="editorRef"
            :defaultConfig="toolbarConfig"
            mode="default"
          />
          <Editor
            class="editor"
            v-model="content"
            :defaultConfig="editorConfig"
            mode="default"
            @onCreated="handleCreated"
          />
        </div>
      </div>
    </div>

    <a-modal
      v-model:open="previewVisible"
      title="预览"
      :footer="null"
      width="800px"
    >
      <div class="preview-content">
        <h1 class="preview-title">{{ title || '无标题' }}</h1>
        <div class="preview-html" v-html="content"></div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, shallowRef, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import type { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor'
import { getPostDetail, updatePost, type PostDetailVO } from '@/api/postController'
import { listPostCategory, type PostCategoryVO } from '@/api/postCategoryController'
import { uploadImage } from '@/api/fileController'

const router = useRouter()
const route = useRoute()

const postId = ref<string>('')
const title = ref<string>('')
const content = ref<string>('')
const categoryId = ref<string | undefined>(undefined)
const categories = ref<PostCategoryVO[]>([])
const loading = ref<boolean>(true)
const updating = ref<boolean>(false)
const previewVisible = ref<boolean>(false)

const editorRef = shallowRef<IDomEditor>()

const canPreview = computed(() => {
  return title.value.trim() || content.value.trim()
})

const canPublish = computed(() => {
  return title.value.trim() && content.value.trim() && categoryId.value
})

const toolbarConfig: Partial<IToolbarConfig> = {
  toolbarKeys: [
    'headerSelect',
    '|',
    'bold',
    'italic',
    'underline',
    'through',
    '|',
    'color',
    'bgColor',
    'fontSize',
    'fontFamily',
    '|',
    'bulletedList',
    'numberedList',
    'lineHeight',
    '|',
    'justifyLeft',
    'justifyCenter',
    'justifyRight',
    '|',
    'uploadImage',
    'insertLink',
    '|',
    'blockquote',
    'codeBlock',
    'divider',
    '|',
    'undo',
    'redo',
  ],
}

const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入内容...',
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
      maxFileSize: 5 * 1024 * 1024,
      allowedFileTypes: ['image/*'],
    },
    color: {
      colors: [
        '#000000', '#333333', '#666666', '#999999', '#cccccc',
        '#ff0000', '#ff6600', '#ff9900', '#ffcc00', '#ffff00',
        '#00ff00', '#00ff99', '#00ffff', '#0099ff', '#0000ff',
        '#9900ff', '#ff00ff', '#ff0099', '#ff0066',
      ],
    },
    bgColor: {
      colors: [
        '#ffffff', '#f5f5f5', '#eeeeee', '#e0e0e0', '#cccccc',
        '#ffcccc', '#ffe6cc', '#ffffcc', '#ccffcc', '#ccffff',
        '#cce6ff', '#ccccff', '#e6ccff', '#ffccff', '#ffcccc',
      ],
    },
    fontSize: {
      fontSizeList: ['12px', '14px', '16px', '18px', '20px', '24px', '28px', '32px'],
    },
    fontFamily: {
      fontFamilyList: [
        '黑体',
        '楷体',
        '仿宋',
        '宋体',
        'Arial',
        'Tahoma',
        'Verdana',
      ],
    },
    lineHeight: {
      lineHeightList: ['1', '1.5', '2', '2.5', '3'],
    },
  },
}

const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor
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

const loadPostDetail = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('帖子ID无效')
    router.push('/community')
    return
  }

  postId.value = id
  loading.value = true

  try {
    const res = await getPostDetail(id)
    if (res.code === 0 && res.data) {
      const post: PostDetailVO = res.data
      title.value = post.title
      content.value = post.content
      categoryId.value = post.categoryId
    } else {
      message.error('获取帖子详情失败：' + res.message)
      router.push('/community')
    }
  } catch (e: any) {
    message.error('获取帖子详情失败：' + (e.message || '未知错误'))
    router.push('/community')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const handlePreview = () => {
  previewVisible.value = true
}

const handleUpdate = async () => {
  if (!canPublish.value) {
    message.warning('请填写标题、内容并选择分类')
    return
  }

  updating.value = true
  try {
    const res = await updatePost({
      id: postId.value,
      title: title.value.trim(),
      content: content.value,
      categoryId: categoryId.value!,
    })

    if (res.code === 0) {
      message.success('更新成功')
      router.push(`/post/${postId.value}`)
    } else {
      message.error('更新失败：' + res.message)
    }
  } catch (e: any) {
    message.error('更新失败：' + (e.message || '未知错误'))
  } finally {
    updating.value = false
  }
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})

loadCategories()
loadPostDetail()
</script>

<style scoped>
.post-edit-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.nav-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 72px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.nav-content {
  max-width: 1450px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  height: 200px;
  cursor: pointer;
  margin-left: -23px;
}

.nav-right {
  display: flex;
  gap: 12px;
}

.page-loading {
  padding-top: 120px;
  display: flex;
  justify-content: center;
}

.editor-container {
  padding-top: 88px;
  min-height: 100vh;
}

.editor-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.title-input {
  margin-bottom: 16px;
}

.title-input :deep(.ant-input) {
  font-size: 24px;
  font-weight: 600;
  padding: 12px 0;
}

.title-input :deep(.ant-input:focus) {
  box-shadow: none;
}

.category-select {
  margin-bottom: 16px;
}

.editor-wrapper {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
}

.toolbar {
  border-bottom: 1px solid #e8e8e8;
}

.editor {
  min-height: calc(100vh - 300px);
  max-height: calc(100vh - 300px);
  overflow-y: auto;
}

.preview-content {
  padding: 24px;
}

.preview-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
}

.preview-html {
  line-height: 1.8;
}

.preview-html :deep(img) {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
}
</style>
