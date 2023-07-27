package com.unicam.chorchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ChorchainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChorchainServiceApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost").allowedOrigins("http://localhost:81").allowedOrigins("http://localhost:82").allowedOrigins("http://localhost:83");
            }
        };
    }
}
