
/**
 * Created by sam on 2018/10/30.
 */

package com.wh.mobile.configuration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableCaching
public class RiosSpringCacheConfig
        implements CachingConfigurer {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private KeyGenerator keyGenerator;

    @Override
    public CacheManager cacheManager() {
        return cacheManager;
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return keyGenerator;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RiosCacheErrorHandler();
    }
}

