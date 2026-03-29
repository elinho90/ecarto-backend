package com.gs2e.stage_eranove_academy.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    // La configuration par défaut de Spring Boot pour Redis fonctionne out-of-the-box
    // grâce à spring-boot-starter-data-redis et aux properties définies.
}
