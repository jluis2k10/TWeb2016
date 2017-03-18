package es.jperez2532.config;

import es.jperez2532.components.CacheFilmsKeyGenerator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    CacheManager cacheManager() {
        ConcurrentMapCacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
        return concurrentMapCacheManager;
    }

    @Bean
    KeyGenerator filmsKey() {
        return new CacheFilmsKeyGenerator();
    }

}
