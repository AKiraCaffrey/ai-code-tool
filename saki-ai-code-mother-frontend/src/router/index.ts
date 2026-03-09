import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserCenterPage from '@/pages/user/UserCenterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import AppManagePage from '@/pages/admin/AppManagePage.vue'
import PostManagePage from '@/pages/admin/PostManagePage.vue'
import CommunityPage from '@/pages/community/CommunityPage.vue'
import PostCreatePage from '@/pages/post/PostCreatePage.vue'
import PostDetailPage from '@/pages/post/PostDetailPage.vue'
import PostEditPage from '@/pages/post/PostEditPage.vue'
import AppChatPage from '@/pages/app/AppChatPage.vue'
import AppEditPage from '@/pages/app/AppEditPage.vue'
import ChatManagePage from "@/pages/admin/ChatManagePage.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/community',
      name: '交流社区',
      component: CommunityPage,
    },
    {
      path: '/post/create',
      name: '发帖',
      component: PostCreatePage,
    },
    {
      path: '/post/:id',
      name: '帖子详情',
      component: PostDetailPage,
    },
    {
      path: '/post/edit/:id',
      name: '编辑帖子',
      component: PostEditPage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterPage,
    },
    {
      path: '/user/center',
      name: '个人中心',
      component: UserCenterPage,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManagePage,
    },
    {
      path: '/admin/appManage',
      name: '应用管理',
      component: AppManagePage,
    },
    {
      path: '/admin/postManage',
      name: '帖子管理',
      component: PostManagePage,
    },
    {
      path: '/admin/chatManage',
      name: '对话管理',
      component: ChatManagePage,
    },
    {
      path: '/app/chat/:id',
      name: '应用对话',
      component: AppChatPage,
    },
    {
      path: '/app/edit/:id',
      name: '编辑应用',
      component: AppEditPage,
    },
  ],
})

export default router
