package es.jperez2532.components;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class CacheFilmsKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        SimpleKey simpleKey = new SimpleKey(method.getName(), params);
        return simpleKey;
    }
}
