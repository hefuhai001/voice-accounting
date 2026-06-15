<template>
  <a-config-provider :locale="zhCN" :theme="antdTheme">
    <router-view />
  </a-config-provider>
</template>

<script setup>
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const antdTheme = {
  token: {
    // 主色 - 对应 primary-container (#ff6b35)
    colorPrimary: '#ff6b35',
    // 链接色
    colorLink: '#ff6b35',
    colorLinkHover: '#ab3500',
    colorLinkActive: '#832600',
    // 成功色 - 对应 success-green
    colorSuccess: '#34C759',
    // 警告色 - 对应 secondary-container
    colorWarning: '#fe9824',
    // 错误色 - 对应 danger-red
    colorError: '#FF3B30',
    // 信息色 - 对应 tertiary
    colorInfo: '#00677e',
    // 文字色 - 对应 on-surface
    colorText: '#1b1b1d',
    colorTextSecondary: '#594139',
    colorTextTertiary: '#8d7168',
    colorTextQuaternary: 'rgba(27, 27, 29, 0.25)',
    // 背景色 - 对应 surface 系列
    colorBgContainer: '#ffffff',
    colorBgElevated: '#ffffff',
    colorBgLayout: '#fcf8fb',
    // 边框色 - 对应 outline-variant
    colorBorder: '#e1bfb5',
    colorBorderSecondary: '#e4e2e4',
    // 填充色
    colorFill: 'rgba(171, 53, 0, 0.08)',
    colorFillSecondary: 'rgba(171, 53, 0, 0.04)',
    colorFillTertiary: '#f6f3f5',
    colorFillQuaternary: '#f0edef',
    // 圆角
    borderRadius: 12,
    borderRadiusLG: 16,
    borderRadiusSM: 8,
    borderRadiusXS: 4,
    // 字体
    fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
    fontSize: 14,
    fontSizeHeading1: 32,
    fontSizeHeading2: 24,
    fontSizeHeading3: 20,
    fontSizeHeading4: 16,
    // 间距
    padding: 16,
    paddingLG: 24,
    paddingSM: 12,
    paddingXS: 8,
    // 控件高度
    controlHeight: 40,
    controlHeightLG: 48,
    controlHeightSM: 32,
    // 阴影
    boxShadow: '0 4px 20px rgba(0, 0, 0, 0.04)',
    boxShadowSecondary: '0 8px 24px rgba(0, 0, 0, 0.08)',
  },
  components: {
    Button: {
      primaryShadow: '0 4px 12px rgba(255, 107, 53, 0.3)',
      defaultBorderColor: '#e1bfb5',
      defaultColor: '#1b1b1d',
    },
    Input: {
      activeBorderColor: '#ff6b35',
      hoverBorderColor: '#e1bfb5',
      colorBorder: '#e4e2e4',
    },
    Select: {
      colorBorder: '#e4e2e4',
      optionSelectedBg: 'rgba(255, 107, 53, 0.08)',
    },
    Modal: {
      borderRadiusLG: 20,
    },
    Card: {
      borderRadiusLG: 20,
    },
    Badge: {
      colorText: '#594139',
    },
    DatePicker: {
      colorBorder: '#e4e2e4',
      activeBorderColor: '#ff6b35',
    },
    Tabs: {
      inkBarColor: '#ff6b35',
      itemActiveColor: '#ab3500',
      itemSelectedColor: '#ff6b35',
      itemHoverColor: '#ab3500',
    },
  },
}
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /* 全屏模式安全区域适配 */
  padding: env(safe-area-inset-top) env(safe-area-inset-right) env(safe-area-inset-bottom) env(safe-area-inset-left);
}

#app {
  min-height: 100vh;
}
</style>
