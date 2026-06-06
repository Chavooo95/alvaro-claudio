package com.boardgames.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.boardgames")
@EntityScan(basePackages = "com.boardgames")
@EnableJpaRepositories(basePackages = "com.boardgames")
public class BoardGamesApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoardGamesApplication.class, args);
    }
}
