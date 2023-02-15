package com.dev.museummate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MuseumMateApplication {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
        + "classpath:application.yml";
    public static void main(String[] args) {
        new SpringApplicationBuilder(MuseumMateApplication.class)
            .properties(APPLICATION_LOCATIONS)
            .run(args);
    }

}