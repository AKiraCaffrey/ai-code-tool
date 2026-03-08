<template>
  <div class="user-center-page">
    <div class="user-info-section">
      <div class="avatar-wrapper" @mouseenter="showAvatarOverlay = true" @mouseleave="showAvatarOverlay = false">
        <a-avatar :src="userInfo.userAvatar" :size="120" class="user-avatar" />
        <div v-if="showAvatarOverlay" class="avatar-overlay">
          <CameraOutlined class="overlay-icon" @click.stop="triggerAvatarUpload" />
          <EyeOutlined class="overlay-icon" @click.stop="previewAvatar" />
        </div>
        <input
          ref="avatarInputRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarChange"
        />
      </div>
      
      <div class="user-name-row">
        <template v-if="editingName">
          <a-input
            v-model:value="editNameValue"
            style="width: 200px"
            :maxlength="20"
            @pressEnter="saveName"
          />
          <CheckOutlined class="edit-icon save" @click="saveName" />
          <CloseOutlined class="edit-icon cancel" @click="cancelEditName" />
        </template>
        <template v-else>
          <span class="user-name">{{ userInfo.userName || '无名' }}</span>
          <EditOutlined class="edit-icon" @click="startEditName" />
        </template>
      </div>
      
      <div class="user-profile-row">
        <template v-if="editingProfile">
          <a-textarea
            v-model:value="editProfileValue"
            style="width: 400px"
            :maxlength="200"
            :rows="3"
            show-count
          />
          <div class="profile-actions">
            <CheckOutlined class="edit-icon save" @click="saveProfile" />
            <CloseOutlined class="edit-icon cancel" @click="cancelEditProfile" />
          </div>
        </template>
        <template v-else>
          <span class="user-profile">{{ userInfo.userProfile || '暂无个人简介' }}</span>
          <EditOutlined class="edit-icon" @click="startEditProfile" />
        </template>
      </div>
    </div>

    <div class="content-section">
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="apps" tab="作品">
          <div class="apps-grid">
            <template v-if="myApps.length > 0">
              <AppCard
                v-for="app in myApps"
                :key="app.id"
                :app="app"
              />
            </template>
            <a-empty v-else description="暂无作品" />
          </div>
        </a-tab-pane>
        
        <a-tab-pane key="posts" tab="文章">
          <div class="posts-sub-tabs">
            <a-radio-group v-model:value="postSubTab" button-style="solid">
              <a-radio-button value="my">我的帖子</a-radio-button>
              <a-radio-button value="liked">我点赞的帖子</a-radio-button>
            </a-radio-group>
          </div>
          
          <div class="posts-list">
            <template v-if="currentPosts.length > 0">
              <div
                v-for="post in currentPosts"
                :key="post.id"
                class="post-card"
                @click="goToPostDetail(post.id)"
              >
                <div class="post-content">
                  <div class="post-header">
                    <div class="post-title">
                      <span class="title-text">{{ post.title }}</span>
                      <a-tag v-if="post.isTop === 1" color="#52c41a" class="top-tag">置顶</a-tag>
                    </div>
                    <div class="post-actions" @click.stop>
                      <a-button type="link" size="small" @click="editPost(post)">
                        <EditOutlined /> 编辑
                      </a-button>
                      <a-popconfirm
                        title="确定要删除这篇帖子吗？"
                        @confirm="deletePost(post.id)"
                      >
                        <a-button type="link" size="small" danger>
                          <DeleteOutlined /> 删除
                        </a-button>
                      </a-popconfirm>
                    </div>
                  </div>
                  <div class="post-summary">{{ stripHtml(post.content) }}</div>
                  <div class="post-footer">
                    <div class="post-meta">
                      <a-tag v-if="post.categoryName" color="blue" size="small">{{ post.categoryName }}</a-tag>
                      <span class="post-time">{{ formatTime(post.createTime) }}</span>
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
            <a-empty v-else :description="postSubTab === 'my' ? '暂无帖子' : '暂无点赞的帖子'" />
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>

    <a-modal
      v-model:open="avatarPreviewVisible"
      :footer="null"
      title="头像预览"
    >
      <img :src="userInfo.userAvatar" style="width: 100%" alt="头像预览" />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  EditOutlined,
  CameraOutlined,
  EyeOutlined,
  CheckOutlined,
  CloseOutlined,
  DeleteOutlined,
  LikeOutlined,
  MessageOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { updateMyUser, getMyApps, getMyPosts, getMyLikedPosts } from '@/api/userController'
import { deletePost as deletePostApi, type PostVO } from '@/api/postController'
import { uploadImage } from '@/api/fileController'
import AppCard from '@/components/AppCard.vue'
import { formatRelativeTime } from '@/utils/time'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const userInfo = computed(() => loginUserStore.loginUser)

const activeTab = ref('apps')
const postSubTab = ref('my')
const myApps = ref<API.AppVO[]>([])
const myPosts = ref<any[]>([])
const myLikedPosts = ref<any[]>([])

const editingName = ref(false)
const editNameValue = ref('')
const editingProfile = ref(false)
const editProfileValue = ref('')

const showAvatarOverlay = ref(false)
const avatarPreviewVisible = ref(false)
const avatarInputRef = ref<HTMLInputElement>()

