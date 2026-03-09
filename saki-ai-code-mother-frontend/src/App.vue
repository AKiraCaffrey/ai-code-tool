<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

const isStandalonePage = computed(() => {
  return route.path === '/post/create' || route.path.startsWith('/post/edit/')
})
</script>

<template>
  <BasicLayout v-if="!isStandalonePage" />
  <router-view v-else />
</template>

<style>
/* 全局样式优化 */
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell',
    'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  overflow-x: hidden;
}

html {
  overflow-x: hidden;
}
</style>
