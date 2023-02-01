package com.dev.museummate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableJpaAuditing
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class MuseumMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuseumMateApplication.class, args);
    }

}
