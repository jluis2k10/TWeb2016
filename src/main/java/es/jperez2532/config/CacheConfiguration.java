package es.jperez2532.config;

import es.jperez2532.components.CacheFilmsKeyGenerator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la caché de la aplicación.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    // Generador de claves para consultas a la tabla FILMS
    @Bean
    KeyGenerator filmsKey() {
        return new CacheFilmsKeyGenerator();
    }

}
