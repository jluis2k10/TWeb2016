package es.jperez2532.components;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Generador de claves para la colección de elementos en la caché de Películas.
 */
@Component
public class CacheFilmsKeyGenerator implements KeyGenerator {
    /**
     * {@inheritDoc}
     * <p>
     * Genera una clave personalizada con el nombre del método desde donde se invoca
     * la generación de la caché y los parámetros de dicho método.
     * @param target clase desde donde se invoca la generación de la caché
     * @param method método desde el que se invoca la generación de la caché
     * @param params parámetros del método desde el cual se invoca la generación de la caché
     * @return clave generada
     */
    @Override
    public Object generate(Object target, Method method, Object... params) {
        SimpleKey simpleKey = new SimpleKey(method.getName(), params);
        return simpleKey;
    }
}
