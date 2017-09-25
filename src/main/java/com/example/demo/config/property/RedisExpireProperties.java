package com.example.demo.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;


@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisExpireProperties {
    private HashMap<String,Long> expireMap;
}
