package com.wh.mobile.configuration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class RiosCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        log.error("redis get error：",e);
        System.out.println("redis get error");
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        log.error("redis put error：",e);
        System.out.println("redis put error:"+ e);

    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        log.error("redis evict error：",e);
        System.out.println("redis evict error");
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.error("redis clear error：",e);
        System.out.println("redis clear error");
    }
}
