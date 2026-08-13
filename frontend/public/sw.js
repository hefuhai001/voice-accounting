/* 语音记账 PWA Service Worker
 * 策略：导航/API 网络优先（保证最新数据与页面），静态资源 cache-first（加速加载）
 */
const CACHE_NAME = 'va-cache-v1'
const PRECACHE_URLS = ['/', '/manifest.json']

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches
      .open(CACHE_NAME)
      .then((cache) => cache.addAll(PRECACHE_URLS))
      .then(() => self.skipWaiting())
  )
})

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((keys) => Promise.all(keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key))))
      .then(() => self.clients.claim())
  )
})

self.addEventListener('fetch', (event) => {
  const { request } = event
  if (request.method !== 'GET') return
  const url = new URL(request.url)
  // API 请求一律走网络，不做缓存
  if (url.origin === self.location.origin && url.pathname.startsWith('/api/')) return

  // 导航请求：网络优先，离线时回退缓存
  if (request.mode === 'navigate') {
    event.respondWith(
      fetch(request)
        .then((response) => {
          const copy = response.clone()
          caches.open(CACHE_NAME).then((cache) => cache.put(request, copy))
          return response
        })
        .catch(() => caches.match(request).then((hit) => hit || caches.match('/')))
    )
    return
  }

  // 静态资源：cache-first，后台更新
  event.respondWith(
    caches.match(request).then((hit) => {
      const network = fetch(request)
        .then((response) => {
          if (response && response.status === 200) {
            const copy = response.clone()
            caches.open(CACHE_NAME).then((cache) => cache.put(request, copy))
          }
          return response
        })
        .catch(() => hit)
      return hit || network
    })
  )
})
