<template>
  <a-layout-footer class="footer">
    <div class="footer-container">
      <div class="footer-left">
        <span class="copyright">©2026 Neal Caffrey 版权所有</span>
        <span class="divider">|</span>
        <span class="icp">京ICP备XXXXXXXX号</span>
      </div>
      <div class="footer-right">
        <a href="#" class="footer-link">
          <MessageOutlined />
          <span>交流社区</span>
        </a>
        <a href="#" class="footer-link">
          <FileTextOutlined />
          <span>使用文档</span>
        </a>
      </div>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
import { MessageOutlined, FileTextOutlined } from '@ant-design/icons-vue'
</script>

<style scoped>
.footer {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 0;
  margin-top: 40px;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}

.divider {
  color: rgba(0, 0, 0, 0.25);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.footer-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #1890ff;
}

@media (max-width: 600px) {
  .footer-container {
    flex-direction: column;
    gap: 16px;
    text-align: center;
    padding: 24px;
  }
}
</style>
