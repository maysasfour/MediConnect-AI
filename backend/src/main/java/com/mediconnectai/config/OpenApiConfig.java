package com.mediconnectai.config;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    @Bean("apiMetadata") public Map<String, String> apiMetadata() {
        return Map.of("title", "MediConnect AI API", "version", "v1", "description", "Clinical operations demo API");
    }
}
