<template>
  <a-layout-header 
    class="header" 
    :class="{ scrolled: isScrolled }"
  >
    <div class="header-content">
      <div class="header-left">
        <RouterLink to="/" class="logo-wrapper">
          <img class="logo" src="@/assets/ZeroCode-TextAndLogo.png" alt="Logo" />
        </RouterLink>
        
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
          class="nav-menu"
          :disabledOverflow="true"
        />
      </div>
      
      <div class="header-right">
        <div v-if="loginUserStore.loginUser.id" class="user-info">
          <a-dropdown>
            <a-space class="user-avatar">
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              <span class="user-name">{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="goToUserCenter">
                  <IdcardOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <a-button v-else type="primary" href="/user/login">登录</a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined, UserOutlined, AppstoreOutlined, MessageOutlined, FileTextOutlined, IdcardOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()

const selectedKeys = ref<string[]>(['/'])
const isScrolled = ref(false)

router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

const handleScroll = () => {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/community',
    icon: () => h(MessageOutlined),
    label: '交流社区',
    title: '交流社区',
  },
  {
    key: '/admin/userManage',
    icon: () => h(UserOutlined),
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    icon: () => h(AppstoreOutlined),
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: '/admin/postManage',
    icon: () => h(FileTextOutlined),
    label: '帖子管理',
    title: '帖子管理',
  },
]

const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  if (key.startsWith('/')) {
    router.push(key)
  }
}

const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}

const goToUserCenter = () => {
  const url = router.resolve('/user/center').href
  window.open(url, '_blank')
}
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 72px;
  padding: 0;
  background: rgba(255, 255, 255, 0.01);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease, background 0.3s ease;
}

.header.scrolled {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1450px;
  margin: 0 auto;
  padding: 0 -32px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.logo {
  height: 150px;
  width: auto;
  margin-right: -60px;
}

.nav-menu {
  border-bottom: none !important;
  background: transparent !important;
}

:deep(.ant-menu-horizontal) {
  border-bottom: none !important;
  line-height: 70px;
}

:deep(.ant-menu-horizontal > .ant-menu-item) {
  border-bottom: none !important;
  transition: all 0.3s ease;
  border-radius: 4px;
  margin: 0 4px;
  color: rgba(0, 0, 0, 0.88);
}

:deep(.ant-menu-horizontal > .ant-menu-item::after) {
  border-bottom: none !important;
}

:deep(.ant-menu-horizontal > .ant-menu-item:hover:not(.ant-menu-item-selected)) {
  color: rgba(0, 0, 0, 0.65) !important;
}

:deep(.ant-menu-horizontal > .ant-menu-item-selected) {
  color: #1890ff !important;
}

:deep(.ant-menu-horizontal > .ant-menu-item-selected:hover) {
  color: #1890ff !important;
}

:deep(.ant-menu-horizontal > .ant-menu-item-selected::after) {
  border-bottom: 2px solid #1890ff !important;
}

.header-right {
  display: flex;
  align-items: center;
  height: 100%;
}

.user-avatar {
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: color 0.3s ease;
}

.user-avatar:hover .user-name {
  color: #1890ff;
}

.user-name {
  color: #333;
  font-size: 14px;
  transition: color 0.3s ease;
}
</style>
