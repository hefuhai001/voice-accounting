<script>
export default {
  onLaunch: function () {
    console.log('App Launch')
  },
  onShow: function () {
    console.log('App Show')
  },
  onHide: function () {
    console.log('App Hide')
  },
}
</script>

<style>
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  html, body, page, uni-page, .uni-page-body {
    font-family: 'Hanken Grotesk', 'Inter', system-ui, -apple-system, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-rendering: optimizeLegibility;
  }
  body {
    background: linear-gradient(135deg, #f9f9fc 0%, #fff0eb 100%);
  }
}

@layer components {
  .glass-card {
    background: rgba(255, 255, 255, 0.4);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.5);
  }
  .glass-panel {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 1);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  .glass-panel:active {
    transform: scale(0.98);
  }
  .rounded-3xl {
    border-radius: 2rem !important;
  }
  .rounded-2xl {
    border-radius: 1.5rem !important;
  }
  /* 确保input/select不超出容器 */
  input, textarea, select {
    box-sizing: border-box;
  }
  .no-scrollbar::-webkit-scrollbar {
    display: none;
  }
  .no-scrollbar {
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
}

/* Material Symbols Outlined 图标字体 */
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;
  line-height: 1;
  letter-spacing: normal;
  text-transform: none;
  display: inline-block;
  white-space: nowrap;
  word-wrap: normal;
  direction: ltr;
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  -webkit-font-smoothing: antialiased;
}

/* 隐藏原生 tabBar（custom:true 在 H5 下仍会渲染） */
.uni-tabbar,
.uni-tabbar-bottom {
  display: none !important;
}
</style>
