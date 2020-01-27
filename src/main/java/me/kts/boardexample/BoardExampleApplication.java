package me.kts.boardexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class BoardExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardExampleApplication.class, args);
    }

}
