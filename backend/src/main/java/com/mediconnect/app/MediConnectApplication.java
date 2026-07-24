package com.mediconnect.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the MediConnect AI modular monolith.
 *
 * <p>Modules live under {@code com.mediconnect.<module>} with a clear dependency
 * direction: feature modules depend on {@code shared}, never on each other's
 * internals. Cross-module coordination happens through published DTOs and
 * application services only.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class MediConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediConnectApplication.class, args);
    }
}
