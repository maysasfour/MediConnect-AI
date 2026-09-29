package com.mediconnectai.config;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableConfigurationProperties(JwtConfig.class)
public class SecurityConfig {
    @Bean public Clock applicationClock() { return Clock.systemUTC(); }
}
