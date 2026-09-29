package com.mediconnectai.config;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RedisConfig {
    @Bean("applicationCache") public Map<String, Object> applicationCache() { return new ConcurrentHashMap<>(); }
}
