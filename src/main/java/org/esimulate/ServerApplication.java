package org.esimulate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
@EnableScheduling
public class ServerApplication {
    public static void main(String[] args) {
        log.info("Starting ServerApplication main");
        SpringApplication.run(ServerApplication.class, args);
        log.info("ServerApplication started main");

    }
}