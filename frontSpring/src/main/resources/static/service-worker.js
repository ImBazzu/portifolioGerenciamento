const CACHE_NAME = 'secretaria-transporte-static-v4';
const STATIC_PREFIXES = ['/css/', '/js/', '/icons/'];
const STATIC_FILES = [
    '/css/principal.css',
    '/js/base.js',
    '/manifest.webmanifest',
    '/icons/icon-192.svg',
    '/icons/icon-512.svg'
];

self.addEventListener('install', event => {
    event.waitUntil(caches.open(CACHE_NAME).then(cache => cache.addAll(STATIC_FILES)));
    self.skipWaiting();
});

self.addEventListener('activate', event => {
    event.waitUntil(
        caches.keys().then(keys => Promise.all(
            keys.filter(key => key.startsWith('secretaria-transporte-') && key !== CACHE_NAME)
                .map(key => caches.delete(key))
        ))
    );
    self.clients.claim();
});

self.addEventListener('fetch', event => {
    const requestUrl = new URL(event.request.url);
    if (event.request.method !== 'GET' || requestUrl.origin !== self.location.origin) return;
    if (requestUrl.pathname !== '/manifest.webmanifest'
            && !STATIC_PREFIXES.some(prefix => requestUrl.pathname.startsWith(prefix))) return;

    event.respondWith(fetch(event.request).then(response => {
            if (response.ok) {
                const copy = response.clone();
                caches.open(CACHE_NAME).then(cache => cache.put(event.request, copy));
            }
            return response;
        }).catch(async error => {
            const cached = await caches.match(event.request);
            if (cached) return cached;
            throw error;
        }));
});
