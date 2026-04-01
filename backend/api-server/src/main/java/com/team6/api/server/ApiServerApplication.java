package com.team6.api.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        // 모든 모듈의 com.team6 하위 빈들을 다 찾아내서 등록합니다.
        scanBasePackages = "com.team6"
)
@EnableJpaRepositories(basePackages = "com.team6")
@EntityScan(basePackages = "com.team6")
public class ApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiServerApplication.class, args);
    }

}