const currentPosts = computed(() => {
  return postSubTab.value === 'my' ? myPosts.value : myLikedPosts.value
})

const formatTime = (time: string) => {
  if (!time) return ''
  return formatRelativeTime(time)
}

const stripHtml = (html: string) => {
  if (!html) return ''
  const tmp = document.createElement('div')
  tmp.innerHTML = html
  return tmp.textContent || tmp.innerText || ''
}

const loadMyApps = async () => {
  try {
    const res = await getMyApps()
    if (res.data.code === 0) {
      myApps.value = res.data.data || []
    }
  } catch (e) {
    console.error('加载应用失败', e)
  }
}

const loadMyPosts = async () => {
  try {
    const res = await getMyPosts()
    if (res.data.code === 0) {
      myPosts.value = res.data.data || []
    }
  } catch (e) {
    console.error('加载帖子失败', e)
  }
}

const loadMyLikedPosts = async () => {
  try {
    const res = await getMyLikedPosts()
    if (res.data.code === 0) {
      myLikedPosts.value = res.data.data || []
    }
  } catch (e) {
    console.error('加载点赞帖子失败', e)
  }
}

const startEditName = () => {
  editNameValue.value = userInfo.value.userName || ''
  editingName.value = true
}

const cancelEditName = () => {
  editingName.value = false
  editNameValue.value = ''
}

const saveName = async () => {
  if (!editNameValue.value.trim()) {
    message.warning('用户名不能为空')
    return
  }
  try {
    const res = await updateMyUser({ userName: editNameValue.value.trim() })
    if (res.data.code === 0) {
      loginUserStore.loginUser.userName = editNameValue.value.trim()
      message.success('用户名更新成功')
      editingName.value = false
    } else {
      message.error('更新失败：' + res.data.message)
    }
  } catch (e) {
    message.error('更新失败')
  }
}

const startEditProfile = () => {
  editProfileValue.value = userInfo.value.userProfile || ''
  editingProfile.value = true
}

const cancelEditProfile = () => {
  editingProfile.value = false
  editProfileValue.value = ''
}

const saveProfile = async () => {
  try {
    const res = await updateMyUser({ userProfile: editProfileValue.value })
    if (res.data.code === 0) {
      loginUserStore.loginUser.userProfile = editProfileValue.value
      message.success('个人简介更新成功')
      editingProfile.value = false
    } else {
      message.error('更新失败：' + res.data.message)
    }
  } catch (e) {
    message.error('更新失败')
  }
}

const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

const previewAvatar = () => {
  if (userInfo.value.userAvatar) {
    avatarPreviewVisible.value = true
  }
}

const handleAvatarChange = async (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  try {
    const res = await uploadImage(file)
    if (res.code === 0) {
      const avatarUrl = res.data
      const updateRes = await updateMyUser({ userAvatar: avatarUrl })
      if (updateRes.data.code === 0) {
        loginUserStore.loginUser.userAvatar = avatarUrl
        message.success('头像更新成功')
      } else {
        message.error('更新头像失败：' + updateRes.data.message)
      }
    } else {
      message.error('上传头像失败：' + res.message)
    }
  } catch (e) {
    message.error('上传头像失败')
  }
  target.value = ''
}

const goToPostDetail = (id: string) => {
  const url = router.resolve(`/post/${id}`).href
  window.open(url, '_blank')
}

const editPost = (post: PostVO) => {
  const url = router.resolve(`/post/edit/${post.id}`).href
  window.open(url, '_blank')
}

const deletePost = async (id: string) => {
  try {
    const res = await deletePostApi(id)
    if (res.data.code === 0) {
      message.success('删除成功')
      await loadMyPosts()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (e) {
    message.error('删除失败')
  }
}

onMounted(() => {
  loadMyApps()
  loadMyPosts()
  loadMyLikedPosts()
})
</script>

<style scoped>
.user-center-page {
  min-height: calc(100vh - 88px);
  background: #f5f5f5;
  padding: 24px;
}

.user-info-section {
  max-width: 1200px;
  margin: 0 auto 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.user-avatar {
  border: 3px solid #1890ff;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.overlay-icon {
  font-size: 24px;
  color: #fff;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.overlay-icon:hover {
  transform: scale(1.2);
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.user-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.user-profile-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  max-width: 500px;
}

.user-profile {
  font-size: 14px;
  color: #666;
  text-align: center;
}

.edit-icon {
  font-size: 16px;
  color: #1890ff;
  cursor: pointer;
  transition: color 0.3s ease;
}

.edit-icon:hover {
  color: #40a9ff;
}

.edit-icon.save {
  color: #52c41a;
}

.edit-icon.save:hover {
  color: #73d13d;
}

.edit-icon.cancel {
  color: #ff4d4f;
}

.edit-icon.cancel:hover {
  color: #ff7875;
}

.profile-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.content-section {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.posts-sub-tabs {
  margin-bottom: 16px;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.post-content {
  flex: 1;
  padding: 16px;
  min-width: 0;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.post-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.title-text {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.top-tag {
  flex-shrink: 0;
}

.post-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.post-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-stats {
  display: flex;
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
  width: 160px;
  height: 120px;
  flex-shrink: 0;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
