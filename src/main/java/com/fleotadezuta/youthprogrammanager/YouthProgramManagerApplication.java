package com.fleotadezuta.youthprogrammanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
public class YouthProgramManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouthProgramManagerApplication.class, args);
    }

}
