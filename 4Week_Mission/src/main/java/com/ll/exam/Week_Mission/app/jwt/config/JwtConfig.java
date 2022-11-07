package com.ll.exam.Week_Mission.app.jwt.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class JwtConfig {
    @Value("${spring.custom.jwt.secret-key}")
    private String secretKeyPlain;

    @PostConstruct
    protected SecretKey init() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    @Bean
    public SecretKey jwtSecretKey() {
        return init();
    }
}
